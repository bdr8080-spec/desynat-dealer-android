package com.desynatdealer.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.desynatdealer.utils.SharedPreference

abstract class BaseFragment : Fragment() {

    lateinit var sharedPreference: SharedPreference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        sharedPreference = SharedPreference.get(requireActivity())
    }

    abstract fun initView()
    abstract fun initControl()
    abstract fun observe()


}