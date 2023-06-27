package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentWebViewBinding


class WebViewFragment : Fragment() {

    private lateinit var binding: FragmentWebViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentWebViewBinding.inflate(layoutInflater)

        val custom = binding.appBar1
        custom.ivDelete.visibility = View.GONE
        custom.ivBack.setOnClickListener {
            loadFragment(MoreFragment())
        }


        val value = arguments?.getString("PRIVACY_KEY")
        if (value == "PRIVACY") {
            custom.ivTitle.setText("Privacy Policy")
            binding.wvPrivacyAbtapp.loadUrl("https://doc-hosting.flycricket.io/exbin-privacy-policy/690d0a6a-2061-43d4-9785-290268ca613c/privacy")
        } else if(value == "TERMS"){
            custom.ivTitle.setText("Terms & Conditions")
            binding.wvPrivacyAbtapp.loadUrl("https://doc-hosting.flycricket.io/exbin-terms-of-use/7396c7f3-efd7-4a7f-9064-0336e7a31d02/terms")

        }
        // Inflate the layout for this fragment
        return binding.root
    }
    private fun loadFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

}