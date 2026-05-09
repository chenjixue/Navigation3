package com.example.navigation3

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.example.sync.initializers.Sync
/**
 * [Application] class for NiA
 */
@HiltAndroidApp
class NiaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Sync.initialize(context = this)
    }
}
