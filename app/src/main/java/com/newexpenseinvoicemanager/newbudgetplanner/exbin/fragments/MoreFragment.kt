package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments


import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentMoreBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


private const val REQUEST_CODE_SIGN_IN = 1001


class MoreFragment : Fragment() {
    private lateinit var binding: FragmentMoreBinding
    private lateinit var progressDialog: ProgressDialog
    private val RC_SIGN_IN = 123
    private lateinit var googleApiClient: GoogleApiClient


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreBinding.inflate(layoutInflater)

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Exporting database to Google Drive...")
        progressDialog.setCancelable(false)

        val custom = binding.editBackarrow

        custom.setOnClickListener {
            loadFragment(HomeFragment())
        }
        var fragment: Fragment

        binding.addCatBtn.setOnClickListener {
            fragment = CategoryListFragment()
            loadFragment(fragment)
        }

        binding.paymentModeBtn.setOnClickListener {
            fragment = PaymentModeFragment()
            loadFragment(fragment)
        }
        binding.btnCurrency.setOnClickListener {
            loadFragment(CurrencyFragment())
        }

        binding.clTakeDrive.setOnClickListener {

        }
        binding.clRestore.setOnClickListener {
            importDatabase(requireContext())
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
                "Database exported to ${destinationFile.absolutePath}",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to export database", Toast.LENGTH_SHORT).show()
        }
    }


    private fun importDatabase(context: Context) {
        val sourceFile = File(context.getExternalFilesDir(null), "EXBIN_DATABASE.db")
        val destinationFile = context.getDatabasePath("EXBIN")

        try {
            // Check if database file exists
            if (destinationFile.exists()) {
                // Close any open connections to the database
                AppDataBase.getInstance(context).close()

                // Delete the existing database file
                context.deleteDatabase("EXBIN")
            }

            val fileInputStream = FileInputStream(sourceFile)
            val fileOutputStream = FileOutputStream(destinationFile)
            val buffer = ByteArray(1024)
            var length: Int
            while (fileInputStream.read(buffer).also { length = it } > 0) {
                fileOutputStream.write(buffer, 0, length)
            }
            fileInputStream.close()
            fileOutputStream.close()

            // Reopen the database after importing
            AppDataBase.getInstance(context)

            Toast.makeText(
                context,
                "Database imported from ${sourceFile.absolutePath}",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to import database", Toast.LENGTH_SHORT).show()
        }
    }






}

