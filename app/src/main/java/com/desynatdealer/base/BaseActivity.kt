package com.desynatdealer.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.statusBarTransparentWithWhite

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var sharedPreference: SharedPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        sharedPreference = SharedPreference.get(this)
        statusBarTransparentWithWhite()

    }

    abstract fun initView()
    abstract fun initControl()
    abstract fun observer()


}