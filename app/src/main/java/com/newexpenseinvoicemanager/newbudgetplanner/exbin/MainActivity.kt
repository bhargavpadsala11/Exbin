@file:Suppress("NAME_SHADOWING", "DEPRECATION", "ReplaceGetOrSet")

package com.newexpenseinvoicemanager.newbudgetplanner.exbin

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.ActivityMainBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var bottomNavigationView: BottomNavigationView? = null
    private var bottomAppBar: BottomAppBar? = null
    private var fab: FloatingActionButton? = null
    private var backPressedTime: Long = 0
    private val backPressedTimeout: Long = 2000 // 2 seconds
    private var isButtonVisible = false
    private var currentFragment: Fragment? = null
    private lateinit var sharedPreferences: SharedPreferences
    private val PERMISSION_REQUEST_CODE = 123
    private var FireBaseGooggleAdsId: String = ""

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
            val toolbar = supportActionBar
            bottomNavigationView = findViewById(R.id.bottomNavigationView)
            bottomAppBar = findViewById(R.id.bottomAppBar)

            fab = binding.fab


            val isConnected = isInternet()
            if (isConnected) {
                val value =
                    intent.getStringExtra("key_Main") // Replace "key" with the key used in the first activity
                if (value != null) {
                    // Do something with the value
                    loadFragment(MoreFragment())
                }
                bottomNavigationView?.setOnItemSelectedListener { item ->
                    currentFragment =
                        supportFragmentManager.findFragmentById(R.id.fragment_container)
                    val fragment: Fragment
                    when (item.itemId) {
                        R.id.navigation_home -> {
                            if (currentFragment !is HomeFragment) {
                                toolbar?.title = "Home"
                                fragment = HomeFragment()
                                loadFragment(fragment)
                                if (fab?.isPressed == true) {
                                    floatButtonShow()
                                }
                                true
                            } else {
                                false
                            }
                        }
                        R.id.navigation_transection -> {
                            if (currentFragment !is TransectionFragment) {
                                floatButtonHide()
                                toolbar?.title = "Transection"
                                fragment = TransectionFragment()
                                loadFragment(fragment)
                                true
                            } else {
                                false
                            }
                        }
                        R.id.navigation_add -> {
                            floatButtonShow()
                            toolbar?.title = "Home"
                            fragment = HomeFragment()
                            loadFragment(fragment)
                            floatButtonShow()
                            true
                        }
                        R.id.navigation_budget -> {
                            if (currentFragment !is BudgetFragment) {
                                floatButtonHide()
                                toolbar?.title = "Budget"
                                fragment = BudgetFragment()
                                loadFragment(fragment)
                                true
                            } else {
                                false
                            }
                        }
                        R.id.navigation_more -> {
                            if (currentFragment !is MoreFragment) {
                                floatButtonHide()
                                toolbar?.title = "More"
                                fragment = MoreFragment()
                                loadFragment(fragment)
                                true
                            } else {
                                false
                            }
                        }
                        else -> false
                    }
                }
                if (currentFragment !is HomeFragment) {
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
                    if (currentFragment !is HomeFragment) {
                        bottomNavigationView?.selectedItemId = R.id.navigation_home
                        val fragment = HomeFragment()
                        loadFragment(fragment)
                    } else {
                        floatButtonShow()
                    }

                }
                if (currentFragment !is HomeFragment) {
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

        } else {
            showNoInternetDialog()
        }
    }

    fun getIdofNativeAds() {
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Keys")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.value as Map<*, *>?

                val bannerKey = data?.get("Banner_key") as String
                val interstitialKey = data.get("Interstitial_key") as String
                val nativeAdvancedKey = data.get("Native_Advanced_key") as String
                FireBaseGooggleAdsId = nativeAdvancedKey
                val privacy_policy = data.get("Privacy_policy") as String
                val terms_conditions = data.get("terms_conditions") as String
                val share_link = data.get("share_link") as String
                val isShowAds = data.get("is_show") as String

                var isShow = false
                if (isShowAds == "true") {
                    isShow = true
                } else {
                    !isShow
                }
Log.d("status","$isShow")
                val preference = getSharedPreferences("NativeId", MODE_PRIVATE)
                val editor = preference.edit()
                editor.putBoolean("isShow", isShow)
                editor.putString("Na_tive_id", nativeAdvancedKey)
                editor.putString("inter_id", interstitialKey)
                editor.putString("banner_Key", bannerKey)
                editor.apply()

                val pref = getSharedPreferences("TERMS_PRIVACY", MODE_PRIVATE)
                val edit = pref.edit()
                edit.putString("privacy_policy", privacy_policy)
                edit.putString("terms_conditions", terms_conditions)
                edit.putString("share_link", share_link)
                edit.apply()

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if retrieval is unsuccessful
            }
        })
    }

    @SuppressLint("CommitTransaction")
    private fun loadFragment(fragment: Fragment) {

        val isConnected = isInternet()
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

    fun floatButtonHide() {
        binding.exp.visibility = View.GONE
        binding.inc.visibility = View.GONE
    }

    fun showBottomNavigationView() {
        bottomNavigationView?.visibility = View.VISIBLE
        bottomAppBar?.visibility = View.VISIBLE
        fab?.visibility = View.VISIBLE
    }

    fun setBottomNavigationAsHome() {
        bottomNavigationView?.selectedItemId = R.id.navigation_home
    }

    fun hideBottomNavigationView() {
        bottomNavigationView?.visibility = View.GONE
        bottomAppBar?.visibility = View.GONE
        fab?.visibility = View.GONE


    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is HomeFragment || currentFragment == null) {
            bottomNavigationView?.selectedItemId = R.id.navigation_home
            val currentTime = System.currentTimeMillis()
            if (currentTime - backPressedTime < backPressedTimeout) {
                val myButton: View = findViewById(R.id.exit_dialog)
                myButton.visibility = View.VISIBLE
                if (myButton.visibility == View.VISIBLE) {
                    Log.d("myButton.visibility", "Called")
                    binding.mainActiviry.isEnabled = false
                    binding.bottomAppBar.isEnabled = false
                    binding.bottomNavigationView.isEnabled = false
                    binding.fragmentContainer.isEnabled = false
                } else {
                    binding.mainActiviry.isEnabled = true
                    binding.bottomAppBar.isEnabled = true
                    binding.bottomNavigationView.isEnabled = true
                    binding.fragmentContainer.isEnabled = true
                }
                val deleteDiloug = binding.exitDialog
                val adLoader = AdLoader.Builder(this, FireBaseGooggleAdsId)
                    .forNativeAd { nativeAd ->
                        val styles =
                            NativeTemplateStyle.Builder().build()
                        val template: TemplateView = deleteDiloug.myTemplate
                        template.setStyles(styles)
                        template.setNativeAd(nativeAd)
                    }
                    .build()

                adLoader.loadAd(AdRequest.Builder().build())
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
            val isDataTrueOrNot = categoryListFragment.valueToCheck

            Log.d("INCOME_ACTIVITY", isDataTrueOrNot)
            if (isDataTrueOrNot == "INCOME_ACTIVITY" || isDataTrueOrNot == "INCOME_ACTIVITY_UPDATE") {
                loadFragmentForBack(IncomeActivity())
            } else if (isDataTrueOrNot == "EXPENSE_ACTIVITY" || isDataTrueOrNot == "EXPENSE_ACTIVITY_UPDATE") {
                loadFragmentForBack(ExpenseActivity())
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

    @SuppressLint("CommitTransaction")
    private fun loadFragmentForBack(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun isPermissionGranted(): Boolean {
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


    private fun isInternet(): Boolean {
        val isConnected: Boolean
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

    private fun showNoInternetDialog() {
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