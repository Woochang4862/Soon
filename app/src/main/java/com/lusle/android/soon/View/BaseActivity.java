package com.lusle.android.soon.View;

import android.content.Context;

import com.lusle.android.soon.R;

import androidx.appcompat.app.AppCompatActivity;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/NanumBarunGothic.otf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
