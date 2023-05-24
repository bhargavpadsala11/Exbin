package com.newexpenseinvoicemanager.newbudgetplanner.exbin.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.ActivityIncomeBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.incexpTbl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class IncomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIncomeBinding
    private lateinit var categoryList: ArrayList<String>
    private lateinit var PaymentModeList: ArrayList<String>
    private lateinit var date: String
    private lateinit var time: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Income"
        getCategory()
        getPaymentMode()

        val calendar = Calendar.getInstance()

        binding.incdate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                    // Store the date in Room database
                    binding.incdate.text = date

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        binding.inctime.setOnClickListener {
            val timePicker = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
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
            val currentDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.now().toString()
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val amount = binding.incAmount.text.toString()
            val note = binding.incNote.text.toString()
            val category = binding.category.selectedItem as String
            val paymentModes = binding.paymentMode.selectedItem as String
            insertIncome(amount, category, date, time, paymentModes, note, currentDateTime)

           // checkValidation(amount, category, date, time, paymentModes, note, currentDateTime)
            clearText()
        }

    }

    fun insertIncome(
        amount: String,
        category: String,
        date: String,
        time: String,
        paymentMode: String,
        note: String,
        currentDateTime: String
    ) {
        val db = AppDataBase.getInstance(this).incexpTblDao()
        val data = incexpTbl(
            amount = amount,
            category = category,
            date = date,
            time = time,
            paymentMode = paymentMode,
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
        val dao = AppDataBase.getInstance(this).paymentModesDao()

        dao.getAllPaymentMode().observe(this) { paymentModes ->
            if (paymentModes != null) {
                if (paymentModes.isEmpty()) {
                    PaymentModeList.clear()
                    PaymentModeList.add(0, "Select Payment Mode")
                } else {
                    PaymentModeList.clear()
                    for (paymentMode in paymentModes) {
                        val mode = paymentMode.paymentMode
                        if (mode != null) {
                            PaymentModeList.add(mode)
                        }
                    }
                    val arrayAdapter =
                        ArrayAdapter(this, R.layout.dropdown_item_layout, PaymentModeList)
                    binding.paymentMode.adapter = arrayAdapter
                }
            }
        }
    }

//    fun checkValidation(amount: String,
//                        category: String,
//                        date: String,
//                        time: String,
//                        paymentMode: String,
//                        note: String,
//                        currentDateTime: String){
//        if(binding.incAmount.text.toString().isEmpty()){
//            Toast.makeText(
//                this,
//                "Please Enter Amount",
//                Toast.LENGTH_SHORT
//            ).show()
//        }else if (binding.category.selectedItem.toString().isBlank()){
//            Toast.makeText(
//                this,
//                "Please Select Category",
//                Toast.LENGTH_SHORT
//            ).show()
//        } else if(binding.incdate.text.toString().isEmpty()) {
//            Toast.makeText(
//                this,
//                "Please Select Date",
//                Toast.LENGTH_SHORT
//            ).show()
//        } else if (binding.inctime.text.toString().isEmpty()){
//            Toast.makeText(
//                this,
//                "Please Select Time",
//                Toast.LENGTH_SHORT
//            ).show()
//        }else if (binding.paymentMode.selectedItem.toString().isBlank()){
//            Toast.makeText(
//                this,
//                "Please Select Payment Mode",
//                Toast.LENGTH_SHORT
//            ).show()
//        }else if(binding.incNote.text.toString().isEmpty()){
//            Toast.makeText(
//                this,
//                "Please write Note",
//                Toast.LENGTH_SHORT
//            ).show()
//        }else{
//            insertIncome(amount, category, date, time, paymentMode, note, currentDateTime)
//        }
//    }

    fun getCategory() {
        categoryList = ArrayList()
        val dao = AppDataBase.getInstance(this).categoriesDao()

        dao.getAllCategory().observe(this) { categories ->
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
                        ArrayAdapter(this, R.layout.dropdown_item_layout, categoryList)
                    binding.category.adapter = arrayAdapter
                }
            }
        }
    }

    fun clearText() {
        binding.incAmount.setText("")
        binding.incNote.setText("")
        binding.incdate.setText("")
        binding.inctime.setText("")
        Toast.makeText(this, "Income Added Successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}