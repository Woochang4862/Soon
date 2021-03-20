package com.lusle.android.soon.View.Main.Setting.AppInfo;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lusle.android.soon.R;
import com.lusle.android.soon.View.BaseActivity;

public class AppInfoActivity extends BaseActivity {

    private TextView appVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        appVersion = findViewById(R.id.app_version);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            appVersion.setText("버전 "+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            appVersion.setVisibility(View.GONE);
        }
    }
}
