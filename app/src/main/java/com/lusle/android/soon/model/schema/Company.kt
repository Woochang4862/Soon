package com.lusle.android.soon.model.schema

import java.io.Serializable

data class Company(
        val id: Int,
        val logo_path: String?,
        val name: String
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (other is Company){
            return other.id == id
        }
        return false
    }

    override fun hashCode(): Int {
        return id
    }
}