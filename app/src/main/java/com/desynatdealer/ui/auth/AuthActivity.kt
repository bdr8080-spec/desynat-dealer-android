package com.desynatdealer.ui.auth

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.desynatdealer.R
import com.desynatdealer.base.BaseActivity
import com.desynatdealer.databinding.ActivityAuthBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity() {
    private lateinit var controller: NavController
    private var binding: ActivityAuthBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightNavigationBars = false

        initView()
        initControl()
        observer()
    }

    override fun initView() {
        CommonMethod.detectGestureMode(binding?.root!!, binding?.viewBottom!!)

       /* val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        controller = navHostFragment.navController
        controller.graph = controller.navInflater.inflate(R.navigation.auth_navigation_graph)*/
        val fromLanguage = intent.getStringExtra("open_screen")
        val destination  =  if (fromLanguage != null) {

            if (fromLanguage == "profile") {
                R.id.login
            }  else {

                val savedLanguage = SharedPreference.get(this).language

                if (Constants.isLogout) {
                    R.id.login
                } else if (savedLanguage.isEmpty()) {
                    R.id.language
                } else {
                    R.id.login
                }
            }

        }
        else {

            val savedLanguage = SharedPreference.get(this).language

            if (Constants.isLogout) {
                R.id.login
            } else if (savedLanguage.isEmpty()) {
                R.id.language
            } else {
                R.id.login
            }
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        controller = navHostFragment.navController

        val navGraph = controller.navInflater.inflate(R.navigation.auth_navigation_graph)

        navGraph.setStartDestination(
            destination
        )

        controller.graph = navGraph
    }

    override fun initControl() {
    }

    override fun observer() {
    }
}