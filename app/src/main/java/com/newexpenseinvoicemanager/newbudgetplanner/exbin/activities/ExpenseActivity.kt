package com.newexpenseinvoicemanager.newbudgetplanner.exbin.activities

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

//        supportActionBar?.title = "Expense"
//        value = intent.getStringExtra("value")
//        val SELECTED_CATEGORY = intent.getStringExtra("CATEGORY_NAME_1")
//
//        if (value != null) {
//            val id = intent.getStringExtra("id")
//            val amt = intent.getStringExtra("amt")
//            val cty = intent.getStringExtra("cty")
//            val dt = intent.getStringExtra("dt")
//            val pmd = intent.getStringExtra("pmd")
//            val nt = intent.getStringExtra("nt")
//            val time = intent.getStringExtra("time")
//            val ctyInd = intent.getStringExtra("ctyInd")
//            val pmInd = intent.getStringExtra("pmInd")
//
//        } else if (SELECTED_CATEGORY != null){
//            binding.expcategory.setOnClickListener { getCategory() }
//            binding.expcategory.setText(SELECTED_CATEGORY)
//
//            val calendar = Calendar.getInstance()
//
//            binding.expdate.setOnClickListener {
//                val datePicker = DatePickerDialog(
//                    this,
//                    { _, year, month, dayOfMonth ->
//                        calendar.set(year, month, dayOfMonth)
//                        date = SimpleDateFormat(
//                            "dd/MM/yyyy",
//                            Locale.getDefault()
//                        ).format(calendar.time)
//                        // Store the date in Room database
//                        binding.expdate.text = date
//
//                    },
//                    calendar.get(Calendar.YEAR),
//                    calendar.get(Calendar.MONTH),
//                    calendar.get(Calendar.DAY_OF_MONTH)
//                )
//                datePicker.show()
//            }
//
//            binding.exptime.setOnClickListener {
//                val timePicker = TimePickerDialog(
//                    this,
//                    { _, hourOfDay, minute ->
//                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
//                        calendar.set(Calendar.MINUTE, minute)
//                        time =
//                            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
//                        // Store the time in Room database
//                        binding.exptime.text = time
//
//                    },
//                    calendar.get(Calendar.HOUR_OF_DAY),
//                    calendar.get(Calendar.MINUTE),
//                    false
//                )
//                timePicker.show()
//            }
//
//            binding.addIncomeBtn.setOnClickListener {
//                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
//                val currentDate = sdf.format(Date())
//                val amount = binding.expAmount.text.toString()
//                val note = binding.expNote.text.toString()
//                val category = binding.expcategory.text.toString()
//                val paymentModes = binding.exppaymentMode.selectedItem as String
////            val categoryindex = binding.expcategory.selectedItemPosition.toString()
//                val paymentModesIndex = binding.exppaymentMode.selectedItemPosition.toString()
//
//                insertExpsnes(
//                    amount,
//                    category,
//                    date,
//                    time,
//                    paymentModes,
//                    paymentModesIndex,
//                    note,
//                    currentDate
//                )
//
//                clearText()
//                // checkValidation(amount, category, date, time, paymentModes, note, currentDateTime)
//            }
//        } else {
//
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
                // checkValidation(amount, category, date, time, paymentModes, note, currentDateTime)
            }


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
                        ArrayAdapter(requireContext(), R.layout.dropdown_item_layout, PaymentModeList)
                    binding.exppaymentMode.adapter = arrayAdapter
                }
            }
        }
    }

    fun getCategory() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra("SELECT_CATEGORY_01", "TRUE")
        startActivity(intent)


//        val args = Bundle()
//        val fragmnet = AddCategoriesFragment()
//        args.putString("SELECT_CATEGORY","TRUE")
//        fragmnet.setArguments(args)
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, fragmnet)
//            .addToBackStack(null)
//            .commit()
    }

    fun clearText() {
        binding.expAmount.setText("")
        binding.expNote.setText("")
        binding.expdate.setText("")
        binding.exptime.setText("")
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

}