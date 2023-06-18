package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.MainActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.ActivityIncomeBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments.AddCategoriesFragment
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments.CategoryListFragment
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments.HomeFragment
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.incexpTbl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class IncomeActivity : Fragment() {
    private lateinit var binding: ActivityIncomeBinding
    private lateinit var categoryList: ArrayList<String>
    private lateinit var PaymentModeList: ArrayList<String>
    private lateinit var date: String
    private lateinit var time: String
    private var vl: String? = ""
    private var p: String = ""
    private var pdm: String = ""
    private var c: String = ""
    private var value: String? = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityIncomeBinding.inflate(layoutInflater)

//        supportActionBar?.title = "Income"
//
        value = arguments?.getString("value")
        val SELECTED_CATEGORY = arguments?.getString("CATEGORY")
//        val value = arguments?.getString("CATEGORY_NAME_1")
//        Toast.makeText(requireContext(), "$value", Toast.LENGTH_SHORT).show()


        if (value != null) {
//            vl = value!!
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
//            p = pmInd!!
//            c = ctyInd!!
//            pdm = pmd!!
//            val addIncomeBtn = binding.addIncomeBtn
//            addIncomeBtn.setText("Update Income")
//            getPaymentMode()
//
//            binding.incAmount.setText(amt)
//            binding.incdate.setText(dt)
//            binding.inctime.setText(time)
//            binding.incNote.setText(nt)

        } else if (SELECTED_CATEGORY != null) {

            binding.category.setOnClickListener {
                getCategory()
            }
            binding.category.setText(SELECTED_CATEGORY)
            Toast.makeText(requireContext(), "$SELECTED_CATEGORY", Toast.LENGTH_SHORT).show()
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

                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())
                System.out.println(" C DATE is  " + currentDate)
                val amount = binding.incAmount.text.toString()
                val note = binding.incNote.text.toString()
                val category = binding.category.text.toString()
                // val categoryindex = binding.category.selectedItemPosition.toString()
                val paymentModes = binding.paymentMode.selectedItem as String
                val paymentModesIndex = binding.paymentMode.selectedItemPosition.toString()
                insertIncome(
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
        } else {
            binding.category.setOnClickListener {
                getCategory()
            }
            binding.category.setOnClickListener { getCategory() }
            getPaymentMode()
            if (value != null) {
                binding.category.setText(value)
              //  Toast.makeText(requireContext(), "$value", Toast.LENGTH_SHORT).show()


            }
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

                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val currentDate = sdf.format(Date())
                System.out.println(" C DATE is  " + currentDate)
                val amount = binding.incAmount.text.toString()
                val note = binding.incNote.text.toString()
                val category = binding.category.text.toString()
//                val categoryindex = binding.category.selectedItemPosition.toString()
                val paymentModes = binding.paymentMode.selectedItem as String
                val paymentModesIndex = binding.paymentMode.selectedItemPosition.toString()
                insertIncome(
                    amount,
                    category,
                    date,
                    time,
                    paymentModes,
                    paymentModesIndex,
                    note,
                    currentDate
                )

//                clearText()
            }
        }
        binding.category.setOnClickListener {
            getCategory()
        }

        return binding.root
    }

    fun insertIncome(
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
//                    if (vl != null){
//                        if (p != null) {
//                            Toast.makeText(requireContext(), "$pdm", Toast.LENGTH_SHORT).show()
//                            val position = PaymentModeList.indexOf(p)
//                            if (position != -1) {
//                                Toast.makeText(requireContext(), "$pdm", Toast.LENGTH_SHORT).show()
//                                binding.paymentMode.setSelection(position)
//                            }
//                        }
//
//                    }
                }
            }
        }
    }

    fun getCategory() {
//        val intent = Intent(requireContext(), MainActivity::class.java)
//        intent.putExtra("SELECT_CATEGORY_01", "TRUE")
//        startActivity(intent)
        val ldf = CategoryListFragment()
        val args = Bundle()
        args.putString("SELECT_CAT_INC", "001")
        ldf.setArguments(args)
        //Toast.makeText(requireContext(), "$args", Toast.LENGTH_SHORT).show()
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, ldf)
            ?.commit()

    }

    fun clearText() {
        binding.incAmount.setText("")
        binding.incNote.setText("")
        binding.incdate.setText("")
        binding.inctime.setText("")
        binding.category.setText("")
        Toast.makeText(requireContext(), "Income Added Successfully", Toast.LENGTH_SHORT).show()
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