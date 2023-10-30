package com.lusle.android.soon.view.main.setting.company_alarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.lusle.android.soon.model.api.CompanyApi
import com.lusle.android.soon.model.schema.Company
import com.lusle.android.soon.model.source.FavoriteCompanyRepository
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CompanyAlarmSettingViewModel(private val favoriteCompanyRepository: FavoriteCompanyRepository) :
    ViewModel() {

    private val _allIsChecked = MutableLiveData<Boolean>()
    val allIsChecked: LiveData<Boolean> = _allIsChecked

    private val _favoriteCompanyLiveData: MutableLiveData<ArrayList<Company>> = MutableLiveData()
    val favoriteCompanyLiveData: LiveData<ArrayList<Company>> = _favoriteCompanyLiveData

    private val _topics: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val topics: LiveData<ArrayList<String>> = _topics

    private val token = MutableLiveData<String>()

    private val companyApi = CompanyApi.create()

    val subscribeLoadState: MutableLiveData<Boolean> = MutableLiveData(false)
    val companyLoadState: MutableLiveData<Boolean> = MutableLiveData(false)

    suspend fun loadToken() {
        try {
            token.value = withContext(Dispatchers.IO) {
                FirebaseMessaging.getInstance().token.await()!!
            }
        } catch (e: Exception) {
            throw TokenNotFoundException(e)
        }
        if (token.value == null) {
            throw TokenNotFoundException()
        }
    }

    fun loadFavoriteCompany() {
        _favoriteCompanyLiveData.value = favoriteCompanyRepository.loadFavoriteCompany()
    }

    suspend fun checkSubscribedTopics() {
        _topics.value = try {
            withContext(Dispatchers.IO) {
                companyApi.checkSubscribedTopics(token.value!!).subscribeOn(Schedulers.io())
                    .blockingGet().topics
            }
        } catch (e: Exception) {
            throw SubscribedTopicsNotFoundException()
        }
    }

    fun checkAllIsChecked() {
        val listNotSubscribed:List<String> = (_favoriteCompanyLiveData.value?.let {favoriteCompanies->
            favoriteCompanies.map { company: Company ->
                company.id.toString()
            }.filterNot {
                _topics.value?.contains(it) ?: throw SubscribedTopicsNotFoundException()
            }
        } ?: run {
            throw CompanyNotFoundException()
        })

        _allIsChecked.value = listNotSubscribed.isEmpty()
    }

    suspend fun subscribeCompanyAlarm(company: Company) {
        subscribeLoadState.value = true
        val body = HashMap<String, String>()
        body["company_id"] = company.id.toString()
        body["token"] = token.value!!

        withContext(Dispatchers.IO) {
            try {
                companyApi.addCompanyAlarm(body).subscribeOn(Schedulers.io())
                    .blockingGet()
            } catch (e: Exception) {
                throw SubscribeCompanyException(e)
            }

        }

        subscribeLoadState.value = false
    }

    suspend fun unsubscribeCompanyAlarm(company: Company) {
        subscribeLoadState.value = true
        val body = HashMap<String, String>()
        body["company_id"] = company.id.toString()
        body["token"] = token.value!!
        withContext(Dispatchers.IO) {
            try {
                companyApi.removeCompanyAlarm(body).subscribeOn(Schedulers.io())
                    .blockingGet()
            } catch (e:Exception){
                throw UnsubscribeCompanyException(e)
            }
        }

        subscribeLoadState.value = false
    }

    suspend fun setAlarmSwitch(checked: Boolean) {
        subscribeLoadState.value = true
        if (checked) {
            val listToAdd = _favoriteCompanyLiveData.value?.filterNot {
                _topics.value?.contains(it.id.toString()) ?: false
            } ?: arrayListOf()
            for (company in listToAdd.iterator()) {
                subscribeCompanyAlarm(company)
            }
        } else {
            val listToRemove = _favoriteCompanyLiveData.value?.filter {
                _topics.value?.contains(it.id.toString()) ?: true
            } ?: arrayListOf()
            for (company in listToRemove.iterator()) {
                unsubscribeCompanyAlarm(company)
            }
        }
        subscribeLoadState.value = false
    }

}

class SubscribeCompanyException(private val e: Exception) : Exception() {
    override val message: String?
        get() = "제작사를 구독하는 과정에서 문제가 생겼습니다\n${e.stackTrace}"

}

class UnsubscribeCompanyException(private val e: Exception) : Exception() {
    override val message: String?
        get() = "제작사를 구독하는 과정에서 문제가 생겼습니다\n${e.stackTrace}"

}

class CompanyNotFoundException : Exception() {
    override val message: String?
        get() = "즐겨찾기한 제작사 목록이 존재하지 않습니다"
}

class SubscribedTopicsNotFoundException(private val exception: Exception? = null) : Exception(exception) {
    override val message: String
        get() = "구독목록이 존재하지 않습니다\n" + (exception?.stackTrace ?: "")
}

class TokenNotFoundException(private val exception: Exception? = null) : Exception(exception) {
    override val message: String
        get() = "토큰이 존재하지 않습니다\n" + (exception?.stackTrace ?: "")
}