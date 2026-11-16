package com.prince.findthemischief

import android.app.Application

/** Created once when the app process starts, before any screen. Owns the [AppContainer]. */
class MischiefApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
