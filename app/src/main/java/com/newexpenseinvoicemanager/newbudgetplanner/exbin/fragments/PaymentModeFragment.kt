@file:Suppress("NAME_SHADOWING")

package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.MainActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.PaymentModesAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentPaymentModeBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.PaymentModes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PaymentModeFragment : Fragment() {

    private lateinit var binding: FragmentPaymentModeBinding
    private var addPaymentView: View? = null
    private var addupdatetView: View? = null
    private var deletePaymentView: View? = null
    private var mInterstitialAd: InterstitialAd? = null
    private var FireBaseGooggleAdsInterId: String = ""
    private lateinit var isTitleOrNot: AppCompatTextView
    private lateinit var PaymentIds: String
    private var isTitle: String = ""
    private lateinit var mainview: View
    private var globalgetaymentModeTextView: TextInputEditText? = null
    private var isAds: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPaymentModeBinding.inflate(layoutInflater)
        (activity as MainActivity?)!!.floatButtonHide()
        (activity as MainActivity?)!!.getIdofNativeAds()

        val custom = binding.appBar
        custom.ivDelete.visibility = View.GONE
        custom.ivBack.setOnClickListener { loadFragment(MoreFragment()) }
        val preference =
            requireContext().getSharedPreferences("NativeId", AppCompatActivity.MODE_PRIVATE)
        FireBaseGooggleAdsInterId = preference.getString("inter_id", "")!!
        isAds = preference.getBoolean("isShow", false)


        val mainView = inflater.inflate(R.layout.fragment_payment_mode, container, false)
        mainview = mainView
        addPaymentView = inflater.inflate(R.layout.add_payent_maode_layout, container, false)
        val addButton = addPaymentView?.findViewById<MaterialButton>(R.id.btnapply)
        val cancelButton = addPaymentView?.findViewById<MaterialButton>(R.id.btncancel)
        val getaymentModeTextView =
            addPaymentView?.findViewById<TextInputEditText>(R.id.addPaymet)

        if (isAds == true) {
            loadAd(container, getaymentModeTextView)
        }
        binding.addPaymentFloating.setOnClickListener {
            // binding.addcardview.visibility = View.VISIBLE
            mainView.visibility = View.GONE
            addPaymentView?.visibility = View.VISIBLE
            container?.addView(addPaymentView)


        }


        addButton?.setOnClickListener {

            isTitle = "ADD"

            if (getaymentModeTextView?.text?.isEmpty() == true) {
                //getaymentModeTextView.setBackgroundResource(R.drawable.red_under_line)
                getaymentModeTextView.requestFocus()
                Toast.makeText(
                    requireContext(),
                    "Please write Payment mode name",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val db = AppDataBase.getInstance(requireContext()).paymentModesDao()
                val existingPaymentMode =
                    db.getPaymentModeByName(getaymentModeTextView?.text.toString())
                if (existingPaymentMode == null) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd?.show(requireActivity())
                    } else {
                        addPaymentMode(getaymentModeTextView?.text.toString())
                        mainView.visibility = View.VISIBLE
                        addPaymentView?.visibility = View.GONE
                        container?.removeView(addPaymentView)
                        getaymentModeTextView?.setText("")
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Payment Mode Already Exists",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        cancelButton?.setOnClickListener {
            mainView.visibility = View.VISIBLE
            addPaymentView?.visibility = View.GONE
            container?.removeView(addPaymentView)
            val getaymentModeTextView =
                addPaymentView?.findViewById<TextInputEditText>(R.id.addPaymet)
            getaymentModeTextView?.setText("")
            isTitle = ""

        }

        val dao = AppDataBase.getInstance(requireContext()).paymentModesDao()
        dao.getAllPaymentMode().observe(requireActivity()) {
//                Toast.makeText(requireContext(), "${dao.getAllPaymentMode()}", Toast.LENGTH_SHORT).show()
            val layoutManager = LinearLayoutManager(requireContext())
            val recy = binding.paymentModesList
            recy.layoutManager = layoutManager
            val adapter = PaymentModesAdapter(requireContext(), it) { paymentMode, buttonClicked ->
                if (buttonClicked == "EDIT") {
                    isTitle = "EDIT"

                    addupdatetView =
                        inflater.inflate(R.layout.add_payent_maode_layout, container, false)

                    mainView.visibility = View.GONE
                    addupdatetView?.visibility = View.VISIBLE
                    container?.addView(addupdatetView)


                    val getaymentModeTextView =
                        addupdatetView?.findViewById<TextInputEditText>(R.id.addPaymet)
                    globalgetaymentModeTextView = getaymentModeTextView

                    val titlePaymentModeTextView =
                        addupdatetView?.findViewById<AppCompatTextView>(R.id.titlePaymentMode)
                    titlePaymentModeTextView?.text = "Update Payment Mode"
                    isTitleOrNot = titlePaymentModeTextView!!
                    getaymentModeTextView?.setText("${paymentMode.paymentMode}")
                    val paymentId = paymentMode.paymentModeId
                    PaymentIds = paymentId.toString()
                    val updateBtn = addupdatetView?.findViewById<MaterialButton>(R.id.btnapply)
                    updateBtn?.setOnClickListener {
                        if (mInterstitialAd != null) {
                            mInterstitialAd?.show(requireActivity())
                        } else {
                            Log.d("TAG", "The interstitial ad wasn't ready yet.")

                            updatePaymentMode(paymentId, getaymentModeTextView?.text.toString())
                            container?.removeView(addupdatetView)
                        }
                    }

                    val cancelButton = addupdatetView?.findViewById<MaterialButton>(R.id.btncancel)
                    cancelButton?.setOnClickListener {
                        titlePaymentModeTextView.text = "Add Payment Mode"
                        container?.removeView(addupdatetView)
                    }
                }
                if (buttonClicked == "DELETE") {

                    deletePaymentView =
                        inflater.inflate(R.layout.custom_delete_dialog, container, false)

                    mainView.visibility = View.GONE
                    deletePaymentView?.visibility = View.VISIBLE
                    container?.addView(deletePaymentView)

                    val deletePaymentModeBtn =
                        deletePaymentView?.findViewById<MaterialButton>(R.id.btn_delete)

                    val deleteTitleTextView =
                        deletePaymentView?.findViewById<AppCompatTextView>(R.id.tv_delete_title)
                    deleteTitleTextView?.text = "Are you sure you want to delete this PaymentMode ?"
                    val deletePaymentModeCnlBtn =
                        deletePaymentView?.findViewById<MaterialButton>(R.id.btncancel)
                    deletePaymentModeBtn?.setOnClickListener {
                        Toast.makeText(requireContext(), "Pressed", Toast.LENGTH_SHORT).show()
                        val db = AppDataBase.getInstance(requireContext()).incexpTblDao()
                        val existingpayment =
                            db.getPaymentModeByName(getaymentModeTextView?.text.toString())
                        if (existingpayment == null) {
                            Log.d("in if", "$existingpayment")
                            val paymentId = paymentMode.paymentModeId
                            deltePaymentMode(paymentId)
                            container?.removeView(deletePaymentView)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Can not delete This Payment Mode It is in use",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                    deletePaymentModeCnlBtn?.setOnClickListener {
                        container?.removeView(deletePaymentView)
                    }

                }


            }
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


    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.hideBottomNavigationView()
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity?)!!.showBottomNavigationView()
        addPaymentView?.visibility = View.GONE
        addupdatetView?.visibility = View.GONE
        deletePaymentView?.visibility = View.GONE
        mInterstitialAd = null


    }

    fun updatePaymentMode(id: Int, paymentMode: String) {
        val db = AppDataBase.getInstance(requireContext()).paymentModesDao()
//        val data = PaymentModes()
        lifecycleScope.launch(Dispatchers.IO) {
            db.updatePaymentMode(id, paymentMode)
        }
    }

    fun deltePaymentMode(id: Int) {
        val db = AppDataBase.getInstance(requireContext()).paymentModesDao()
//        val data = PaymentModes()
        lifecycleScope.launch(Dispatchers.IO) {
            db.deletePaymentMode(id)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        mInterstitialAd = null
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    fun loadAd(container: ViewGroup?, getaymentModeTextView: TextInputEditText?) {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            requireContext(),
            FireBaseGooggleAdsInterId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(ContentValues.TAG, adError.toString())
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(ContentValues.TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(ContentValues.TAG, "Ad was clicked.")
                            }

                            override fun onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                Log.d(ContentValues.TAG, "Ad dismissed fullscreen content.")

                                Log.d("Add Payment Mode", isTitle)
                                mInterstitialAd = null

                                if (isTitle == "ADD") {
                                    Log.d("Add Payment Mode", isTitle)
                                    addPaymentMode(getaymentModeTextView?.text.toString())
                                    mainview.visibility = View.VISIBLE
                                    addPaymentView?.visibility = View.GONE
                                    container?.removeView(addPaymentView)
                                    getaymentModeTextView?.setText("")
                                    loadFragment(PaymentModeFragment())
                                } else if (isTitle == "EDIT") {
                                    Log.d("EDIT", "$globalgetaymentModeTextView")
                                    updatePaymentMode(
                                        PaymentIds.toInt(),
                                        globalgetaymentModeTextView!!.text.toString()
                                    )
                                    container?.removeView(addupdatetView)
                                    loadFragment(PaymentModeFragment())
                                }

                            }

                            override fun onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(ContentValues.TAG, "Ad recorded an impression.")
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(ContentValues.TAG, "Ad showed fullscreen content.")
                            }
                        }

                }
            })
    }

}
