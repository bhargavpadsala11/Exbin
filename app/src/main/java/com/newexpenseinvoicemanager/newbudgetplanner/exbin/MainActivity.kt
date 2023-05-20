package com.newexpenseinvoicemanager.newbudgetplanner.exbin

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.activities.ExpenseActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.activities.IncomeActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.ActivityMainBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var fab: FloatingActionButton
    private var backPressedTime: Long = 0
    private val backPressedTimeout: Long = 2000 // 2 seconds
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
        val toolbar = getSupportActionBar();
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        fab = binding.fab

        bottomNavigationView.setOnItemSelectedListener { item ->
            var fragment: Fragment
            when (item.itemId) {
                R.id.navigation_home -> {
                    floatButtonHide()
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
        fab.setOnClickListener {
            floatButtonShow()
        }
        binding.inc.setOnClickListener {
            val intent = Intent(this, IncomeActivity::class.java)
            val options = ActivityOptionsCompat.makeCustomAnimation(
                this,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right,
            )
            ActivityCompat.startActivity(this, intent, options.toBundle())
        }
        binding.exp.setOnClickListener {
            val intent = Intent(this, ExpenseActivity::class.java)
            val options = ActivityOptionsCompat.makeCustomAnimation(
                this,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right,
            )
            ActivityCompat.startActivity(this, intent, options.toBundle())
        }

    }


    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            //add animation code here   .setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun floatButtonShow() {
        binding.exp.visibility = View.VISIBLE
        binding.inc.visibility = View.VISIBLE
    }

    private fun floatButtonHide() {
        binding.exp.visibility = View.GONE
        binding.inc.visibility = View.GONE
    }


    public fun showBottomNavigationView() {
        bottomNavigationView?.setVisibility(View.VISIBLE)
        bottomAppBar.visibility = View.VISIBLE
        fab.visibility = View.VISIBLE
    }

    public fun hideBottomNavigationView() {
        bottomNavigationView?.setVisibility(View.GONE)
        bottomAppBar.visibility = View.GONE
        fab.visibility = View.GONE


    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        var fragment: Fragment
        fragment = HomeFragment()
        loadFragment(fragment)
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        when {
            currentFragment is MoreFragment -> {
                // If the current fragment is a MoreFragment, pop the back stack
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()

                } else {
                    super.onBackPressed()
                }
            }
            currentFragment is CategoryListFragment -> {
                // If the current fragment is a CategoryFragment, pop the back stack
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    super.onBackPressed()
                }
            }
            currentFragment is AddCategoriesFragment -> {
                // If the current fragment is a SlideFragment, pop the back stack
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    super.onBackPressed()
                }
            }
            currentFragment is PaymentModeFragment -> {
                // If the current fragment is a SlideFragment, pop the back stack
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    super.onBackPressed()
                }
            }
            else -> {
                val fragment = HomeFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, fragment)
                transaction.commit()
                // Set the selectedItemId based on the current fragment
                when (currentFragment) {
                    is MoreFragment -> bottomNavigationView.selectedItemId = R.id.navigation_more
                    is TransectionFragment -> bottomNavigationView.selectedItemId = R.id.navigation_transection
                    is BudgetFragment -> bottomNavigationView.selectedItemId = R.id.navigation_budget
                    else -> bottomNavigationView.selectedItemId = R.id.navigation_home
                }
            }
        }
    }




}