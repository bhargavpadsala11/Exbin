package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.MainActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentWebViewBinding


class WebViewFragment : Fragment() {

    private lateinit var binding: FragmentWebViewBinding
    private var privacyUrl:String = ""
    private var termsyUrl:String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentWebViewBinding.inflate(layoutInflater)
        (activity as MainActivity?)!!.floatButtonHide()

        val preference =
            requireContext().getSharedPreferences("TERMS_PRIVACY", AppCompatActivity.MODE_PRIVATE)
        privacyUrl = preference.getString("privacy_policy", "")!!
        termsyUrl = preference.getString("terms_conditions", "")!!
        val custom = binding.appBar1
        custom.ivDelete.visibility = View.GONE
        custom.ivBack.setOnClickListener {
            loadFragment(MoreFragment())
        }
        val webView = binding.wvPrivacyAbtapp
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        val value = arguments?.getString("PRIVACY_KEY")
        Log.d("privacyUrl", privacyUrl)
        Log.d("termsyUrl", termsyUrl)

        if (value == "PRIVACY") {
            webView.loadUrl(privacyUrl)
            custom.ivTitle.text = "Privacy Policy"
        } else if (value == "TERMS") {
            webView.loadUrl(termsyUrl)
            custom.ivTitle.text = "Terms & Conditions"

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