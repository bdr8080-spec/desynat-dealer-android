package com.desynatdealer.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import com.desynatdealer.base.BaseActivity
import com.desynatdealer.databinding.ActivitySplashBinding
import com.desynatdealer.ui.auth.AuthActivity
import com.desynatdealer.ui.home.HomeActivity
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.SharedPreference

class SplashActivity : BaseActivity() {
    private var binding: ActivitySplashBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initView()
        initControl()
        observer()
    }

    override fun initView() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (SharedPreference.get(this).accessToken.isNotEmpty()&&SharedPreference.get(this).isSubscription) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
            else{
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }

        },500)
    }

    override fun initControl() {
    }

    override fun observer() {
    }
}