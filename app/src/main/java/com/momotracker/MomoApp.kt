package com.momotracker

import android.app.Application
import com.momotracker.ai.CategoryClassifier

class MomoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CategoryClassifier.init(this)
    }
}
