package com.lusle.android.soon.model.source

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lusle.android.soon.model.schema.Company

class FavoriteCompanyRepository(context: Context) {

    private val pref = context.getSharedPreferences("pref", MODE_PRIVATE)

    fun addCompany(company: Company){
        val favoriteCompany = loadFavoriteCompany() ?: arrayListOf()
        val listToAdd: ArrayList<Company> = if (favoriteCompany.contains(company)){
            Log.w(TAG, "addCompany : Warning! ${company.name}(${company.id}) is already exist.", )
            favoriteCompany
        } else {
            ArrayList((favoriteCompany.clone() as ArrayList<Company>).plus(company))
        }
        pref.edit().putString("favorite_company", Gson().toJson(listToAdd)).apply()
    }

    fun removeCompany(company: Company){
        val favoriteCompany = loadFavoriteCompany() ?: arrayListOf()
        val listToAdd: ArrayList<Company> = if (!favoriteCompany.contains(company)){
            Log.w(TAG, "removeCompany : Warning! ${company.name}(${company.id}) is not exist.", )
            favoriteCompany
        } else {
            val tmp = (favoriteCompany.clone() as ArrayList<Company>)
            tmp.remove(company)
            tmp
        }
        pref.edit().putString("favorite_company", Gson().toJson(listToAdd)).apply()
    }

    fun loadFavoriteCompany(): ArrayList<Company>? {
        val type = object : TypeToken<ArrayList<Company>>() {}.type
        return Gson().fromJson(
            pref.getString("favorite_company", "[]"),
            type
        )
    }

    companion object {
        val TAG = FavoriteCompanyRepository::class.java.simpleName
    }
}