package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.button.MaterialButton
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.MainActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.getCurrencyClass
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.ActivityIncomeBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.Categories
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.incexpTbl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.FieldPosition
import java.text.SimpleDateFormat
import java.util.*


class IncomeActivity : Fragment() {
    private lateinit var binding: ActivityIncomeBinding
    private lateinit var categoryList: ArrayList<String>
    private lateinit var PaymentModeList: ArrayList<String>
    private var date: String? = ""
    private var time: String? = ""
    private var c: String = ""
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
    private var isAds: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityIncomeBinding.inflate(layoutInflater)
        (activity as MainActivity?)!!.floatButtonHide()
        (activity as MainActivity?)!!.getIdofNativeAds()

        val custom = binding.appBar
        custom.ivDelete.visibility = View.GONE
        custom.ivTitle.setText("Add Income")
        custom.ivBack.setOnClickListener {
            (activity as MainActivity?)!!.setBottomNavigationAsHome()
            loadFragment(HomeFragment())
        }
        val preference =
            requireContext().getSharedPreferences("NativeId", AppCompatActivity.MODE_PRIVATE)
        FireBaseGooggleAdsInterId = preference.getString("inter_id", "")!!
        FireBaseGooggleAdsBanner = preference.getString("banner_Key", "")!!
        isAds = preference.getBoolean("isShow", false)


        if (isAds == true) {
            val adContainer = binding.adView
            val mAdView = AdView(requireContext())
            mAdView.setAdSize(AdSize.BANNER)
            mAdView.adUnitId = FireBaseGooggleAdsBanner
            adContainer.addView(mAdView)
            val adRequest = AdRequest.Builder().build()
            mAdView.loadAd(adRequest)

            mAdView.adListener = object : AdListener() {
                override fun onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                override fun onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
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

        }

        val SELECTED_CATEGORY = arguments?.getString("CATEGORY")
        val INC_ = arguments?.getString("INC_")

        if (INC_ != null) {

            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            custom.ivTitle.setText("Update Income")
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
            Log.d("PAY_", "$PAY_")
            _ID = ID_

            if (isAds) {
                loadAd()
            }
            binding.category.setOnClickListener { getCategoryForUpdate() }
            spinnerSet(PAY_!!)
            //PaymentModeList.set(PAY_MD_!!.toInt(),PAY_!!)
            binding.incAmount.setText(AMNT_)
            binding.incdate.setText(DATE_)
            binding.category.setText(CAT_)
            binding.incNote.setText(NOTE_)
            binding.inctime.setText(TIME_)
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
                hintText?.setText("Are you sure you want to delete this Income Item?")
                deleteBtn?.setOnClickListener {
                    val db = AppDataBase.getInstance(requireContext()).incexpTblDao()
                    lifecycleScope.launch(Dispatchers.IO) {
                        db.deleteincomeexpenseId(ID_!!.toInt())
                    }
                    container?.removeView(deleteCategoryView)
                    val ldf = HomeFragment()
                    (activity as MainActivity?)!!.setBottomNavigationAsHome()
                    loadFragment(ldf)
//                    val transaction = activity?.supportFragmentManager?.beginTransaction()
//                    transaction?.replace(R.id.fragment_container, ldf)
//                    transaction?.disallowAddToBackStack()
//                    transaction?.commit()
                }
                cancelBtn?.setOnClickListener {
                    container?.removeView(deleteCategoryView)
                }
            }
            binding.incdate.setOnClickListener {
                val datePicker = DatePickerDialog(
                    requireContext(),
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        DATE_ = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(calendar.time)
                        // Store the date in Room database
                        binding.incdate.text = DATE_
                        val selectedDateString = binding.incdate.text.toString()
                        val selectedDate = sdf.parse(selectedDateString)
                        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
                        val monthName = monthFormat.format(selectedDate)
                        SMONTH_ = monthName
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.show()
            }

            binding.inctime.setOnClickListener {
                val timePicker = TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        TIME_ =
                            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
                        // Store the time in Room database
                        binding.inctime.text = TIME_

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
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val sdf_1 = SimpleDateFormat("hh:mm a")
            val defaulttDate = sdf.format(Date())
            val defaultTime = sdf_1.format(Date())
            // Get the dynamically selected date as a string


            date = defaulttDate
            time = defaultTime
            val amnt = arguments?.getString("amnt_vle")
            val nte = arguments?.getString("nte_vle")
            val dte = arguments?.getString("dte_vle")
            val tme = arguments?.getString("tme_vle")
            val py_ind_ = arguments?.getString("pay_ind_vle")
            binding.incAmount.setText(amnt)
            binding.incNote.setText(nte)
            if (tme != null) {

                binding.inctime.setText(tme)
                time = binding.inctime.text.toString()
            }else{
                binding.inctime.setText(defaultTime)
            }
            spinnerSet(py_ind_!!)



            if (dte != null) {
                binding.incdate.setText(dte)
                date = binding.incdate.text.toString()
            } else {
                binding.incdate.setText(defaulttDate)
            }

            val selectedDateString = binding.incdate.text.toString()
            val selectedDate = sdf.parse(selectedDateString)
            val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
            val monthName = monthFormat.format(selectedDate)
            sMonth = monthName

            binding.category.setOnClickListener {
                getCategory()
            }
            binding.category.setText(SELECTED_CATEGORY)
//            Toast.makeText(requireContext(), "$SELECTED_CATEGORY", Toast.LENGTH_SHORT).show()

            val calendar = Calendar.getInstance()

            binding.incdate.setOnClickListener {
                val datePicker = DatePickerDialog(
                    requireContext(),
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        date = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(calendar.time)
                        // Store the date in Room database
                        binding.incdate.text = date
                        val selectedDateString = binding.incdate.text.toString()
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

            binding.inctime.setOnClickListener {
                val timePicker = TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        time =
                            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
                        // Store the time in Room database
                        binding.inctime.text = time

                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )
                timePicker.show()
            }

            binding.addIncomeBtn.setOnClickListener {

                validationOfData()

            }
        } else {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val sdf_1 = SimpleDateFormat("hh:mm a")
            val defaulttDate = sdf.format(Date())
            val defaultTime = sdf_1.format(Date())
            date = defaulttDate
            time = defaultTime
            binding.incdate.setText(defaulttDate)
            binding.inctime.setText(defaultTime)
            val selectedDateString = binding.incdate.text.toString()
            val selectedDate = sdf.parse(selectedDateString)
            val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
            val monthName = monthFormat.format(selectedDate)
            sMonth = monthName

            binding.category.setOnClickListener {
                getCategory()
            }
            binding.category.setOnClickListener { getCategory() }
            getPaymentMode()
            val calendar = Calendar.getInstance()

            binding.incdate.setOnClickListener {
                val datePicker = DatePickerDialog(
                    requireContext(),
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        date = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(calendar.time)
                        // Store the date in Room database
                        binding.incdate.text = date
                        val selectedDateString = binding.incdate.text.toString()
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

            binding.inctime.setOnClickListener {
                val timePicker = TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        time =
                            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
                        // Store the time in Room database
                        binding.inctime.text = time

                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )
                timePicker.show()
            }

            binding.addIncomeBtn.setOnClickListener {
                validationOfData()
            }
        }

        return binding.root
    }

    private fun getCategoryForUpdate() {
        val ldf = CategoryListFragment()
        val args = Bundle()
        args.putString("SELECT_CAT_INC_FOR_UPDATE", "11")
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

    fun validationOfData() {
        if (binding.category.text.toString() == "Select Category" || binding.category.text.toString()
                .isEmpty()
        ) {
            binding.category.requestFocus()
            binding.category.error = "Select Category"
        } else if (binding.incAmount.text.toString() == "Amount" || binding.incAmount.text.toString()
                .isEmpty()
        ) {
            binding.incAmount.requestFocus()
            binding.incAmount.error = "Empty"
        } else if (binding.incdate.text.toString()
                .isEmpty() || binding.incdate.text.toString() == "Date"
        ) {
            binding.incdate.requestFocus()
            binding.incdate.error = "Empty"
        } else if (binding.paymentMode.selectedItem.toString() == "Select Payment Mode"
        ) {
            binding.paymentMode.requestFocus()
            Toast.makeText(requireContext(), "Please Select Payment mode", Toast.LENGTH_SHORT)
                .show()
        } else if (binding.incNote.text.toString().isEmpty()) {
            binding.incNote.requestFocus()
            binding.incNote.error = "Empty"
        } else {

            val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            val amount = binding.incAmount.text.toString()
            val note = binding.incNote.text.toString()
            val category = binding.category.text.toString()
//                val categoryindex = binding.category.selectedItemPosition.toString()
            val paymentModes = binding.paymentMode.selectedItem as String
            val paymentModesIndex = binding.paymentMode.selectedItemPosition.toString()
            insertIncome(
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

    fun validationOfDataForUpdate(
        ID_: String?,
        CAT_: String?,
        AMNT_: String?,
        DATE_: String?,
        TIME_: String?,
        PAY_: String?,
        PAY_MD_: String?,
        NOTE_: String?,
        SMONTH_: String?
    ) {
        if (binding.category.text.toString() == "Select Category" || binding.category.text.toString()
                .isEmpty()
        ) {
            binding.category.requestFocus()
            binding.category.error = "Select Category"
        } else if (binding.incAmount.text.toString() == "Amount" || binding.incAmount.text.toString()
                .isEmpty()
        ) {
            binding.incAmount.requestFocus()
            binding.incAmount.error = "Empty"
        } else if (binding.incdate.text.toString()
                .isEmpty() || binding.incdate.text.toString() == "Date"
        ) {
            binding.incdate.requestFocus()
            binding.incdate.error = "Empty"
        } else if (binding.paymentMode.selectedItem.toString() == "Select Payment Mode"
        ) {
            binding.paymentMode.requestFocus()
            Toast.makeText(requireContext(), "Please Select Payment mode", Toast.LENGTH_SHORT)
                .show()
        } else if (binding.incNote.text.toString().isEmpty()) {
            binding.incNote.requestFocus()
            binding.incNote.error = "Empty"
        } else {
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(requireActivity())
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")

                val AMNT_ = binding.incAmount.text.toString()
                val NOTE_ = binding.incNote.text.toString()
                val PAY_ = binding.paymentMode.selectedItem as String
                val PAY_MD_ = binding.paymentMode.selectedItemPosition.toString()
                updateIncome(ID_, CAT_, AMNT_, DATE_, TIME_, PAY_, PAY_MD_, NOTE_, SMONTH_)
                Toast.makeText(requireContext(), "Income Updated", Toast.LENGTH_SHORT).show()
                loadFragment(HomeFragment())
            }

        }
    }

    private fun updateIncome(
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

    fun insertIncome(
        amount: String,
        category: String,
        date: String,
        time: String,
        paymentMode: String,
        paymentModeIndex: String,
        note: String,
        currentDateTime: String,
        SmOnth: String
    ) {
        val db = AppDataBase.getInstance(requireContext()).incexpTblDao()
        val data = incexpTbl(
            amount = amount,
            category = category,
            sMonth = SmOnth,
            date = date,
            time = time,
            paymentMode = paymentMode,
            paymentModeIndex = paymentModeIndex,
            note = note,
            dType = "INCOME",
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

                    binding.paymentMode.adapter = arrayAdapter

                }
            }
        }
    }

    fun getCategory() {
        val ldf = CategoryListFragment()
        val args = Bundle()
        args.putString("SELECT_CAT_INC", "001")
        args.putString("AMNT_VL", "${binding.incAmount.text.toString()}")
        args.putString("NOTE_VL", "${binding.incNote.text.toString()}")
        args.putString("DATE_VL", "$date")
        args.putString("TIME_VL", "$time")
        args.putString("PAY_IND_VL", "${binding.paymentMode.selectedItem as String}")

        ldf.setArguments(args)
        //Toast.makeText(requireContext(), "$args", Toast.LENGTH_SHORT).show()
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, ldf)
            ?.commit()

    }

    fun clearText() {
        binding.incAmount.setText("")
        binding.incNote.setText("")
        binding.incdate.setText("$date")
        binding.inctime.setText("$time")
        binding.category.setText("")
        Toast.makeText(requireContext(), "Income Added Successfully", Toast.LENGTH_SHORT).show()
        loadFragment(HomeFragment())
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.hideBottomNavigationView()
    }

    override fun onStop() {
        (activity as MainActivity?)!!.showBottomNavigationView()
        super.onStop()
        deleteCategoryView?.visibility = View.GONE
    }

    private fun loadFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    fun spinnerSet(position: String) {
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
                    binding.paymentMode.adapter = arrayAdapter

                    val spinnerPosition: Int = arrayAdapter.getPosition(position)
                    binding.paymentMode.setSelection(spinnerPosition)
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
                    Log.d(TAG, adError?.toString()!!)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(TAG, "Ad was clicked.")
                            }

                            override fun onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                Log.d(TAG, "Ad dismissed fullscreen content.")

                                mInterstitialAd = null
                                val AMNT_ = binding.incAmount.text.toString()
                                val NOTE_ = binding.incNote.text.toString()
                                val PAY_ = binding.paymentMode.selectedItem as String
                                val PAY_MD_ = binding.paymentMode.selectedItemPosition.toString()
                                updateIncome(
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
                                    "Income Updated",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loadFragment(HomeFragment())

                            }

                            override fun onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(TAG, "Ad recorded an impression.")
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad showed fullscreen content.")
                            }
                        }

                }
            })
    }
}