package com.example.myapplication

import android.app.Application
import com.example.myapplication.core.initPlatform
import com.example.myapplication.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initPlatform(this)
        startKoin {
            if (BuildConfig.DEBUG) androidLogger()
            androidContext(this@MyApp)
            modules(appModule)
        }
    }
}
