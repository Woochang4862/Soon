package com.lusle.android.soon.adapter.listener

import android.widget.CompoundButton
import com.lusle.android.soon.model.schema.Company

interface OnCheckedChangeListener {
    fun onCheckedChangeListener(view: CompoundButton, isChecked:Boolean, company:Company)
}