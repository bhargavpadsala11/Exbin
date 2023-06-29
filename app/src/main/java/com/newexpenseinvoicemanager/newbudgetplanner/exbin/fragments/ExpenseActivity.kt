package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.button.MaterialButton
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.MainActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.ActivityExpenseBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.incexpTbl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class ExpenseActivity : Fragment() {
    private lateinit var binding: ActivityExpenseBinding
    private lateinit var categoryList: ArrayList<String>
    private lateinit var PaymentModeList: ArrayList<String>
    private var date: String? = ""
    private var time: String? = ""
    private var value: String? = ""
    private var sMonth: String? = ""
    private var deleteCategoryView: View? = null
    private var CAT_: String? = ""
    private var AMNT_: String? = ""
    private var DATE_: String? = ""
    private var TIME_: String? = ""
    private var PAY_: String? = ""
    private var PAY_MD_: String? = ""
    private var NOTE_: String? = ""
    private var SMONTH_: String? = ""
    private var _ID: String? = ""
    private var mInterstitialAd: InterstitialAd? = null
    private var FireBaseGooggleAdsInterId: String = ""
    private var FireBaseGooggleAdsBanner: String = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityExpenseBinding.inflate(layoutInflater)
        (activity as MainActivity?)!!.floatButtonHide()
        val preference =
            requireContext().getSharedPreferences("NativeId", AppCompatActivity.MODE_PRIVATE)
        FireBaseGooggleAdsInterId = preference.getString("inter_id", "")!!
        FireBaseGooggleAdsBanner = preference.getString("banner_Key", "")!!

        val custom = binding.appBar
        custom.ivDelete.visibility = View.GONE
        custom.ivTitle.setText("Add Expense")
        custom.ivBack.setOnClickListener {
            loadFragment(HomeFragment())
            (activity as MainActivity?)!!.setBottomNavigationAsHome()
        }

        val adContainer = binding.adView
        val mAdView = AdView(requireContext())
        mAdView.setAdSize(AdSize.BANNER)
        mAdView.adUnitId = FireBaseGooggleAdsBanner
        adContainer.addView(mAdView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        mAdView.adListener = object: AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }

//        supportActionBar?.title = "Expense"
//        value = intent.getStringExtra("value")
        value = arguments?.getString("value")

        val SELECTED_CATEGORY = arguments?.getString("CATEGORY1")
        val INC_ = arguments?.getString("EXP_")

        if (INC_ != null) {

            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("dd/M/yyyy")
            custom.ivTitle.setText("Update Expense")
            custom.ivDelete.visibility = View.VISIBLE
            // getPaymentMode()
            val UPDATE_CAT = arguments?.getString("CATEGORY_Update")
            if (UPDATE_CAT != null) {
                CAT_ = UPDATE_CAT
            } else {
                CAT_ = arguments?.getString("cty")
            }
            val ID_ = arguments?.getString("id")
            AMNT_ = arguments?.getString("amt")
            DATE_ = arguments?.getString("dt")
            TIME_ = arguments?.getString("time")
            PAY_ = arguments?.getString("pmd")
            PAY_MD_ = arguments?.getString("PMIND")
            NOTE_ = arguments?.getString("nt")
            SMONTH_ = arguments?.getString("month")
            _ID = ID_
            loadAd()
            spinnerSet(PAY_!!)
            binding.expcategory.setOnClickListener { getCategoryForUpdate() }
            //PaymentModeList.set(PAY_MD_!!.toInt(),PAY_!!)
            binding.expAmount.setText(AMNT_)
            binding.expdate.setText(DATE_)
            binding.expcategory.setText(CAT_)
            binding.expNote.setText(NOTE_)
            binding.exptime.setText(TIME_)
            custom.ivDelete.setOnClickListener {
                deleteCategoryView =
                    inflater.inflate(R.layout.custom_delete_dialog, container, false)
                container?.addView(deleteCategoryView)
                val deleteBtn =
                    deleteCategoryView?.findViewById<MaterialButton>(R.id.btn_delete)
                val cancelBtn =
                    deleteCategoryView?.findViewById<MaterialButton>(R.id.btncancel)
                val hintText =
                    deleteCategoryView?.findViewById<AppCompatTextView>(R.id.tv_delete_title)
                hintText?.setText("Are you sure you want to delete this Expense Item?")
                deleteBtn?.setOnClickListener {
                    val db = AppDataBase.getInstance(requireContext()).incexpTblDao()
                    lifecycleScope.launch(Dispatchers.IO) {
                        db.deleteincomeexpenseId(ID_!!.toInt())
                    }
                    container?.removeView(deleteCategoryView)
                    val ldf = HomeFragment()
                    loadFragment(ldf)
                    (activity as MainActivity?)!!.setBottomNavigationAsHome()
//                    val transaction = activity?.supportFragmentManager?.beginTransaction()
//                    transaction?.replace(R.id.fragment_container, ldf)
//                    transaction?.disallowAddToBackStack()
//                    transaction?.commit()
                }
                cancelBtn?.setOnClickListener {
                    container?.removeView(deleteCategoryView)
                }
            }
            binding.expdate.setOnClickListener {
                val datePicker = DatePickerDialog(
                    requireContext(),
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        DATE_ = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(calendar.time)
                        // Store the date in Room database
                        binding.expdate.text = DATE_
                        val selectedDateString = binding.expdate.text.toString()
                        val selectedDate = sdf.parse(selectedDateString)
                        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
                        val monthName = monthFormat.format(selectedDate)
                        sMonth = monthName

                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.show()
            }

            binding.exptime.setOnClickListener {
                val timePicker = TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        TIME_ =
                            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
                        // Store the time in Room database
                        binding.exptime.text = TIME_

                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )
                timePicker.show()
            }
            binding.addIncomeBtn.setText("Update")

            binding.addIncomeBtn.setOnClickListener {
                validationOfDataForUpdate(
                    ID_,
                    CAT_,
                    AMNT_,
                    DATE_,
                    TIME_,
                    PAY_,
                    PAY_MD_,
                    NOTE_,
                    SMONTH_
                )
            }

        } else if (SELECTED_CATEGORY != null) {
            val amnt = arguments?.getString("amnt_vle")
            val nte = arguments?.getString("nte_vle")
            if (amnt != null && nte != null) {
                binding.expAmount.setText(amnt)
                binding.expNote.setText(nte)
            } else if (amnt == null && nte != null) {
                binding.expNote.setText(nte)
            } else if (nte == null && amnt != null) {
                binding.expAmount.setText(amnt)
            }
            val sdf = SimpleDateFormat("dd/M/yyyy")
            val sdf_1 = SimpleDateFormat("hh:mm a")
            val defaulttDate = sdf.format(Date())
            val defaultTime = sdf_1.format(Date())
            date = defaulttDate
            time = defaultTime
            binding.expdate.setText(defaulttDate)
            binding.exptime.setText(defaultTime)
            val selectedDateString = binding.expdate.text.toString()
            val selectedDate = sdf.parse(selectedDateString)
            val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
            val monthName = monthFormat.format(selectedDate)
            sMonth = monthName
//            Toast.makeText(requireContext(), "Selected $SELECTED_CATEGORY", Toast.LENGTH_SHORT).show()
            binding.expcategory.setOnClickListener { getCategory() }
            binding.expcategory.setText(SELECTED_CATEGORY)
            getPaymentMode()

            val calendar = Calendar.getInstance()

            binding.expdate.setOnClickListener {
                val datePicker = DatePickerDialog(
                    requireContext(),
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        date = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(calendar.time)
                        // Store the date in Room database
                        binding.expdate.text = date
                        val selectedDateString = binding.expdate.text.toString()
                        val selectedDate = sdf.parse(selectedDateString)
                        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
                        val monthName = monthFormat.format(selectedDate)
                        sMonth = monthName

                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.show()
            }

            binding.exptime.setOnClickListener {
                val timePicker = TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        time =
                            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
                        // Store the time in Room database
                        binding.exptime.text = time

                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )
                timePicker.show()
            }

            binding.addIncomeBtn.setOnClickListener {
                validationOfData()
                // checkValidation(amount, category, date, time, paymentModes, note, currentDateTime)
            }
        } else {
            val sdf = SimpleDateFormat("dd/M/yyyy")
            val sdf_1 = SimpleDateFormat("hh:mm a")
            val defaulttDate = sdf.format(Date())
            val defaultTime = sdf_1.format(Date())
            date = defaulttDate
            time = defaultTime
            binding.expdate.setText(defaulttDate)
            binding.exptime.setText(defaultTime)
            val selectedDateString = binding.expdate.text.toString()
            val selectedDate = sdf.parse(selectedDateString)
            val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
            val monthName = monthFormat.format(selectedDate)
            sMonth = monthName
            binding.expcategory.setText(SELECTED_CATEGORY)


            getPaymentMode()
            binding.expcategory.setOnClickListener { getCategory() }
            val calendar = Calendar.getInstance()

            binding.expdate.setOnClickListener {
                val datePicker = DatePickerDialog(
                    requireContext(),
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        date = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(calendar.time)
                        // Store the date in Room database
                        binding.expdate.text = date
                        val selectedDateString = binding.expdate.text.toString()
                        val selectedDate = sdf.parse(selectedDateString)
                        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
                        val monthName = monthFormat.format(selectedDate)
                        sMonth = monthName

                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.show()
            }

            binding.exptime.setOnClickListener {
                val timePicker = TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        time =
                            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
                        // Store the time in Room database
                        binding.exptime.text = time

                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )
                timePicker.show()
            }

            binding.addIncomeBtn.setOnClickListener {
                validationOfData()
                // checkValidation(amount, category, date, time, paymentModes, note, currentDateTime)
            }

        }
//        binding.expcategory.setOnClickListener { getCategory() }
        return binding.root
    }

    private fun getCategoryForUpdate() {
        val ldf = CategoryListFragment()
        val args = Bundle()
        args.putString("SELECT_CAT_EXP_FOR_UPDATE", "22")
        args.putString("amt", AMNT_)
        args.putString("cty", CAT_)
        args.putString("dt", DATE_)
        args.putString("pmd", PAY_)
        args.putString("nt", NOTE_)
        args.putString("time", TIME_)
        args.putString("month", SMONTH_)
        args.putString("PMIND", PAY_MD_)
        args.putString("id", _ID)
        ldf.setArguments(args)
        //Toast.makeText(requireContext(), "$args", Toast.LENGTH_SHORT).show()
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, ldf)
            ?.commit()
    }

    private fun validationOfDataForUpdate(
        iD_: String?,
        caT_: String?,
        amnT_: String?,
        datE_: String?,
        timE_: String?,
        paY_: String?,
        payMd: String?,
        notE_: String?,
        smontH_: String?
    ) {
        if (binding.expcategory.text.toString() == "Select Category" || binding.expcategory.text.toString()
                .isEmpty()
        ) {
            binding.expcategory.requestFocus()
            binding.expcategory.error = "Select Category"
        } else if (binding.expAmount.text.toString() == "Amount" || binding.expAmount.text.toString()
                .isEmpty()
        ) {
            binding.expAmount.requestFocus()
            binding.expAmount.error = "Empty"
        } else if (binding.expdate.text.toString()
                .isEmpty() || binding.expdate.text.toString() == "Date"
        ) {
            binding.expdate.requestFocus()
            binding.expdate.error = "Empty"
        } else if (binding.exppaymentMode.selectedItem.toString() == "Select Payment Mode"
        ) {
            binding.exppaymentMode.requestFocus()
            Toast.makeText(requireContext(), "Please Select Payment mode", Toast.LENGTH_SHORT)
                .show()
        } else if (binding.expNote.text.toString().isEmpty()) {
            binding.expNote.requestFocus()
            binding.expNote.error = "Empty"
        } else {
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(requireActivity())
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")

                val AMNT_ = binding.expAmount.text.toString()
                val NOTE_ = binding.expNote.text.toString()
                val CAT_ = binding.expcategory.text.toString()
                val PAY_ = binding.exppaymentMode.selectedItem as String
//            val categoryindex = binding.expcategory.selectedItemPosition.toString()
                val PAY_MD_ = binding.exppaymentMode.selectedItemPosition.toString()
                updateExpense(_ID, CAT_, AMNT_, DATE_, TIME_, PAY_, PAY_MD_, NOTE_, SMONTH_)
                Toast.makeText(requireContext(), "Expense Updated", Toast.LENGTH_SHORT).show()
                loadFragment(HomeFragment())
            }

        }
    }


    fun insertExpsnes(
        amount: String,
        category: String,
        date: String,
        time: String,
        paymentMode: String,
        paymentModeIndex: String,
        note: String,
        currentDateTime: String,
        sMonth: String
    ) {
        val db = AppDataBase.getInstance(requireContext()).incexpTblDao()
        val data = incexpTbl(
            amount = amount,
            category = category,
            sMonth = sMonth,
            date = date,
            time = time,
            paymentMode = paymentMode,
            paymentModeIndex = paymentModeIndex,
            note = note,
            dType = "EXPENSE",
            currentDate = currentDateTime
        )
        lifecycleScope.launch(Dispatchers.IO) {
            db.inserincexpTbl(data)
        }
    }

    fun getPaymentMode() {
        PaymentModeList = ArrayList()
        val dao = AppDataBase.getInstance(requireContext()).paymentModesDao()

        dao.getAllPaymentMode().observe(requireActivity()) { paymentModes ->
            if (paymentModes != null) {
                if (paymentModes.isEmpty()) {
                    PaymentModeList.clear()
                    PaymentModeList.add(0, "Select Payment Mode")
                } else {
                    PaymentModeList.clear()
                    PaymentModeList.add(0, "Select Payment Mode")
                    for (paymentMode in paymentModes) {
                        val mode = paymentMode.paymentMode
                        if (mode != null) {
                            PaymentModeList.add(mode)
                        }
                    }
                    val arrayAdapter =
                        ArrayAdapter(
                            requireContext(),
                            R.layout.dropdown_item_layout,
                            PaymentModeList
                        )
                    binding.exppaymentMode.adapter = arrayAdapter
                }
            }
        }
    }

    fun getCategory() {
        val ldf = CategoryListFragment()
        val args = Bundle()
        args.putString("SELECT_CAT_EXP", "002")
        args.putString("AMNT_VL", "${binding.expAmount.text.toString()}")
        args.putString("NOTE_VL", "${binding.expNote.text.toString()}")
        ldf.setArguments(args)
        //Toast.makeText(requireContext(), "$args", Toast.LENGTH_SHORT).show()
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, ldf)
            ?.commit()
    }

    fun clearText() {
        binding.expAmount.setText("")
        binding.expNote.setText("")
        binding.expdate.setText("$date")
        binding.exptime.setText("$time")
        binding.expcategory.setText("")
        Toast.makeText(requireContext(), "Expense Added Successfully", Toast.LENGTH_SHORT).show()
        loadFragment(HomeFragment())
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.hideBottomNavigationView()
    }

    override fun onStop() {
        (activity as MainActivity?)!!.showBottomNavigationView()
        super.onStop()

    }

    fun validationOfData() {
        if (binding.expcategory.text.toString() == "Select Category" || binding.expcategory.text.toString()
                .isEmpty()
        ) {
            binding.expcategory.requestFocus()
            binding.expcategory.error = "Select Category"
        } else if (binding.expAmount.text.toString() == "Amount" || binding.expAmount.text.toString()
                .isEmpty()
        ) {
            binding.expAmount.requestFocus()
            binding.expAmount.error = "Empty"
        } else if (binding.expdate.text.toString()
                .isEmpty() || binding.expdate.text.toString() == "Date"
        ) {
            binding.expdate.requestFocus()
            binding.expdate.error = "Empty"
        } else if (binding.exppaymentMode.selectedItem.toString() == "Select Payment Mode"
        ) {
            binding.exppaymentMode.requestFocus()
            Toast.makeText(requireContext(), "Please Select Payment mode", Toast.LENGTH_SHORT)
                .show()
        } else if (binding.expNote.text.toString().isEmpty()) {
            binding.expNote.requestFocus()
            binding.expNote.error = "Empty"
        } else {
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            val amount = binding.expAmount.text.toString()
            val note = binding.expNote.text.toString()
            val category = binding.expcategory.text.toString()
            val paymentModes = binding.exppaymentMode.selectedItem as String
//            val categoryindex = binding.expcategory.selectedItemPosition.toString()
            val paymentModesIndex = binding.exppaymentMode.selectedItemPosition.toString()

            insertExpsnes(
                amount,
                category,
                date!!,
                time!!,
                paymentModes,
                paymentModesIndex,
                note,
                currentDate,
                sMonth!!
            )

            clearText()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun updateExpense(
        iD_: String?,
        caT_: String?,
        amnT_: String?,
        datE_: String?,
        timE_: String?,
        paY_: String?,
        payMd: String?,
        notE_: String?,
        smontH_: String?
    ) {
        val db = AppDataBase.getInstance(requireContext()).incexpTblDao()
        lifecycleScope.launch(Dispatchers.IO) {
            db.updateIncExpByID(
                amnT_!!,
                caT_!!,
                smontH_!!,
                datE_!!,
                timE_!!,
                paY_!!,
                payMd!!,
                notE_!!,
                iD_!!.toInt()
            )
        }
    }

    //    @BindingAdapter("spinnerError")
//    fun setSpinnerError(spinner: Spinner, error: String?) {
//        spinner.error = error
//    }
    fun spinnerSet(position: String) {
        PaymentModeList = java.util.ArrayList()
        val dao = AppDataBase.getInstance(requireContext()).paymentModesDao()

        dao.getAllPaymentMode().observe(requireActivity()) { paymentModes ->
            if (paymentModes != null) {
                if (paymentModes.isEmpty()) {
                    PaymentModeList.clear()
                    PaymentModeList.add(0, "Select Payment Mode")
                } else {
                    PaymentModeList.clear()
                    PaymentModeList.add(0, "Select Payment Mode")
                    for (paymentMode in paymentModes) {
                        val mode = paymentMode.paymentMode
                        if (mode != null) {
                            PaymentModeList.add(mode)
                        }
                    }
                    val arrayAdapter =
                        ArrayAdapter(
                            requireContext(),
                            R.layout.dropdown_item_layout,
                            PaymentModeList
                        )
                    binding.exppaymentMode.adapter = arrayAdapter

                    val spinnerPosition: Int = arrayAdapter.getPosition(position)
                    binding.exppaymentMode.setSelection(spinnerPosition)
                    Log.d("position", "$spinnerPosition")
                }
            }
        }
    }

    fun loadAd(
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

                                val AMNT_ = binding.expAmount.text.toString()
                                val NOTE_ = binding.expNote.text.toString()
                                val CAT_ = binding.expcategory.text.toString()
                                val PAY_ = binding.exppaymentMode.selectedItem as String
//            val categoryindex = binding.expcategory.selectedItemPosition.toString()
                                val PAY_MD_ = binding.exppaymentMode.selectedItemPosition.toString()
                                updateExpense(
                                    _ID,
                                    CAT_,
                                    AMNT_,
                                    DATE_,
                                    TIME_,
                                    PAY_,
                                    PAY_MD_,
                                    NOTE_,
                                    SMONTH_
                                )
                                Toast.makeText(
                                    requireContext(),
                                    "Expense Updated",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loadFragment(HomeFragment())
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