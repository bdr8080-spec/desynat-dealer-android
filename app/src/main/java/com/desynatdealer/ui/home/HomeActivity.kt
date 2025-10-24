package com.desynatdealer.ui.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.desynatdealer.base.BaseActivity
import com.desynatdealer.databinding.ActivityHomeBinding

class HomeActivity : BaseActivity() {
    private var binding: ActivityHomeBinding? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initView()
        initControl()
        observer()
    }

    override fun initView() {


    }

    override fun initControl() {
    }

    override fun observer() {
    }

}
