package dev.notrobots.timeline

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        PACKAGE_NAME = packageName
        VERSION = packageManager.getPackageInfo(packageName, 0).versionName
    }

    companion object {
        var VERSION = ""
        var PACKAGE_NAME = ""
    }
}