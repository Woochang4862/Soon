package com.lusle.android.soon.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.calligraphy3.CalligraphyConfig
import com.lusle.android.soon.R
import com.lusle.android.soon.util.TransformationCompat
import com.skydoves.transformationlayout.TransformationLayout
import com.skydoves.transformationlayout.onTransformationEndContainer
import io.github.inflationx.viewpump.ViewPumpContextWrapper

open class BaseActivity : AppCompatActivity() {
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
        intent?.getParcelableExtra<TransformationLayout.Params>(TransformationCompat.activityTransitionName)?.let{onTransformationEndContainer(it)}
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}