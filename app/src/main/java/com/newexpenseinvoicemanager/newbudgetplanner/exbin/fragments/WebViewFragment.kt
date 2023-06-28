package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
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
        val webView = binding.wvPrivacyAbtapp
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        val value = arguments?.getString("PRIVACY_KEY")
        val privacyUrl =
            "https://doc-hosting.flycricket.io/exbin-privacy-policy/690d0a6a-2061-43d4-9785-290268ca613c/privacy"
        val termsyUrl =
            "https://doc-hosting.flycricket.io/exbin-privacy-policy/690d0a6a-2061-43d4-9785-290268ca613c/privacy"
        if (value == "PRIVACY") {
            webView.loadUrl(privacyUrl)
            custom.ivTitle.setText("Privacy Policy")
        } else if (value == "TERMS") {
            webView.loadUrl(termsyUrl)
            custom.ivTitle.setText("Terms & Conditions")

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