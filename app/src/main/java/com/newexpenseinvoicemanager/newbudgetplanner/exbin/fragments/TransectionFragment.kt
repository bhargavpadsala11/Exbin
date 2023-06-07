package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.TransectionListAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.getCurrencyClass
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentTransectionBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.Categories
import java.text.SimpleDateFormat
import java.util.*

class TransectionFragment : Fragment() {


    private lateinit var binding: FragmentTransectionBinding
    private var date: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTransectionBinding.inflate(layoutInflater)

        val custom = binding.appBar

        custom.ivBack.setOnClickListener { loadFragment(HomeFragment()) }
        custom.ivTitle.setText("Transaction List")

        custom.ivDelete.setImageResource(R.drawable.ic_filter)

        val calendar = Calendar.getInstance()

        val currencyClass = getCurrencyClass(viewLifecycleOwner, requireContext())
        val dao = AppDataBase.getInstance(requireContext())
        val categoryMap = mutableMapOf<String, Categories>()
        dao.categoriesDao().getAllCategory().observe(requireActivity()) { categories ->
            categories.forEach { category ->
                categoryMap[category.CategoryName!!] = category
            }
        }

        dao.incexpTblDao().getAllData().observe(requireActivity()) {
            binding.transectionItem.adapter =
                TransectionListAdapter(requireContext(), it, categoryMap, currencyClass)
        }

        custom.ivDelete.setOnClickListener {

            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    date =
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                    Log.d("Date", "$date")
                    if (dao.incexpTblDao().getAllDataByCat(date) != null) {
                        dao.incexpTblDao().getAllDataByCat(date).observe(requireActivity()) {
                            binding.transectionItem.adapter =
                                TransectionListAdapter(
                                    requireContext(),
                                    it,
                                    categoryMap,
                                    currencyClass
                                )
                        }
                    } else {
                        binding.transectionItem.visibility = View.GONE
                        binding.tvNoDataFound.visibility = View.VISIBLE
                    }
                },
                year,
                month,
                dayOfMonth
            )

            datePickerDialog.show()

        }



        return binding.root
    }

    private fun loadFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    fun getTransecton(currencyClass: getCurrencyClass) {

        val dao = AppDataBase.getInstance(requireContext())
        val categoryMap = mutableMapOf<String, Categories>()
        dao.categoriesDao().getAllCategory().observe(requireActivity()) { categories ->
            categories.forEach { category ->
                categoryMap[category.CategoryName!!] = category
            }
        }

        dao.incexpTblDao().getAllData().observe(requireActivity()) {
            binding.transectionItem.adapter =
                TransectionListAdapter(requireContext(), it, categoryMap, currencyClass)
        }
    }

}