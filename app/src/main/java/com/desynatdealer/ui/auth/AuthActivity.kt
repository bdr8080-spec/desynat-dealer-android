package com.desynatdealer.ui.auth

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.desynatdealer.R
import com.desynatdealer.base.BaseActivity
import com.desynatdealer.databinding.ActivityAuthBinding
import com.desynatdealer.utils.CommonMethod
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity() {
    private lateinit var controller: NavController
    private var binding: ActivityAuthBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView()
        initControl()
        observer()
    }

    override fun initView() {
        CommonMethod.detectGestureMode(binding?.root!!,binding?.viewBottom!!)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        controller = navHostFragment.navController
        controller.graph = controller.navInflater.inflate(R.navigation.auth_navigation_graph)
    }

    override fun initControl() {
    }

    override fun observer() {
    }
}