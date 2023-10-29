package com.lusle.android.soon.adapter.Listener

import android.widget.CompoundButton
import com.lusle.android.soon.Model.Schema.Company

interface OnCheckedChangeListener {
    fun onCheckedChangeListener(view: CompoundButton, isChecked:Boolean, company:Company)
}