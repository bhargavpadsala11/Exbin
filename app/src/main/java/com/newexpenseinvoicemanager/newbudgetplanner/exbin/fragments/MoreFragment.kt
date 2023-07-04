@file:Suppress("DEPRECATION")

package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments


import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.MainActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.activities.BackUpActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentMoreBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class MoreFragment : Fragment() {
    private lateinit var binding: FragmentMoreBinding
    private lateinit var progressDialog: ProgressDialog
    private var share_link :String? = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreBinding.inflate(layoutInflater)
        (activity as MainActivity?)!!.floatButtonHide()

        val preference =
            requireContext().getSharedPreferences("TERMS_PRIVACY", AppCompatActivity.MODE_PRIVATE)
        share_link = preference.getString("share_link", "")!!
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Exporting database to Google Drive...")
        progressDialog.setCancelable(false)

        val custom = binding.editBackarrow

        custom.setOnClickListener {
            loadFragment(HomeFragment())
            (activity as MainActivity?)!!.setBottomNavigationAsHome()
        }
        var fragment: Fragment

        binding.titleTextView.setOnClickListener {
            this.binding.addCatBtn.performClick()
        }
        binding.addCatBtn.setOnClickListener {
            fragment = CategoryListFragment()
            loadFragment(fragment)
        }

        binding.tvPayment.setOnClickListener {
            this.binding.paymentModeBtn.performClick()
        }
        binding.paymentModeBtn.setOnClickListener {
            fragment = PaymentModeFragment()
            loadFragment(fragment)
        }
        binding.tvCurrency.setOnClickListener {
            this.binding.btnCurrency.performClick()
        }
        binding.btnCurrency.setOnClickListener {
            loadFragment(CurrencyFragment())
        }

        binding.tvTakeDrive.setOnClickListener {
            this.binding.clTakeDrive.performClick()
        }
        binding.clTakeDrive.setOnClickListener {
            exportDataBase(requireContext())
            val intent = Intent(requireContext(),BackUpActivity::class.java)
            startActivity(intent)
        }


        binding.tvShareWf.setOnClickListener {
            this.binding.clShareWf.performClick()
        }
        binding.clShareWf.setOnClickListener {
            shareApplication()
        }

        binding.tvAddPrivacy.setOnClickListener {
            this.binding.clPrivacy.performClick()
        }
        binding.clPrivacy.setOnClickListener {
            val ldf = WebViewFragment()
            val args = Bundle()
            args.putString("PRIVACY_KEY", "PRIVACY")
            ldf.arguments = args
            loadFragment(ldf)
        }
        binding.tvAddAbout.setOnClickListener {
            this.binding.clAbout.performClick()
        }
        binding.clAbout.setOnClickListener {
            val ldf = WebViewFragment()
            val args = Bundle()
            args.putString("PRIVACY_KEY", "TERMS")
            ldf.arguments = args
            loadFragment(ldf)
        }

        return binding.root
    }



    private fun loadFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun exportDataBase(context: Context) {
        val sourceFile = context.getDatabasePath("EXBIN")
        val destinationFile = File(context.getExternalFilesDir(null), "EXBIN_DATABASE.db")

        try {
            // Close any open connections to the database
            AppDataBase.getInstance(context).close()

            val fileInputStream = FileInputStream(sourceFile)
            val fileOutputStream = FileOutputStream(destinationFile)
            val buffer = ByteArray(1024)
            var length: Int
            while (fileInputStream.read(buffer).also { length = it } > 0) {
                fileOutputStream.write(buffer, 0, length)
            }
            fileInputStream.close()
            fileOutputStream.close()
            Toast.makeText(
                context,
                "Database exported",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to export database", Toast.LENGTH_SHORT).show()
        }
    }




    private fun shareApplication() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Application")
        shareIntent.putExtra(Intent.EXTRA_TEXT, share_link)

        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }



}

