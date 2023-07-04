@file:Suppress(
    "NAME_SHADOWING", "DEPRECATION", "ReplaceGetOrSet", "SENSELESS_COMPARISON",
    "UNREACHABLE_CODE", "FunctionName"
)

package com.newexpenseinvoicemanager.newbudgetplanner.exbin.activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.ActivityBackUpBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class BackUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBackUpBinding
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    private lateinit var mDrive: Drive
    private var FireBaseGooggleAdsId: String = ""
    private var isAds: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preference =
            this.getSharedPreferences("NativeId", MODE_PRIVATE)
        FireBaseGooggleAdsId = preference.getString("Na_tive_id", "")!!
        isAds = preference.getBoolean("isShow", false)


        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)


        binding.mbBackupBtn.setOnClickListener {
            uploadFileToGDrive(this)
        }
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo


        if (isAds) {
            if (networkInfo != null && networkInfo.isConnected) {
                val adLoader = AdLoader.Builder(this, FireBaseGooggleAdsId)
                    .forNativeAd { nativeAd ->
                        val styles =
                            NativeTemplateStyle.Builder().build()
                        val template: TemplateView = binding.myTemplate
                        template.setStyles(styles)
                        template.setNativeAd(nativeAd)
                    }
                    .build()

                adLoader.loadAd(AdRequest.Builder().build())
            } else {
                binding.myTemplate.visibility = View.GONE
            }
        } else {
            binding.myTemplate.visibility = View.GONE

        }
        binding.titleTextView.setOnClickListener {
            this.binding.mbBackup.performClick()
        }
        binding.mbBackup.setOnClickListener {
            SingIn()
        }

        binding.titleTextView1.setOnClickListener {
            this.binding.mbSingOut.performClick()
        }
        binding.mbSingOut.setOnClickListener {
            gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

            gsc = GoogleSignIn.getClient(this, gso)

            val account: GoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)!!
            if (account != null) {
                singOut()
            } else {
                Toast.makeText(this, "Log in First", Toast.LENGTH_SHORT).show()
            }
        }

        binding.mbDwnld.setOnClickListener {
            gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

            gsc = GoogleSignIn.getClient(this, gso)

            val account: GoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)!!
            if (account != null) {
                restoreFromBackup()
            } else {
                Toast.makeText(this, "Log in First", Toast.LENGTH_SHORT).show()
            }
        }
        binding.editBackarrow.setOnClickListener {
            onBackPressed()
        }


    }

    private fun singOut() {
        gsc.signOut().addOnSuccessListener {
            finish()
            startActivity(Intent(applicationContext, BackUpActivity::class.java))
        }
    }

    private fun SingIn() {
        val intent = gsc.signInIntent
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) run {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                task.getResult(ApiException::class.java)
//                val selectedFile = data!!.data //The uri with the location of the file
//                makeCopy(selectedFile!!)
                BackupActivity()
            } catch (e: Exception) {
                Toast.makeText(this, "error $e", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun BackupActivity() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)

        val account: GoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)!!
        if (account != null) {
            binding.tvEmail.text = account.email
            binding.tvName.text = account.displayName
            val photoUrl = account.photoUrl
            if (photoUrl != null) {
                Glide.with(this)
                    .load(photoUrl)
                    .into(binding.gmailimageView)
            }
            mDrive = getDriveService(this)

            // Call the exportToGoogleDrive function
        }
    }


    // upload code
    private fun getDriveService(context: Context): Drive {
        GoogleSignIn.getLastSignedInAccount(context).let { googleAccount ->
            val credential = GoogleAccountCredential.usingOAuth2(
                this, listOf(DriveScopes.DRIVE_FILE)
            )
            credential.selectedAccount = googleAccount!!.account!!
            return Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
            )
                .setApplicationName(getString(R.string.app_name))
                .build()
        }
        var tempDrive: Drive
        return tempDrive
    }


    private fun uploadFileToGDrive(context: Context) {
        mDrive.let { googleDriveService ->
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val fileName = "EXBIN_DATABASE.db" // The name of the file to upload
                    val localFile = File(context.getExternalFilesDir(null), fileName)

                    if (!localFile.exists()) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Local file does not exist",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        return@launch
                    }

                    // Check if a file with the same name already exists on Google Drive
                    val query = "name = '$fileName'"
                    val fileList = googleDriveService.files().list().setQ(query).execute().files
                    if (fileList.isNotEmpty()) {
                        // Delete the existing file before uploading the new one
                        val fileId = fileList[0].id
                        googleDriveService.files().delete(fileId).execute()
                    }

                    val fileMetadata = com.google.api.services.drive.model.File()
                    fileMetadata.name = fileName





                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "File uploaded to Google Drive",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (userAuthEx: UserRecoverableAuthIOException) {
                    withContext(Dispatchers.Main) {
                        startActivity(userAuthEx.intent)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("asdf", e.toString())
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Error occurred while uploading file",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun restoreFromBackup() {
        mDrive.let { googleDriveService ->
            lifecycleScope.launch {
                try {
                    val fileName = "EXBIN_DATABASE.db" // The name of the file to download

                    withContext(Dispatchers.IO) {
                        val query = "name = '$fileName'"
                        val fileList = googleDriveService.files().list().setQ(query).execute().files

                        if (fileList.isNotEmpty()) {
                            val fileId = fileList[0].id
                            importDatabaseFromDrive(applicationContext, fileId)
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    applicationContext,
                                    "File not found on Google Drive",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } catch (userAuthEx: UserRecoverableAuthIOException) {
                    startActivity(userAuthEx.intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("asdf", e.toString())
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            applicationContext,
                            "Error occurred while importing database",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }


    private fun importDatabaseFromDrive(context: Context, fileId: String) {
        val destinationFile = context.getDatabasePath("EXBIN")

        try {
            // Check if database file exists
            if (destinationFile.exists()) {
                // Close any open connections to the database
                AppDataBase.getInstance(context).close()

                // Delete the existing database file
                context.deleteDatabase("EXBIN")
            }

            val outputStream = FileOutputStream(destinationFile)
            mDrive.files().get(fileId).executeMediaAndDownloadTo(outputStream)

            // Reopen the database after importing
            AppDataBase.getInstance(context)

            runOnUiThread {
                Toast.makeText(
                    context,
                    "Database imported from Google Drive",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(context, "Failed to import database", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }

}