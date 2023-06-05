package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.os.Binder
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

class TransectionListFragment : Fragment() {


    private lateinit var binding: FragmentTransectionListBinding
    private var inc: Double = 0.0
    private var exp: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTransectionListBinding.inflate(layoutInflater)
        val currencyClass = getCurrencyClass(viewLifecycleOwner, requireContext())
        val value = arguments?.getString("TRANSECTIONKEY")
        if (value == "INCOME") {
            getTotalIncome()
            getDailyAvg()
            binding.crdTrns.visibility = View.VISIBLE
            binding.crdTrns.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transectionGreen))
            val dao = AppDataBase.getInstance(requireContext())
            val categoryMap = mutableMapOf<String, Categories>()
            dao.categoriesDao().getAllCategory().observe(requireActivity()) { categories ->
                categories.forEach { category ->
                    categoryMap[category.CategoryName!!] = category
                }
            }
            dao.incexpTblDao().getAllIncomeData().observe(requireActivity()) {
                binding.transectionRecy.adapter =
                    TransectionListAdapter(requireContext(), it, categoryMap,currencyClass)
            }
        } else if (value == "EXPENSE") {
            getDailyAvgExp()
            getTotalExpense()
            binding.crdTrns.visibility = View.VISIBLE
            binding.crdTrns.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transectionRed))
            val dao = AppDataBase.getInstance(requireContext())
            val categoryMap = mutableMapOf<String, Categories>()
            dao.categoriesDao().getAllCategory().observe(requireActivity()) { categories ->
                categories.forEach { category ->
                    categoryMap[category.CategoryName!!] = category
                }
            }
            dao.incexpTblDao().getAllExpenseData().observe(requireActivity()) {
                binding.transectionRecy.adapter =
                    TransectionListAdapter(requireContext(), it, categoryMap,currencyClass)
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

    fun getTotalIncome() {
        val dao = AppDataBase.getInstance(requireContext()).incexpTblDao()

        dao.getTotalIncomeAmount().observe(requireActivity()) { income ->
            if (income != null) {
                inc = income
                val formattedAvg = String.format("%.2f", inc)
                binding.totalIncome.setText(formattedAvg)
            } else {
                // handle empty income
                binding.totalIncome.setText("00.00")
            }
        }
    }

    fun getTotalExpense() {
        val dao = AppDataBase.getInstance(requireContext()).incexpTblDao()

        dao.getTotalExpenseAmount().observe(requireActivity()) { expense ->
            if (expense != null) {
                exp = expense
                val formattedAvg = String.format("%.2f", exp)
                binding.totalIncome.setText(formattedAvg)

            } else {
                // handle empty expense
                binding.totalIncome.setText("00.00")
            }
        }
    }

    fun getDailyAvg() {
        val dao = AppDataBase.getInstance(requireContext()).incexpTblDao()

        dao.getDailyAverage().observe(requireActivity()) { dailyAverage ->
            if (dailyAverage != null) {
                val avg = dailyAverage
                val formattedAvg = String.format("%.2f", avg)
                binding.avgTtl.text = formattedAvg
            } else {
                binding.avgTtl.text = "00.00"
            }
        }
    }

    fun getDailyAvgExp() {
        val dao = AppDataBase.getInstance(requireContext()).incexpTblDao()

        dao.getDailyAverageExp().observe(requireActivity()) { dailyAverage ->
            if (dailyAverage != null) {
                val avg = dailyAverage
                val formattedAvg = String.format("%.2f", avg)
                binding.avgTtl.text = formattedAvg
            } else {
                binding.avgTtl.text = "00.00"
            }
        }
    }




}