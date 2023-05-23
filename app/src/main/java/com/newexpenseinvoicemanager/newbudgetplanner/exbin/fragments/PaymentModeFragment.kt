package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.MainActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.FragmentCallListener
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.PaymentModesAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentPaymentModeBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.PaymentModes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PaymentModeFragment : Fragment(),FragmentCallListener{

    private lateinit var binding: FragmentPaymentModeBinding
    private lateinit var addPaymentView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPaymentModeBinding.inflate(layoutInflater)

        val mainView = inflater.inflate(R.layout.fragment_payment_mode, container, false)
        addPaymentView = inflater.inflate(R.layout.add_payent_maode_layout, container, false)

        binding.addPaymentFloating.setOnClickListener {
            Toast.makeText(requireContext(), "Button pressing", Toast.LENGTH_SHORT).show()
            // binding.addcardview.visibility = View.VISIBLE
            mainView.visibility = View.GONE
            addPaymentView.visibility = View.VISIBLE
            container?.addView(addPaymentView)


        }
        val addButton = addPaymentView.findViewById<MaterialButton>(R.id.btnapply)
        val cancelButton = addPaymentView.findViewById<MaterialButton>(R.id.btncancel)
        addButton.setOnClickListener {

            val getaymentModeTextView =
                addPaymentView.findViewById<TextInputEditText>(R.id.addPaymet)
            if (getaymentModeTextView.text?.isEmpty() == true){
                //getaymentModeTextView.setBackgroundResource(R.drawable.red_under_line)
                getaymentModeTextView.requestFocus()
                Toast.makeText(requireContext(), "Please write Payment mode name", Toast.LENGTH_SHORT).show()
            }else{
            addPaymentMode(getaymentModeTextView.text.toString())
            mainView.visibility = View.VISIBLE
            addPaymentView.visibility = View.GONE
            container?.removeView(addPaymentView)
            getaymentModeTextView.setText("")}
        }
        cancelButton.setOnClickListener {
            mainView.visibility = View.VISIBLE
            addPaymentView.visibility = View.GONE
            container?.removeView(addPaymentView)
        }

        val dao = AppDataBase.getInstance(requireContext()).paymentModesDao()
        dao.getAllPaymentMode().observe(requireActivity()) {
//                Toast.makeText(requireContext(), "${dao.getAllPaymentMode()}", Toast.LENGTH_SHORT).show()
            val layoutManager = LinearLayoutManager(requireContext())
            val recy = binding.paymentModesList
            recy.layoutManager = layoutManager
            val mainLayout = inflater.inflate(R.layout.add_payent_maode_layout, container, false)
            val adapter = PaymentModesAdapter(requireContext(), it, mainLayout,this)
            recy.adapter = adapter
        }
       // container?.removeView(addPaymentView)
        return binding.root
    }
    private fun addPaymentMode(addPaymet: String) {
        val db = AppDataBase.getInstance(requireContext()).paymentModesDao()
        val data = PaymentModes(paymentMode = addPaymet)
        lifecycleScope.launch(Dispatchers.IO) {
            db.insertPaymentMode(data)
        }
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
        addPaymentView.visibility = View.GONE

    }

    override fun callOtherLayoutFile() {
        layoutInflater.inflate(R.layout.add_payent_maode_layout, null)
    }

}
