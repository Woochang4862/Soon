package com.lusle.android.soon.View.Main.Setting.AppInfo

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.lusle.android.soon.R

class AppInfoFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_app_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appVersion = view.findViewById<TextView>(R.id.app_version)
        try {
            val pInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            val version = pInfo.versionName
            appVersion.text = "버전 $version"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            appVersion.visibility = View.GONE
        }
    }
}