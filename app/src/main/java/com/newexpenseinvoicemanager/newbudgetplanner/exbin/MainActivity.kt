package com.newexpenseinvoicemanager.newbudgetplanner.exbin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Layout
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.ActivityMainBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var bottomNavigationView: BottomNavigationView? = null
    private var bottomAppBar: BottomAppBar? = null
    private var fab: FloatingActionButton? = null
    private var backPressedTime: Long = 0
    private val backPressedTimeout: Long = 2000 // 2 seconds
    private var isButtonVisible = false
    private var addupdatetView: View? = null
    private var currentFragment: Fragment? = null
    private lateinit var sharedPreferences: SharedPreferences
    private val PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()

        val isConnected = isInternet()
        if (isConnected) {


            sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            if (!isPermissionGranted()) {
                requestPermission()
            }




            getIdofNativeAds()
            val toolbar = getSupportActionBar();
            bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)

            fab = binding.fab
            val CHECK_VALUE = intent.getStringExtra("SELECT_CATEGORY_01")

            if (CHECK_VALUE != null) {
                val args = Bundle()
                args.putString("SELECT_CAT", "001")
                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                val fragment = CategoryListFragment()
                fragment.setArguments(args)
                fragmentTransaction.replace(R.id.fragment_container, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            } else {
                val isConnected = isInternet()
                if (isConnected) {
                    bottomNavigationView?.setOnItemSelectedListener { item ->
                        var fragment: Fragment
                        when (item.itemId) {
                            R.id.navigation_home -> {
                                toolbar?.setTitle("Home")
                                fragment = HomeFragment()
                                loadFragment(fragment)
                                true
                            }
                            R.id.navigation_transection -> {
                                floatButtonHide()
                                toolbar?.setTitle("Transection")
                                fragment = TransectionFragment()
                                loadFragment(fragment)
                                true
                            }
                            R.id.navigation_add -> {
                                floatButtonShow()
                                true
                            }
                            R.id.navigation_budget -> {
                                floatButtonHide()
                                toolbar?.setTitle("Budget")
                                fragment = BudgetFragment()
                                loadFragment(fragment)
                                true
                            }
                            R.id.navigation_more -> {
                                floatButtonHide()
                                toolbar?.setTitle("More")
                                fragment = MoreFragment()
                                loadFragment(fragment)
                                true
                            }
                            else -> false
                        }
                    }
                    if (!(currentFragment is HomeFragment)) {
                        bottomNavigationView?.selectedItemId = R.id.navigation_home
                    }
                } else {
                    showNoInternetDialog()
                }
                if (isConnected) {
                    fab?.setOnClickListener {

//                        Toast.makeText(this, "$currentDate", Toast.LENGTH_SHORT).show()
                        currentFragment =
                            supportFragmentManager.findFragmentById(R.id.fragment_container)
                        if (!(currentFragment is HomeFragment)) {
                            bottomNavigationView?.selectedItemId = R.id.navigation_home
                            val fragment = HomeFragment()
                            loadFragment(fragment)
                        } else {
                            floatButtonShow()
                        }

                    }
                    if (!(currentFragment is HomeFragment)) {
                        bottomNavigationView?.selectedItemId = R.id.navigation_home
                    }
                } else {
                    showNoInternetDialog()
                }

                binding.inc.setOnClickListener {
                    floatButtonHide()
                    loadFragment(IncomeActivity())
                }
                binding.exp.setOnClickListener {
                    floatButtonHide()
                    loadFragment(ExpenseActivity())
                }
            }
        } else {
            showNoInternetDialog()
        }
    }


    private fun loadFragment(fragment: Fragment) {
        var isConnected = isInternet()
        if (isConnected) {
            supportFragmentManager.beginTransaction()
                //add animation code here   .setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        } else {
            showNoInternetDialog()
        }
    }

    private fun floatButtonShow() {
        if (isButtonVisible) {
            binding.exp.visibility = View.GONE
            binding.inc.visibility = View.GONE
        } else {
            binding.exp.visibility = View.VISIBLE
            binding.inc.visibility = View.VISIBLE
        }

        // Toggle the visibility state
        isButtonVisible = !isButtonVisible
    }

    private fun floatButtonHide() {
        binding.exp.visibility = View.GONE
        binding.inc.visibility = View.GONE
    }


    public fun showBottomNavigationView() {
        bottomNavigationView?.setVisibility(View.VISIBLE)
        bottomAppBar?.visibility = View.VISIBLE
        fab?.visibility = View.VISIBLE
    }

    public fun setBottomNavigationAsHome(){
        bottomNavigationView?.selectedItemId = R.id.navigation_home
    }

    public fun hideBottomNavigationView() {
        bottomNavigationView?.setVisibility(View.GONE)
        bottomAppBar?.visibility = View.GONE
        fab?.visibility = View.GONE


    }

//    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
//        super.onCreate(savedInstanceState, persistentState)
//        var fragment: Fragment
//        fragment = HomeFragment()
//        loadFragment(fragment)
//    }

    override fun onBackPressed() {
        currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is HomeFragment || currentFragment == null) {
            bottomNavigationView?.selectedItemId = R.id.navigation_home
            val currentTime = System.currentTimeMillis()
            if (currentTime - backPressedTime < backPressedTimeout) {
                val myButton: View = findViewById(R.id.exit_dialog)
                myButton.visibility = View.VISIBLE
                val deleteDiloug = binding.exitDialog
                deleteDiloug.btnDelete.setOnClickListener {
                    finish()
                }

                deleteDiloug.btncancel.setOnClickListener {
                    val myButton: View = findViewById(R.id.exit_dialog)
                    myButton.visibility = View.GONE
                }
            } else {
                backPressedTime = currentTime
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
            }
        } else if (currentFragment is CategoryListFragment) {
            val categoryListFragment = currentFragment as CategoryListFragment
            val isDataTrueOrNot = categoryListFragment.getIsData()

            if (isDataTrueOrNot != null && isDataTrueOrNot == "INCOME_ACTIVITY") {
                loadFragmentForBack(IncomeActivity())
            } else {
                loadFragmentForBack(MoreFragment())
            }
        } else if (currentFragment is PaymentModeFragment) {
            loadFragmentForBack(MoreFragment())
        } else if (currentFragment is AddCategoriesFragment) {
            loadFragmentForBack(CategoryListFragment())
        } else if (currentFragment is BudgetFragment) {
            bottomNavigationView?.selectedItemId = R.id.navigation_home
            loadFragmentForBack(HomeFragment())
        } else if (currentFragment is TransectionFragment) {
            bottomNavigationView?.selectedItemId = R.id.navigation_home
            loadFragmentForBack(HomeFragment())
        } else if (currentFragment is MoreFragment) {
            bottomNavigationView?.selectedItemId = R.id.navigation_home
            loadFragmentForBack(HomeFragment())
        } else if (currentFragment is TransectionListFragment) {
            bottomNavigationView?.selectedItemId = R.id.navigation_home
            loadFragmentForBack(HomeFragment())
        } else if (currentFragment is CurrencyFragment) {
            bottomNavigationView?.selectedItemId = R.id.navigation_more
            loadFragmentForBack(MoreFragment())
        } else if (currentFragment is IncomeActivity) {
            bottomNavigationView?.selectedItemId = R.id.navigation_home
            loadFragmentForBack(HomeFragment())
        } else if (currentFragment is ExpenseActivity) {
            bottomNavigationView?.selectedItemId = R.id.navigation_home
            loadFragmentForBack(HomeFragment())
        } else if (currentFragment is WebViewFragment) {
            bottomNavigationView?.selectedItemId = R.id.navigation_more
            loadFragmentForBack(MoreFragment())
        } else {
            finish()
        }

    }

    fun loadFragmentForBack(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    //    private fun arePermissionsGranted(): Boolean {
//        val permissions = arrayOf(
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.INTERNET,
//            Manifest.permission.GET_ACCOUNTS,
//            Manifest.permission.USE_CREDENTIALS
//        )
//
//        for (permission in permissions) {
//            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//                return false
//            }
//        }
//
//        return true
//    }
    fun isPermissionGranted(): Boolean {
        return sharedPreferences.getBoolean("permission_granted", false)
    }

    private fun requestPermission() {
        val permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                sharedPreferences.edit().putBoolean("permission_granted", true).apply()
            } else {
                // Permission denied
                // Handle the case where the permission is denied by showing a message or taking appropriate action
            }
        }
    }

    fun getIdofNativeAds() {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Keys")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.value as Map<*, *>?

                val appOpenKey = data?.get("App_Open_key") as String
                val bannerKey = data?.get("Banner_key") as String
                val interstitialVideoKey = data?.get("Interstitial_Video_key") as String
                val interstitialKey = data?.get("Interstitial_key") as String
                val nativeAdKey = data?.get("Native_Advanced_Video_key") as String
                val nativeAdvancedKey = data?.get("Native_Advanced_key") as String
                val rewardedInterstitialKey = data?.get("Rewarded_Interstitial_key") as String
                val rewardedKey = data?.get("Rewarded_key") as String
                val isShow = data?.get("is_show") as Boolean

                val preference = getSharedPreferences("NativeId", AppCompatActivity.MODE_PRIVATE)
                val editor = preference.edit()
                editor.putString("Na_tive_id", nativeAdvancedKey)
                editor.putString("inter_id", interstitialKey)
                editor.putString("banner_Key",bannerKey)
                editor.apply()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if retrieval is unsuccessful
            }
        })
    }

    fun isInternet(): Boolean {
        var isConnected = false
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            isConnected = when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            isConnected = when (connectivityManager.activeNetworkInfo?.type) {
                ConnectivityManager.TYPE_WIFI -> true
                ConnectivityManager.TYPE_MOBILE -> true
                ConnectivityManager.TYPE_ETHERNET -> true
                else -> false
            }
        }
        return isConnected
    }

    fun showNoInternetDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("No Internet Connection")
        dialogBuilder.setMessage("Please check your internet connection and try again.")
        dialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            finish()
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }



}