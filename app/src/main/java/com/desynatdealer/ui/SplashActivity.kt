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
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.SharedPreference
import kotlin.collections.get

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
        CommonMethod.setLocale(this,SharedPreference.get(this).language)
        Constants.language = SharedPreference.get(this).language
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