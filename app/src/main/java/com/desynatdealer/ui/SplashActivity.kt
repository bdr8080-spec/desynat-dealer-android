package com.desynatdealer.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.desynatdealer.R
import com.desynatdealer.base.BaseActivity
import com.desynatdealer.databinding.ActivitySplashBinding
import com.desynatdealer.ui.auth.AuthActivity
import com.desynatdealer.ui.home.HomeActivity
import com.desynatdealer.utils.SharedPreference

class SplashActivity : BaseActivity() {
    private var binding: ActivitySplashBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightNavigationBars = false

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