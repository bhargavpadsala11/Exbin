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
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.ActivityExpenseBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.incexpTbl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class ExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExpenseBinding
    private lateinit var categoryList: ArrayList<String>
    private lateinit var PaymentModeList: ArrayList<String>
    private lateinit var date: String
    private lateinit var time: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Expense"
        getCategory()
        getPaymentMode()

        val calendar = Calendar.getInstance()

        binding.expdate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
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
                this,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
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
            val currentDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.now().toString()
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val amount = binding.expAmount.text.toString()
            val note = binding.expNote.text.toString()
            val category = binding.expcategory.selectedItem as String
            val paymentModes = binding.exppaymentMode.selectedItem as String
            insertExpsnes(amount, category, date, time, paymentModes, note, currentDateTime)
            clearText()
        }

    }

    fun insertExpsnes(
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
            dType = "EXPENSE",
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
                    binding.exppaymentMode.adapter = arrayAdapter
                }
            }
        }
    }


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
                    binding.expcategory.adapter = arrayAdapter
                }
            }
        }
    }

    fun clearText() {
        binding.expAmount.setText("")
        binding.expNote.setText("")
        binding.expdate.setText("")
        binding.exptime.setText("")
        categoryList.clear()
        PaymentModeList.clear()
        Toast.makeText(this, "Expense Added Successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}