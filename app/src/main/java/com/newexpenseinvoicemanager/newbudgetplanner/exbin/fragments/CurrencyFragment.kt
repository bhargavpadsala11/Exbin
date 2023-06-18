package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.MainActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.CurrencyAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.PaymentModesAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dao.currencyDao
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.getCurrencyClass
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentCurrencyBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrencyFragment : Fragment() {
    private lateinit var binding: FragmentCurrencyBinding
    private var selectedCurrencyId: Int? = null
    private var selectedCurrencyPosition: Int? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCurrencyBinding.inflate(inflater, container, false)

        val custom = binding.appBar
        custom.ivDelete.visibility = View.GONE
        custom.ivBack.setOnClickListener { loadFragment(MoreFragment()) }
        custom.ivTitle.text = "Select Currency"

        val dao = AppDataBase.getInstance(requireContext()).currencyDao()
        dao.getAllCurrency().observe(viewLifecycleOwner) { currencies ->
            val layoutManager = LinearLayoutManager(requireContext())
            binding.rvCurrency.layoutManager = layoutManager
            val adapter = CurrencyAdapter(requireContext(), currencies) { currencyId ->
                selectedCurrencyId = currencyId
            }
            binding.rvCurrency.adapter = adapter

            // Find the index of the selected currency
            val selectedIndex = currencies.indexOfFirst { it.CurrencyStatus == 1 }
            if (selectedIndex != -1) {
                adapter.selectedPosition = selectedIndex
                selectedCurrencyId = currencies[selectedIndex].currencyId
                adapter.notifyDataSetChanged()
                binding.rvCurrency.scrollToPosition(selectedIndex)
            }
        }

        binding.mbCurrencySet.setOnClickListener {
            selectedCurrencyId?.let { currencyId ->
                updateCurrencyStatus(currencyId)
            }

            loadFragment(MoreFragment())
        }

        binding.mbCurrencyCnl.setOnClickListener {
            loadFragment(MoreFragment())
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideBottomNavigationView()
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).showBottomNavigationView()
    }

    private fun updateCurrencyStatus(currencyId: Int) {
        val db = AppDataBase.getInstance(requireContext()).currencyDao()
        lifecycleScope.launch(Dispatchers.IO) {
            db.updateStatus(currencyId)
            db.resetOtherCurrencyStatus(currencyId)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
