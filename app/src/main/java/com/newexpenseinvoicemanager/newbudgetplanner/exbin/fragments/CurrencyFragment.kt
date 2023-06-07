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
    private var currencyId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCurrencyBinding.inflate(layoutInflater)

        val custom = binding.appBar
        custom.ivDelete.visibility = View.GONE
        custom.ivBack.setOnClickListener { loadFragment(MoreFragment()) }
        custom.ivTitle.setText("Select Currency")
        val dao = AppDataBase.getInstance(requireContext()).currencyDao()
        dao.getAllCurrency().observe(requireActivity()) {
//                Toast.makeText(requireContext(), "${dao.getAllPaymentMode()}", Toast.LENGTH_SHORT).show()
            val layoutManager = LinearLayoutManager(requireContext())
            val recy = binding.rvCurrency
            recy.layoutManager = layoutManager
            val adapter = CurrencyAdapter(requireContext(), it) {
                currencyId = it
            }
            recy.adapter = adapter
        }

        binding.mbCurrencySet.setOnClickListener {
            updateCurrencyStatus(currencyId!!)

//            val currencyClass = getCurrencyClass(viewLifecycleOwner, requireContext())
//            currencyClass.getCurrencies()
//            val currencySymbol = currencyClass.getCurrencySymbol()

        }
        binding.mbCurrencyCnl.setOnClickListener {
            loadFragment(MoreFragment())
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.hideBottomNavigationView()
    }

    override fun onStop() {
        (activity as MainActivity?)!!.showBottomNavigationView()
        super.onStop()

    }

    fun updateCurrencyStatus(it: Int) {
        val db = AppDataBase.getInstance(requireContext()).currencyDao()
        lifecycleScope.launch(Dispatchers.IO) {
            db.updateStatus(it)
            db.resetOtherCurrencyStatus(it)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }
}


