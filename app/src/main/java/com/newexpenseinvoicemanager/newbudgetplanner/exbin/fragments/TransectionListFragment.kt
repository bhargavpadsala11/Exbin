package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.MainActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.TransectionListAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.getCurrencyClass
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentTransectionListBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.Categories
import java.text.SimpleDateFormat
import java.util.*

class TransectionListFragment : Fragment() {


    private lateinit var binding: FragmentTransectionListBinding
    private var inc: Double = 0.0
    private var exp: Double = 0.0
    private var sDate: String = ""
    private var lDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTransectionListBinding.inflate(layoutInflater)

        val custom = binding.appBar
        custom.ivBack.setOnClickListener {
            loadFragment(HomeFragment())
        }
        custom.ivDelete.setImageResource(R.drawable.ic_filter)

        val currencyClass = getCurrencyClass(viewLifecycleOwner, requireContext())

        val dao = AppDataBase.getInstance(requireContext())
        val categoryMap = mutableMapOf<String, Categories>()
        dao.categoriesDao().getAllCategory().observe(requireActivity()) { categories ->
            categories.forEach { category ->
                categoryMap[category.CategoryName!!] = category
            }
        }

        val value = arguments?.getString("TRANSECTIONKEY")

        if (value == "INCOME") {
            custom.ivTitle.setText("Income Transection")
            getIncomeData(dao, categoryMap, currencyClass)
            custom.ivDelete.setOnClickListener {
                binding.clFliter.visibility = View.VISIBLE
            }
        } else if (value == "EXPENSE") {
            custom.ivTitle.setText("Expense Transection")
            getExpenseData(dao, categoryMap, currencyClass)
            custom.ivDelete.setOnClickListener {
                binding.clFliter.visibility = View.VISIBLE
            }
        }

        binding.tvStartdate.setOnClickListener {
            getStartDate()
        }

        binding.tvEnddate.setOnClickListener {
            getLastDate()
        }

        binding.tvAddStartdate.setOnClickListener {
            getStartDate()
        }

        binding.tvAddEnddate.setOnClickListener {
            getLastDate()
        }

        binding.btnApply.setOnClickListener {
            if (value == "INCOME") {
                dao.incexpTblDao().getAllIncomeDataByDate(sDate, lDate).observe(requireActivity()) {
                    binding.transectionRecy.adapter =
                        TransectionListAdapter(
                            requireContext(),
                            it,
                            categoryMap,
                            currencyClass
                        ) { value, mode ->

                        }
                }
            } else if (value == "EXPENSE") {
                dao.incexpTblDao().getAllExpenseDataByDate(sDate, lDate)
                    .observe(requireActivity()) {
                        binding.transectionRecy.adapter =
                            TransectionListAdapter(
                                requireContext(),
                                it,
                                categoryMap,
                                currencyClass
                            ) { value, mode ->

                            }
                    }
            }
        }

        binding.btnReset.setOnClickListener {
            sDate = ""
            lDate = ""
            if (value == "INCOME") {
                getIncomeData(dao, categoryMap, currencyClass)
                binding.clFliter.visibility = View.GONE
            } else if (value == "EXPENSE") {
                getExpenseData(dao, categoryMap, currencyClass)
                binding.clFliter.visibility = View.GONE
            }


        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.hideBottomNavigationView()
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity?)!!.showBottomNavigationView()
    }

//    fun getTotalIncome() {
//        val dao = AppDataBase.getInstance(requireContext()).incexpTblDao()
//
//        dao.getTotalIncomeAmount().observe(requireActivity()) { income ->
//            if (income != null) {
//                inc = income
//                val formattedAvg = String.format("%.2f", inc)
//                binding.totalIncome.setText(formattedAvg)
//            } else {
//                // handle empty income
//                binding.totalIncome.setText("00.00")
//            }
//        }
//    }
//
//    fun getTotalExpense() {
//        val dao = AppDataBase.getInstance(requireContext()).incexpTblDao()
//
//        dao.getTotalExpenseAmount().observe(requireActivity()) { expense ->
//            if (expense != null) {
//                exp = expense
//                val formattedAvg = String.format("%.2f", exp)
//                binding.totalIncome.setText(formattedAvg)
//
//            } else {
//                // handle empty expense
//                binding.totalIncome.setText("00.00")
//            }
//        }
//    }
//
//    fun getDailyAvg() {
//        val dao = AppDataBase.getInstance(requireContext()).incexpTblDao()
//
//        dao.getDailyAverage().observe(requireActivity()) { dailyAverage ->
//            if (dailyAverage != null) {
//                val avg = dailyAverage
//                val formattedAvg = String.format("%.2f", avg)
//                binding.avgTtl.text = formattedAvg
//            } else {
//                binding.avgTtl.text = "00.00"
//            }
//        }
//    }
//
//    fun getDailyAvgExp() {
//        val dao = AppDataBase.getInstance(requireContext()).incexpTblDao()
//
//        dao.getDailyAverageExp().observe(requireActivity()) { dailyAverage ->
//            if (dailyAverage != null) {
//                val avg = dailyAverage
//                val formattedAvg = String.format("%.2f", avg)
//                binding.avgTtl.text = formattedAvg
//            } else {
//                binding.avgTtl.text = "00.00"
//            }
//        }
//    }

    private fun loadFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    fun getLastDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                lDate =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                binding.tvAddEnddate.setText("$lDate")

            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }

    fun getStartDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                sDate =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                binding.tvStartdate.setText("$sDate")

            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }

    fun getExpenseData(
        dao: AppDataBase,
        categoryMap: MutableMap<String, Categories>,
        currencyClass: getCurrencyClass
    ) {
        dao.incexpTblDao().getAllExpenseData().observe(requireActivity()) {
            binding.transectionRecy.adapter =
                TransectionListAdapter(
                    requireContext(),
                    it,
                    categoryMap,
                    currencyClass
                ) { value, mode ->

                }
        }

    }

    fun getIncomeData(
        dao: AppDataBase,
        categoryMap: MutableMap<String, Categories>,
        currencyClass: getCurrencyClass
    ) {


        dao.incexpTblDao().getAllIncomeData().observe(requireActivity()) {
            binding.transectionRecy.adapter =
                TransectionListAdapter(
                    requireContext(),
                    it,
                    categoryMap,
                    currencyClass
                ) { value, mode ->

                }
        }

    }

}