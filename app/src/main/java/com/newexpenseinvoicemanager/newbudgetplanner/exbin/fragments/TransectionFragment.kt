package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.TransectionListAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentTransectionBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.Categories

class TransectionFragment : Fragment() {


    private lateinit var binding : FragmentTransectionBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentTransectionBinding.inflate(layoutInflater)
        val dao = AppDataBase.getInstance(requireContext())
        val categoryMap = mutableMapOf<String, Categories>()
        dao.categoriesDao().getAllCategory().observe(requireActivity()) { categories ->
            categories.forEach { category ->
                categoryMap[category.CategoryName!!] = category
            }
        }

        dao.incexpTblDao().getAllData().observe(requireActivity()){
            binding.transectionItem.adapter = TransectionListAdapter(requireContext(),it,categoryMap)
        }



        return binding.root
    }

}