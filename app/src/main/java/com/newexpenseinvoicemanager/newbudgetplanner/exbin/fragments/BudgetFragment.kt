package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.content.ContentValues
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.ads.nativetemplates.rvadapter.AdmobNativeAdAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.MainActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.BudgetAndExpenseAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.getCurrencyClass
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentBudgetBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.BudgetDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class BudgetFragment : Fragment() {

    private lateinit var binding: FragmentBudgetBinding
    private lateinit var categoryList: ArrayList<String>
    private lateinit var selectedCatColor: String
    private var FireBaseGooggleAdsId: String = ""
    private var currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH)
    var sMonth: String = ""
    private var mInterstitialAd: InterstitialAd? = null
    private var FireBaseGooggleAdsInterId: String = ""
    private var _ID: Int? = null
    private var isAds:Boolean = false

    //    var monthName: String = DateFormatSymbols().months[currentMonth]
    private var editBudgetView: View? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBudgetBinding.inflate(layoutInflater)

        val preference =
            requireContext().getSharedPreferences("NativeId", AppCompatActivity.MODE_PRIVATE)
        FireBaseGooggleAdsId = preference.getString("Na_tive_id", "")!!
        FireBaseGooggleAdsInterId = preference.getString("inter_id", "")!!
        isAds = preference.getBoolean("isShow", false)




        if (editBudgetView != null) {
            binding.createBudget.isEnabled = false
            binding.amountText.isEnabled = false
            binding.saveBudget.isEnabled = false
            binding.budgetRecy.isEnabled = false
            binding.materialButton1.isEnabled = false
            binding.materialButton.isEnabled = false
            binding.categorySpin.setOnTouchListener { v, event ->
                true
            }
            binding.saveBudget.isEnabled = false
            binding.budgetRecy.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    // Return true to consume the touch event and prevent further handling
                    return true
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                    // No-op, as touch events are intercepted and not propagated
                }

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                    // No-op
                }
            })

        }
        val currencyClass = getCurrencyClass(viewLifecycleOwner, requireContext())
        currencyClass.getCurrencies { crnSymb ->
            binding.teSimbol.setText(crnSymb)
        }


        // setBudgetView(inflater, container, sMonth)
//        binding.ydtextView.visibility = View.GONE
//        binding.budgetRecy.visibility = View.VISIBLE
        getCategory()
        binding.materialButton.setOnClickListener {
            binding.createBudget.visibility = View.GONE
            binding.saveBudget.visibility = View.VISIBLE
        }
        val adapter = BudgetAndExpenseAdapter(emptyList()) { it, progress, limitShow, remain -> }

        val monthName = DateFormatSymbols().months[currentMonth]
        sMonth = monthName
        if (sMonth != null) {
            setBudgetView(inflater, container, sMonth)
        }
        binding.monthTxt.text = monthName
        // Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()

        // Set up the back button
        binding.backBtn.setOnClickListener {
            currentMonth--
            if (currentMonth < 0) {
                currentMonth = 11
            }
            val monthName = DateFormatSymbols().months[currentMonth]
            binding.monthTxt.text = monthName
            sMonth = binding.monthTxt.text.toString()
            setBudgetView(inflater, container, sMonth)
            // adapter.notifyDataSetChanged()
//            Toast.makeText(requireContext(), "$sMonth", Toast.LENGTH_SHORT).show()
//            Log.d("bck Mont","$sMonth")

        }

        // Set up the next button
        binding.nxtBtn.setOnClickListener {
            currentMonth++
            if (currentMonth > 11) {
                currentMonth = 0
            }
//            val monthName = DateFormatSymbols().months[currentMonth]
            val monthName = DateFormatSymbols().months[currentMonth]

            binding.monthTxt.text = monthName
            sMonth = binding.monthTxt.text.toString()
            setBudgetView(inflater, container, sMonth)
            // getBudgetByMonth(monthName,adapter)
            adapter.notifyDataSetChanged()
//            Toast.makeText(requireContext(), "$sMonth", Toast.LENGTH_SHORT).show()
//            Log.d("nxt Mont","$sMonth")
        }


        binding.materialButton1.setOnClickListener {
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            val amount = binding.amountText.text.toString()
            if (binding.amountText.text.isEmpty()) {
                Toast.makeText(requireContext(), "Please Enter Amount", Toast.LENGTH_SHORT).show()
                binding.amountText.requestFocus()
            } else {


                if (sMonth == null) {
                    val db = AppDataBase.getInstance(requireContext()).budgetDao()
                    val existingBudget =
                        db.getBudgetByName(binding.categorySpin.selectedItem as String, monthName)
                    if (existingBudget != null) {
                        Toast.makeText(
                            requireContext(),
                            "Budget Already exists",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        insertBudget(
                            amount,
                            binding.categorySpin.selectedItem as String,
                            selectedCatColor,
                            currentDate,
                            monthName
                        )
                        clearText()
                        binding.createBudget.visibility = View.VISIBLE
                        binding.saveBudget.visibility = View.GONE
                        loadFragment(BudgetFragment())
                    }
                } else {
                    val db = AppDataBase.getInstance(requireContext()).budgetDao()
                    val existingBudget =
                        db.getBudgetByName(binding.categorySpin.selectedItem as String, sMonth)
                    if (existingBudget != null) {
                        Toast.makeText(
                            requireContext(),
                            "Budget Already exists",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        insertBudget(
                            amount,
                            binding.categorySpin.selectedItem as String,
                            selectedCatColor,
                            currentDate,
                            sMonth
                        )
                        clearText()
                        binding.createBudget.visibility = View.VISIBLE
                        binding.saveBudget.visibility = View.GONE
                        loadFragment(BudgetFragment())
                    }
                }
            }
        }

        binding.editBackarrow.setOnClickListener {
            loadFragment(BudgetFragment())
        }


        return binding.root
    }

    fun insertBudget(
        amount: String,
        category: String,
        catgoryColor: String,
        date: String,
        dMonth: String
    ) {
//        val currentMonth = Month.values()[Month.values().indexOf(Month.valueOf(java.time.LocalDate.now().month.name))]
//        val monthString = currentMonth.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val cal = Calendar.getInstance()
        val month_date = SimpleDateFormat("MMMM")
        val month_name = month_date.format(cal.time)

        val db = AppDataBase.getInstance(requireContext()).budgetDao()
        val data = BudgetDb(
            budget = amount,
            budgetCat = category,
            catColor = catgoryColor,
            currentDate = date,
            month = month_name,
            dbMonth = dMonth

        )
        lifecycleScope.launch(Dispatchers.IO) {
            db.inserBudget(data)
        }
    }

    fun getCategory() {
        categoryList = ArrayList()
        val dao = AppDataBase.getInstance(requireContext()).categoriesDao()

        dao.getAllCategory().observe(requireActivity()) { categories ->
            if (categories != null) {
                if (categories.isEmpty()) {
                    categoryList.clear()
                    categoryList.add(0, "Select Category")
                } else {
                    categoryList.clear()
                    for (category in categories) {
                        val categoryName = category.CategoryName
                        if (categoryName != null) {
                            categoryList.add(categoryName)
                        }
                    }
                    val arrayAdapter =
                        ArrayAdapter(requireContext(), R.layout.dropdown_item_layout, categoryList)
                    binding.categorySpin.adapter = arrayAdapter

                    binding.categorySpin.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val selectedCategory =
                                    parent?.getItemAtPosition(position).toString()

                                // query the database for the color of the selected category
                                val category = dao.getCategoryByName(selectedCategory)
                                val color = category?.CategoryColor
                                    ?: "#000000" // default color if category not found
                                selectedCatColor = color
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                // do nothing
                            }
                        }
                }
            }
        }
    }

    fun setBudgetView(inflater: LayoutInflater, container: ViewGroup?, sdMonth: String) {

        val adapter = BudgetAndExpenseAdapter(emptyList()) { it, progress, limitShow, remain ->
            binding.createBudget.isEnabled = false
            binding.amountText.isEnabled = false
            binding.saveBudget.isEnabled = false
            binding.budgetRecy.isEnabled = false
            binding.materialButton1.isEnabled = false
            binding.materialButton.isEnabled = false
            binding.categorySpin.setOnTouchListener { v, event ->
                true
            }
            binding.saveBudget.isEnabled = false
            binding.budgetRecy.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    // Return true to consume the touch event and prevent further handling
                    return true
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                    // No-op, as touch events are intercepted and not propagated
                }

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                    // No-op
                }
            })
            val currencyClass = getCurrencyClass(viewLifecycleOwner, requireContext())

            // Log.d("It/prog/limit", "$it $progress $limitShow")
            val bId = it.budgetId
            _ID = bId
            val bData = it

            val mainlayoutView = inflater.inflate(R.layout.fragment_budget, container, false)
            editBudgetView = inflater.inflate(R.layout.all_budget_layout, container, false)
            container?.removeView(mainlayoutView)
            container?.addView(editBudgetView)


            val budgetdetail = editBudgetView?.findViewById<ConstraintLayout>(R.id.cl_pre_screen)
            budgetdetail!!.isEnabled = false
            val budgetProgress = editBudgetView?.findViewById<ProgressBar>(R.id.pre_determinate_bar)
            val budgetLimitImage =
                editBudgetView?.findViewById<AppCompatImageView>(R.id.pre_ic_arror)
            val budgetLimitTextView =
                editBudgetView?.findViewById<AppCompatTextView>(R.id.pre_tv_alert_limit)
            val budgetIconImageView =
                editBudgetView?.findViewById<ShapeableImageView>(R.id.pre_iv_catagory)
            val editButton = editBudgetView?.findViewById<MaterialButton>(R.id.editButton)
            val deleteButton = editBudgetView?.findViewById<AppCompatImageView>(R.id.deleteButton)
            val categoryName = editBudgetView?.findViewById<TextView>(R.id.pre_tv_cat_name)
            val backButtonBudget =
                editBudgetView?.findViewById<AppCompatImageView>(R.id.backButtonBudget)
            budgetdetail?.visibility = View.VISIBLE

            (activity as MainActivity?)!!.hideBottomNavigationView()
            backButtonBudget?.setOnClickListener {
                loadFragment(BudgetFragment())
                container?.removeView(editBudgetView)
                (activity as MainActivity?)!!.showBottomNavigationView()
            }
            if (limitShow == true) {
                budgetLimitImage?.visibility = View.VISIBLE
                budgetLimitTextView?.visibility = View.VISIBLE
            } else {
                budgetLimitImage?.visibility = View.GONE
                budgetLimitTextView?.visibility = View.GONE
            }
            deleteButton?.setOnClickListener {
                // Toast.makeText(requireContext(), "Button Pressed", Toast.LENGTH_SHORT).show()
                val deleteDialog =
                    editBudgetView?.findViewById<ConstraintLayout>(R.id.budgetDeleteCard)
//                budgetdetail?.visibility = View.GONE
                deleteDialog?.visibility = View.VISIBLE
                val cancelBtn = editBudgetView?.findViewById<MaterialButton>(R.id.btncancel)
                val deleteBtn = editBudgetView?.findViewById<MaterialButton>(R.id.btn_delete)

                deleteBtn?.setOnClickListener {
                    // add ads here

                        deleteBudget(bId)
                        deleteDialog?.visibility = View.GONE
                        Toast.makeText(requireContext(), "Budget Deleted", Toast.LENGTH_SHORT)
                            .show()
                        container?.removeView(editBudgetView)
                        loadFragment(BudgetFragment())

                }

                cancelBtn?.setOnClickListener {
                    deleteDialog?.visibility = View.GONE
                    budgetdetail?.visibility = View.VISIBLE

                }
            }
            val budgetTextView = editBudgetView?.findViewById<EditText>(R.id.edit_amount)
            budgetTextView!!.isEnabled = true

            if (editButton!!.isPressed) {
                budgetTextView!!.isEnabled = true
            } else if (editBudgetView != null && !editButton!!.isPressed) {
                budgetTextView!!.isEnabled = false
            }

            editButton?.setOnClickListener {
                budgetTextView!!.isEnabled = true

                val editudgetLayout =
                    editBudgetView?.findViewById<ConstraintLayout>(R.id.cl_edit_budget)
                budgetdetail?.visibility = View.GONE
                editudgetLayout?.visibility = View.VISIBLE

                val bSymbol = editBudgetView?.findViewById<TextView>(R.id.edit_currency_symbol)
                currencyClass.getCurrencies { crnSymb ->
                    bSymbol?.setText(crnSymb)
                }
                budgetTextView?.setText(bData.budget)
                val budgetCatTextView =
                    editBudgetView?.findViewById<AppCompatTextView>(R.id.edit_category_spin)
                budgetCatTextView?.setText(bData.budgetCat)
                val budgetBackButoon =
                    editBudgetView?.findViewById<AppCompatImageView>(R.id.edit_backarrow)
                budgetBackButoon?.setOnClickListener {
                    budgetdetail?.visibility = View.VISIBLE
                    editudgetLayout?.visibility = View.GONE
                }
                if (isAds == true){
                loadAd(budgetTextView,budgetdetail,editudgetLayout,container)}
                val budgetSaveButton =
                    editBudgetView?.findViewById<MaterialButton>(R.id.edit_btn_save)
                budgetSaveButton?.setOnClickListener {
                    // add ads here
                    if (mInterstitialAd != null) {
                        mInterstitialAd?.show(requireActivity())
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.")

                        updateBudget(bId, budgetTextView?.text.toString())
                        Toast.makeText(
                            requireContext(),
                            "Budget Updated Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        budgetdetail?.visibility = View.VISIBLE
                        editudgetLayout?.visibility = View.GONE
                        container?.removeView(editBudgetView)
                        loadFragment(BudgetFragment())
                    }
                    //container?.addView(editBudgetView)
                }
            }

            val remainingAmount = editBudgetView?.findViewById<TextView>(R.id.pre_tv_amount)
            categoryName?.setText("${it.budgetCat}")
            val color = it.catColor
            val colorInt = Color.parseColor(color!!)
            val hsl = FloatArray(3)
            ColorUtils.colorToHSL(colorInt, hsl)

            hsl[2] += 0.2f // Increase the lightness value by 20%
            if (hsl[2] > 1.0f) {
                hsl[2] = 1.0f // Cap the lightness value at 100%
            }
            val lightColor = ColorUtils.HSLToColor(hsl)
            budgetIconImageView?.setImageResource(it.catImage!!.toInt())
            budgetIconImageView?.setColorFilter(colorInt, PorterDuff.Mode.SRC_IN)
            budgetIconImageView?.setBackgroundColor(lightColor)


            if (it.budgetCat != null && it.amount == null) {
                currencyClass.getCurrencies { crnSymb ->
                    remainingAmount?.setText("$crnSymb" + " " + "${it.amount}")
                }
            } else {
                currencyClass.getCurrencies { crnSymb ->

                    remainingAmount?.setText(crnSymb + " " + "$remain")
                }

                if (it.budget != null && it.amount1 != null) {

                    budgetProgress?.progress = progress.toInt()
                    budgetProgress?.progressTintList =
                        ColorStateList.valueOf(Color.parseColor(it.catColor))

                } else {
                    budgetProgress?.progress = 0
                    budgetProgress?.progressTintList =
                        ColorStateList.valueOf(Color.parseColor(it.catColor))
                }

            }

        }
        val admobNativeAdAdapter = AdmobNativeAdAdapter.Builder.with(
            FireBaseGooggleAdsId,
            adapter,
            "small"   // "medium" it can also used
        ).adItemInterval(3).build()


        binding.budgetRecy.adapter = admobNativeAdAdapter
        val dao = AppDataBase.getInstance(requireContext()).budgetDao()
        lifecycleScope.launch {
            val budgetAndExpenseList = dao.getBudgetAndExpense(sdMonth)
            if (budgetAndExpenseList.isEmpty()) {
                binding.ydtextView.visibility = View.VISIBLE
                binding.budgetRecy.visibility = View.GONE
//                Log.d("from if Mont","$sdMonth")
            } else {
//                Log.d("from else Mont","$sdMonth")
                binding.budgetRecy.visibility = View.VISIBLE
                binding.ydtextView.visibility = View.GONE
                adapter.budgetAndExpenseList = budgetAndExpenseList
                adapter.notifyDataSetChanged()
//                Toast.makeText(requireContext(), "Button pressed And data avilable", Toast.LENGTH_SHORT).show()
            }
        }

    }


    fun clearText() {
        binding.amountText.setText("")
        Toast.makeText(requireContext(), "Budget Added Successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        if (editBudgetView != null) {
            super.onStop()
            editBudgetView?.visibility = View.GONE
        } else {
            super.onStop()
        }
    }

    fun deleteBudget(id: Int) {
        val db = AppDataBase.getInstance(requireContext()).budgetDao()
//        val data = PaymentModes()
        lifecycleScope.launch(Dispatchers.IO) {
            db.deleteBudget(id)
        }
    }

    fun updateBudget(id: Int, budget: String) {
        val db = AppDataBase.getInstance(requireContext()).budgetDao()
//        val data = PaymentModes()
        lifecycleScope.launch(Dispatchers.IO) {
            db.updateBudget(id, budget)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)
            ?.addToBackStack(null)
            ?.commit()
        (activity as MainActivity?)!!.showBottomNavigationView()
    }

    fun loadAd(
        budgetTextView: EditText,
        budgetdetail: ConstraintLayout,
        editudgetLayout: ConstraintLayout?,
        container: ViewGroup?
    ) {
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            requireContext(),
            FireBaseGooggleAdsInterId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(ContentValues.TAG, adError?.toString()!!)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(ContentValues.TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(ContentValues.TAG, "Ad was clicked.")
                            }

                            override fun onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                Log.d(ContentValues.TAG, "Ad dismissed fullscreen content.")

                                mInterstitialAd = null
                                updateBudget(_ID!!, budgetTextView?.text.toString())
                                Toast.makeText(
                                    requireContext(),
                                    "Budget Updated Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                budgetdetail?.visibility = View.VISIBLE
                                editudgetLayout?.visibility = View.GONE
                                container?.removeView(editBudgetView)
                                loadFragment(BudgetFragment())

                            }

                            override fun onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(ContentValues.TAG, "Ad recorded an impression.")
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(ContentValues.TAG, "Ad showed fullscreen content.")
                            }
                        }

                }
            })
    }


}

