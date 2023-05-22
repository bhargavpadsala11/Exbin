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
    private var isButtonVisible = false

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
        if (currentFragment is HomeFragment) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - backPressedTime < backPressedTimeout) {
                finish()
            } else {
                backPressedTime = currentTime
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
            }
        } else {
            val fragment = HomeFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
            bottomNavigationView.selectedItemId = R.id.navigation_home
        }
    }




}