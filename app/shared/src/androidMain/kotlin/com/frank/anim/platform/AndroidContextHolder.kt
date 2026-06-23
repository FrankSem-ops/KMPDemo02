package com.frank.anim.platform

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context

@SuppressLint("StaticFieldLeak")
object AndroidContextHolder {
    lateinit var context: Context
        private set

    private var activity: Activity? = null

    fun init(context: Context) {
        this.context = context.applicationContext
    }

    fun initActivity(activity: Activity) {
        this.activity = activity
    }

    fun getActivity(): Activity? = activity

    val isInitialized: Boolean
        get() = try {
            context
            true
        } catch (_: UninitializedPropertyAccessException) {
            false
        }
}
