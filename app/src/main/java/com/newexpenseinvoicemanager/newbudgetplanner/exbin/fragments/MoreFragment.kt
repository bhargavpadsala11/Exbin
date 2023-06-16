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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.drive.Drive
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentMoreBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import com.google.android.gms.drive.*
import com.google.android.gms.drive.query.Filters
import com.google.android.gms.drive.query.Query
import com.google.android.gms.drive.query.SearchableField


private const val REQUEST_CODE_SIGN_IN = 1001
class MoreFragment : Fragment() {

    private lateinit var binding: FragmentMoreBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var progressDialog: ProgressDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreBinding.inflate(layoutInflater)
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Drive.SCOPE_FILE)
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), signInOptions)

        // Create a progress dialog
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
            signInToGoogle()
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


    // error are coming in uploading the databse to the drive from the appllication
    private fun signInToGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)
    }

    private fun exportDatabaseToDrive(account: GoogleSignInAccount) {
        progressDialog.show()

        val sourceFile = requireContext().getDatabasePath("EXBIN")
        val destinationFileName = "EXBIN_DATABASE.db"

        val driveClient = Drive.getDriveClient(requireActivity(), account)
        val driveResourceClient = Drive.getDriveResourceClient(requireActivity(), account)

        // Check if the file already exists on Google Drive
        val query = driveResourceClient.query(
            Query.Builder().addFilter(
                Filters.eq(SearchableField.TITLE, destinationFileName)
            ).build()
        )
        query.addOnSuccessListener { metadataBuffer ->
            if (metadataBuffer.count > 0) {
                progressDialog.dismiss()
                Toast.makeText(context, "Database file already exists on Google Drive", Toast.LENGTH_SHORT).show()
            } else {
                val changeSet = MetadataChangeSet.Builder()
                    .setTitle(destinationFileName)
                    .setMimeType("application/x-sqlite3")
                    .setStarred(true)
                    .build()

                driveResourceClient.createContents()
                    .addOnSuccessListener { driveContents ->
                        val outputStream = driveContents.outputStream

                        try {
                            val fileInputStream = FileInputStream(sourceFile)
                            val buffer = ByteArray(1024)
                            var length: Int
                            while (fileInputStream.read(buffer).also { length = it } > 0) {
                                outputStream.write(buffer, 0, length)
                            }
                            fileInputStream.close()
                            outputStream.close()

                            driveResourceClient.getRootFolder()
                                .addOnSuccessListener { rootFolder ->
                                    driveResourceClient.createFile(rootFolder, changeSet, driveContents)
                                        .addOnSuccessListener {
                                            progressDialog.dismiss()
                                            Toast.makeText(context, "Database exported to Google Drive", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            progressDialog.dismiss()
                                            Toast.makeText(context, "Failed to create file on Google Drive", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                .addOnFailureListener {
                                    progressDialog.dismiss()
                                    Toast.makeText(context, "Failed to get root folder", Toast.LENGTH_SHORT).show()
                                }
                        } catch (e: IOException) {
                            progressDialog.dismiss()
                            e.printStackTrace()
                            Toast.makeText(context, "Failed to export database", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        progressDialog.dismiss()
                        Toast.makeText(context, "Failed to create drive contents", Toast.LENGTH_SHORT).show()
                    }
            }
            metadataBuffer.release()
        }
        query.addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(context, "Failed to export database to Google Drive", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)
                    if (account != null) {
                        exportDatabaseToDrive(account)
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(context, "Failed to sign in to Google account ????", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {
                    progressDialog.dismiss()
                    Toast.makeText(context, "Failed to sign in to Google account !!!!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}

