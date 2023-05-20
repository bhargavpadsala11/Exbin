package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.MainActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.PaymentModesAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentPaymentModeBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.PaymentModes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PaymentModeFragment : Fragment() {

    private lateinit var binding: FragmentPaymentModeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPaymentModeBinding.inflate(layoutInflater)


        binding.addPaymentFloating.setOnClickListener {
            binding.addcardview.visibility = View.VISIBLE
        }

        binding.btnapply.setOnClickListener {
            addPaymentMode(binding.addPaymet.text.toString())
            clearText(binding.addPaymet)
        }
        binding.btncancel.setOnClickListener {
            clearText(binding.addPaymet)
            hideFragment(binding.addcardview)
        }

        val dao = AppDataBase.getInstance(requireContext()).paymentModesDao()
        dao.getAllPaymentMode().observe(requireActivity()) {
//                Toast.makeText(requireContext(), "${dao.getAllPaymentMode()}", Toast.LENGTH_SHORT).show()
            val layoutManager = LinearLayoutManager(requireContext())
            val recy = binding.paymentModesList

            recy.layoutManager = layoutManager
            val mainLayout = binding.MainLayoutOfPaymentMode
            val textInputEditText = binding.addPaymet

            val adapter = PaymentModesAdapter(requireContext(), it, mainLayout)
            recy.adapter = adapter
        }

        return binding.root
    }

    private fun addPaymentMode(addPaymet: String) {
        val db = AppDataBase.getInstance(requireContext()).paymentModesDao()
        val data = PaymentModes(paymentMode = addPaymet)
        lifecycleScope.launch(Dispatchers.IO) {
            db.insertPaymentMode(data)
        }
    }

    private fun clearText(addPaymet: TextInputEditText) {
        addPaymet.setText("")
    }

    private fun hideFragment(addcardview: MaterialCardView) {
        addcardview.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.hideBottomNavigationView()
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity?)!!.showBottomNavigationView()
    }


}
