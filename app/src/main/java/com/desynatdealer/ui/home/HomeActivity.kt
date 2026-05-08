package com.desynatdealer.ui.home

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.desynatdealer.R
import com.desynatdealer.adapter.BottomNavAdapter
import com.desynatdealer.base.BaseActivity
import com.desynatdealer.controller.ApiResult
import com.desynatdealer.data.view_model.HomeViewModel
import com.desynatdealer.databinding.ActivityHomeBinding
import com.desynatdealer.utils.CommonMethod
import com.desynatdealer.utils.Constants
import com.desynatdealer.utils.ProgressDialogUtils
import com.desynatdealer.utils.SharedPreference
import com.desynatdealer.utils.showToast
import com.myfatoorah.sdk.views.MFSDK
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity() {
    private var openScreen: String?=""
    private var binding: ActivityHomeBinding? =null
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var controller: NavController
    var bottonNavItemList = ArrayList<BottomNavItem>()
    private var bottomAdapter : BottomNavAdapter? =null
    lateinit var navGraph: NavGraph

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonMethod.setLocale(this, Constants.language)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightNavigationBars = false

        MFSDK.setUpActionBar(isShowToolBar = false)
        initView()
        initControl()
        observer()
        if (SharedPreference.get(this).userType=="1"){
            SharedPreference.get(this).isSubscription = true
            getProfile()
        }
        else{
            getSubscription()
        }

    }

    override fun initView() {

        CommonMethod.detectGestureMode(binding?.root!!,binding?.viewBottom!!)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        controller = navHostFragment.navController


     /*   val navGraph = controller.navInflater.inflate(R.navigation.home_navigation_graph)

        navGraph.setStartDestination(
            if (SharedPreference.get(this).isSubscription) R.id.home
            else R.id.subscription
        )

        controller.graph = navGraph
*/

        openScreen = intent.getStringExtra("open_screen")


        bottonNavItemList.clear()
        bottonNavItemList.add(BottomNavItem(getString(R.string.home),R.drawable.ic_home,R.drawable.ic_selected_home))
        bottonNavItemList.add(BottomNavItem(getString(R.string.products),R.drawable.products_ic,R.drawable.ic_selected_products))
        bottonNavItemList.add(BottomNavItem(getString(R.string.streaming),R.drawable.ic_streaming,R.drawable.ic_streaming))
        bottonNavItemList.add(BottomNavItem(getString(R.string.orders),R.drawable.ic_orders,R.drawable.ic_selected_orders))
        bottonNavItemList.add(BottomNavItem(getString(R.string.payment),R.drawable.payments_ic,R.drawable.ic_selected_payment))

        setBottomAdapter()

        val bottomNavDestinations = setOf(
            R.id.home,
            R.id.productManagement,
            R.id.orderManagement,
            R.id.event,
          //  R.id.payment,
        )


        controller.addOnDestinationChangedListener { _, destination, _ ->

            if (destination.id in bottomNavDestinations && SharedPreference.get(this).userType=="0") {
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
                R.id.productManagement -> {
                    bottomAdapter?.setSelectedItem(1)
                }
                R.id.orderManagement -> {
                    bottomAdapter?.setSelectedItem(3)
                }
                R.id.liveStreaming -> {
                    bottomAdapter?.setSelectedItem(2)
                }
//                R.id.payment -> {
//                    bottomAdapter?.setSelectedItem(4)
//                }
            }
        }
    }

    private fun getProfile() {
         val token = if (SharedPreference.get(this).userType=="1") SharedPreference.get(this).staffToken else SharedPreference.get(this).accessToken
        viewModel.getSellerInfo("Bearer ${token}")
    }

    private fun getSubscription() {

            viewModel.getSubscriptionHomePlan()

    }
    override fun initControl() {
    }

    override fun observer() {
        viewModel.sellerInfoResponse.observe(this){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(this)
                }
                is ApiResult.Success -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    result.data?.data?.let { SharedPreference.get(this).saveUser(it) }

                    if (!::navGraph.isInitialized) {
                        navGraph = controller.navInflater.inflate(R.navigation.home_navigation_graph)
                    }
                    navGraph.setStartDestination(
                        if (result.data?.data?.subcriptionPlan?.isEmpty() == true&&SharedPreference.get(this).isSubscription==false){
                            R.id.subscription
                        }
                        else{
                                when (openScreen) {
                                    "profile" -> {
                                      R.id.profile
                                    }
                                    "home" -> {
                                       R.id.home
                                    }
                                    else ->{
                                      R.id.home
                                    }
                                }
                        }
                    )
                    if (result.data?.data?.subcriptionPlan?.isEmpty() == true&&SharedPreference.get(this).isSubscription==false){

                    }
                    else{
                        SharedPreference.get(this).isSubscription = true

                    }

                    controller.graph = navGraph
                    binding?.viewWhite?.isVisible = false
                    viewModel.sellerInfoResponse.value = null
                }
                is ApiResult.Error -> {
                    binding?.viewWhite?.isVisible = false
                    ProgressDialogUtils.getInstance().hideProgress()
                    showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.sellerInfoResponse.value = null
                }
                else ->{

                }
            }
        }

        viewModel.subscriptionPlanHomeResponse.observe(this){result->
            when(result){
                ApiResult.Loading -> {
                    ProgressDialogUtils.getInstance().showProgress(this)
                }
                is ApiResult.Success -> {
                    if (result.data?.data?.isEmpty() == true){
                        SharedPreference.get(this).isSubscription = true
                    }
                    viewModel.subscriptionPlanHomeResponse.value = null
                    getProfile()
                }
                is ApiResult.Error -> {
                    binding?.viewWhite?.isVisible = false
                    Log.e("error",result.message.toString())
                    SharedPreference.get(this).isSubscription = true
                    getProfile()
                    ProgressDialogUtils.getInstance().hideProgress()
                   // showToast(result.message ?: getString(R.string.generic_error))
                    viewModel.subscriptionPlanHomeResponse.value = null
                }
                else ->{

                }
            }
        }
    }

    fun setBottomAdapter(){
        bottomAdapter = BottomNavAdapter(this,bottonNavItemList, object :BottomNavAdapter.BottomNavItemClick{
            override fun onBottomNavItemClick(position: Int) {
                when(position) {
                    0 -> {
                        ProgressDialogUtils.getInstance().hideProgress()
                        controller.navigate(R.id.home)
                    }
                    1 -> {
                        ProgressDialogUtils.getInstance().hideProgress()
                        controller.navigate(R.id.productManagement)
                    }
                    2 -> {
                        ProgressDialogUtils.getInstance().hideProgress()
                        controller.navigate(R.id.event)
                    }
                    3 -> {
                        ProgressDialogUtils.getInstance().hideProgress()
                        controller.navigate(R.id.orderManagement)
                    }
//                    4 -> {
//                        ProgressDialogUtils.getInstance().hideProgress()
//                        controller.navigate(R.id.payment)
//                    }
                }

            }


        })
        binding?.rvBottomNav?.adapter = bottomAdapter

    }

    override fun onResume() {
        super.onResume()

//        SharedPreference.get(this).badgeCount = 0
//        ShortcutBadger.removeCount(this)
        //   NotificationManagerCompat.from(this).cancelAll()
    }

    fun updateAdapterCount(orderCount: Int){
        bottomAdapter?.setCartCount(orderCount)
    }

}

data class BottomNavItem(
    var name : String,
    var image: Int,
    var selectedImage: Int,
)