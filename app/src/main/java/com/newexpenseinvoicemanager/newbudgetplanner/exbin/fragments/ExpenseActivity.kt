package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
    private lateinit var date: String
    private lateinit var time: String
    private var value: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityExpenseBinding.inflate(layoutInflater)
        val custom = binding.appBar
        custom.ivDelete.visibility = View.GONE
        custom.ivTitle.setText("Add Expense")
        custom.ivBack.setOnClickListener {
            loadFragment(HomeFragment())
        }
//        supportActionBar?.title = "Expense"
//        value = intent.getStringExtra("value")
        value = arguments?.getString("value")

        val SELECTED_CATEGORY = arguments?.getString("CATEGORY1")

        if (value != null) {
//            val id = intent.getStringExtra("id")
//            val amt = intent.getStringExtra("amt")
//            val cty = intent.getStringExtra("cty")
//            val dt = intent.getStringExtra("dt")
//            val pmd = intent.getStringExtra("pmd")
//            val nt = intent.getStringExtra("nt")
//            val time = intent.getStringExtra("time")
//            val ctyInd = intent.getStringExtra("ctyInd")
//            val pmInd = intent.getStringExtra("pmInd")

        } else if (SELECTED_CATEGORY != null) {
            val amnt = arguments?.getString("amnt_vle")
            val nte = arguments?.getString("nte_vle")
            if (amnt != null && nte != null){
                binding.expAmount.setText(amnt)
                binding.expNote.setText(nte)
            }else if(amnt == null && nte != null){
                binding.expNote.setText(nte)
            }else if(nte == null && amnt != null){
                binding.expAmount.setText(amnt)
            }
            val sdf = SimpleDateFormat("dd/M/yyyy")
            val sdf_1 = SimpleDateFormat("hh:mm a")
            val defaulttDate = sdf.format(Date())
            val defaultTime = sdf_1.format(Date())
            binding.expdate.setText(defaulttDate)
            binding.exptime.setText(defaultTime)
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
            binding.expdate.setText(defaulttDate)
            binding.exptime.setText(defaultTime)
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
        binding.expcategory.setOnClickListener { getCategory() }
        return binding.root
    }


    fun insertExpsnes(
        amount: String,
        category: String,
        date: String,
        time: String,
        paymentMode: String,
        paymentModeIndex: String,
        note: String,
        currentDateTime: String
    ) {
        val db = AppDataBase.getInstance(requireContext()).incexpTblDao()
        val data = incexpTbl(
            amount = amount,
            category = category,
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
        args.putString("AMNT_VL","${binding.expAmount.text.toString()}")
        args.putString("NOTE_VL","${binding.expNote.text.toString()}")
        ldf.setArguments(args)
        //Toast.makeText(requireContext(), "$args", Toast.LENGTH_SHORT).show()
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, ldf)
            ?.commit()
    }

    fun clearText() {
        binding.expAmount.setText("")
        binding.expNote.setText("")
        binding.expdate.setText("")
        binding.exptime.setText("")
        binding.expcategory.setText("")
        Toast.makeText(requireContext(), "Expense Added Successfully", Toast.LENGTH_SHORT).show()
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
                date,
                time,
                paymentModes,
                paymentModesIndex,
                note,
                currentDate
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
//    @BindingAdapter("spinnerError")
//    fun setSpinnerError(spinner: Spinner, error: String?) {
//        spinner.error = error
//    }

}