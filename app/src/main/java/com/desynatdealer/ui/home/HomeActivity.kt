package com.desynatdealer.ui.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.desynatdealer.R
import com.desynatdealer.adapter.BottomNavAdapter
import com.desynatdealer.base.BaseActivity
import com.desynatdealer.databinding.ActivityHomeBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity() {
    private var binding: ActivityHomeBinding? =null
    private lateinit var controller: NavController
    var bottonNavItemList = ArrayList<BottomNavItem>()
    private var bottomAdapter : BottomNavAdapter? =null
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

        CommonMethod.detectGestureMode(binding?.root!!,binding?.viewBottom!!)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        controller = navHostFragment.navController


        val navGraph = controller.navInflater.inflate(R.navigation.home_navigation_graph)

        navGraph.setStartDestination(
            if (SharedPreference.get(this).isSubscription) R.id.home
            else R.id.subscription
        )

        controller.graph = navGraph

        bottonNavItemList.clear()
        bottonNavItemList.add(BottomNavItem("Home",R.drawable.ic_home,R.drawable.ic_selected_home))
        bottonNavItemList.add(BottomNavItem("Products",R.drawable.products_ic,R.drawable.ic_selected_products))
        bottonNavItemList.add(BottomNavItem("Streaming",R.drawable.ic_streaming,R.drawable.ic_streaming))
        bottonNavItemList.add(BottomNavItem("Orders",R.drawable.ic_orders,R.drawable.ic_selected_orders))
        bottonNavItemList.add(BottomNavItem("Payment",R.drawable.payments_ic,R.drawable.ic_selected_payment))

        setBottomAdapter()

        val bottomNavDestinations = setOf(
            R.id.home,
           /* R.id.catalog,
            R.id.myBag,
            R.id.profile,*/
        )


        controller.addOnDestinationChangedListener { _, destination, _ ->

            if (destination.id in bottomNavDestinations) {
                binding?.rvBottomNav?.isVisible = true
                binding?.viewBackground?.isVisible = true
            } else {
                binding?.rvBottomNav?.isVisible = false
                binding?.viewBackground?.isVisible = false
            }

            when (destination.id) {
                R.id.home -> {
                    bottomAdapter?.setSelectedItem(0)
                }
               /* R.id.catalog -> {
                    bottomAdapter?.setSelectedItem(1)
                }
                R.id.myBag -> {
                    bottomAdapter?.setSelectedItem(3)
                }
                R.id.profile -> {
                    bottomAdapter?.setSelectedItem(4)
                }*/
            }
        }

    }

    override fun initControl() {
    }

    override fun observer() {
    }

    fun setBottomAdapter(){
        bottomAdapter = BottomNavAdapter(this,bottonNavItemList, object :BottomNavAdapter.BottomNavItemClick{
            override fun onBottomNavItemClick(position: Int) {
                when(position) {
                    0 -> controller.navigate(R.id.home)
                  /*  1 -> controller.navigate(R.id.catalog)
                    //2 -> controller.navigate(R.id.catalog)
                    3 -> controller.navigate(R.id.myBag)
                    4 -> controller.navigate(R.id.profile)*/
                }

            }


        })
        binding?.rvBottomNav?.adapter = bottomAdapter

    }

}

data class BottomNavItem(
    var name : String,
    var image: Int,
    var selectedImage: Int,
)