package com.lusle.android.soon.view.main.company

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.lusle.android.soon.model.api.CompanyApi
import com.lusle.android.soon.model.schema.Company
import com.lusle.android.soon.model.source.FavoriteCompanyRepository
import com.lusle.android.soon.view.main.setting.company_alarm.TokenNotFoundException
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CompanyViewModel(private val favoriteCompanyRepository: FavoriteCompanyRepository) :
    ViewModel() {

    private val _favoriteCompanyLiveData: MutableLiveData<ArrayList<Company>> = MutableLiveData()
    val favoriteCompanyLiveData: LiveData<ArrayList<Company>> = _favoriteCompanyLiveData
    private var startList: ArrayList<Company>? = null

    private val _saveButtonState: MutableLiveData<Boolean> = MutableLiveData(false)
    val saveButtonState: LiveData<Boolean> = _saveButtonState

    private val companyApi = CompanyApi.create()
    private var token: String? = null

    fun loadFavoriteCompany() {
        _favoriteCompanyLiveData.value = favoriteCompanyRepository.loadFavoriteCompany()
        startList = favoriteCompanyLiveData.value?.clone() as ArrayList<Company>
    }

    fun saveCompaniesToLocal() {
        favoriteCompanyRepository.saveCompanies(favoriteCompanyLiveData.value ?: arrayListOf())
        startList = favoriteCompanyLiveData.value?.clone() as ArrayList<Company>
    }

    fun checkSaveButtonVisibility() {
        Log.d(TAG, "checkSaveButtonVisibility: ${favoriteCompanyLiveData.value}, ${startList}")
        _saveButtonState.value = favoriteCompanyLiveData.value != startList
    }

    suspend fun removeCompaniesAlarm() {
        withContext(Dispatchers.IO) {
            try{
                token = token ?: run { FirebaseMessaging.getInstance().token.await() }
            } catch (e:Exception){
                e.printStackTrace()
                throw TokenNotFoundException(e)
            }
            val listToRemove = startList?.filterNot { favoriteCompanyLiveData.value?.contains(it) ?: true } ?: arrayListOf()
            for (company in listToRemove) {
                val body = HashMap<String, String>()
                body["company_id"] = company.id.toString()
                body["token"] = token!!
                companyApi.removeCompanyAlarm(body).blockingGet()
            }
        }
    }


    companion object {
        val TAG = CompanyViewModel::class.java.simpleName
    }

}