package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.BudgetAndExpenseAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
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
    private var currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH)
    var monthName: String = DateFormatSymbols().months[currentMonth]

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBudgetBinding.inflate(layoutInflater)

        setBudgetView()
        binding.ydtextView.visibility = View.GONE
        binding.budgetRecy.visibility = View.VISIBLE
        getCategory()
        binding.materialButton.setOnClickListener {
            binding.createBudget.visibility = View.GONE
            binding.saveBudget.visibility = View.VISIBLE
        }

        val monthName = DateFormatSymbols().months[currentMonth]
        binding.monthTxt.text = monthName

        // Set up the back button
        binding.backBtn.setOnClickListener {
            currentMonth--
            if (currentMonth < 0) {
                currentMonth = 11
            }
            val monthName = DateFormatSymbols().months[currentMonth]
            binding.monthTxt.text = monthName
        }

        // Set up the next button
        binding.nxtBtn.setOnClickListener {
            currentMonth++
            if (currentMonth > 11) {
                currentMonth = 0
            }
            val monthName = DateFormatSymbols().months[currentMonth]
            binding.monthTxt.text = monthName
        }


        binding.materialButton1.setOnClickListener {
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            val amount = binding.amountText.text.toString()
            insertBudget(
                amount,
                binding.categorySpin.selectedItem as String,
                selectedCatColor,
                currentDate
            )
            clearText()
        }


        return binding.root
    }

    fun insertBudget(
        amount: String,
        category: String,
        catgoryColor: String,
        date: String
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
            month = month_name

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

    fun setBudgetView() {
        val adapter = BudgetAndExpenseAdapter(emptyList())
        binding.budgetRecy.adapter = adapter
        val dao = AppDataBase.getInstance(requireContext()).budgetDao()
        lifecycleScope.launch {
            val budgetAndExpenseList = dao.getBudgetAndExpense()
            adapter.budgetAndExpenseList = budgetAndExpenseList
            adapter.notifyDataSetChanged()
        }
    }

    fun clearText() {
        binding.amountText.setText("")
        Toast.makeText(requireContext(), "Budget Added Successfully", Toast.LENGTH_SHORT).show()
    }




}