package com.lusle.android.soon.view

import android.content.Context
import com.lusle.android.soon.R
import com.skydoves.transformationlayout.TransformationAppCompatActivity
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.viewpump.ViewPumpContextWrapper

open class TransformationBaseActivity : TransformationAppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/NanumBarunGothic.otf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}