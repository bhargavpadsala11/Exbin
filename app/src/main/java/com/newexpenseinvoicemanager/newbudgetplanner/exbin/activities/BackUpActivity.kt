package com.newexpenseinvoicemanager.newbudgetplanner.exbin.activities

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.FileContent
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.IOUtils
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.ActivityBackUpBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class BackUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBackUpBinding
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient
    lateinit var mDrive: Drive

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)


            binding.mbBackupBtn.setOnClickListener {
                uploadFileToGDrive(this) }


            binding.mbBackup.setOnClickListener {
                SingIn()
            }


        binding.mbSingOut.setOnClickListener {
            singOut()
        }

        binding.mbDwnld.setOnClickListener {
            restoreFromBackup()
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
//override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//    super.onActivityResult(requestCode, resultCode, data)
//    if (requestCode == 100 && resultCode == Activity.RESULT_OK) { // Check for RESULT_OK
//        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
//        try {
//            val account = task.getResult(ApiException::class.java)
//            val selectedFile = data!!.data // The uri with the location of the file
//            makeCopy(selectedFile!!)
//            BackupActivity()
//        } catch (e: Exception) {
//            Toast.makeText(this, "Error: $e", Toast.LENGTH_SHORT).show()
//        }
//    }
//}

    private fun BackupActivity() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)

        var account: GoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)!!
        if (account != null) {
            binding.tvEmail.text = account.email
            binding.tvName.text = account.displayName
            mDrive = getDriveService(this)

            // Call the exportToGoogleDrive function
        }
    }

//    private fun exportDataBase(context: Context) {
//        val sourceFile = context.getDatabasePath("EXBIN")
//        val destinationFile = File(context.getExternalFilesDir(null), "EXBIN_DATABASE.db")
//
//        try {
//            // Close any open connections to the database
//            AppDataBase.getInstance(context).close()
//
//            val fileInputStream = FileInputStream(sourceFile)
//            val fileOutputStream = FileOutputStream(destinationFile)
//            val buffer = ByteArray(1024)
//            var length: Int
//            while (fileInputStream.read(buffer).also { length = it } > 0) {
//                fileOutputStream.write(buffer, 0, length)
//            }
//            fileInputStream.close()
//            fileOutputStream.close()
//            Toast.makeText(
//                context,
//                "Database exported to ${destinationFile.absolutePath}",
//                Toast.LENGTH_SHORT
//            ).show()
//        } catch (e: IOException) {
//            e.printStackTrace()
//            Toast.makeText(context, "Failed to export database", Toast.LENGTH_SHORT).show()
//        }
//    }


    // upload code
    fun getDriveService(context: Context): Drive {
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

    fun uploadFileToGDrive(context: Context) {
        mDrive.let { googleDriveService ->
            lifecycleScope.launch {
                try {
                    val fileName = "EXBIN_DATABASE.db" // The name of the file to upload
                    val localFile = File(context.getExternalFilesDir(null), fileName)

                    if (!localFile.exists()) {
                        Toast.makeText(
                            context,
                            "Local file does not exist",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }

                    val fileMetadata = com.google.api.services.drive.model.File()
                    fileMetadata.name = fileName

                    val mediaContent = FileContent("application/octet-stream", localFile)

                    withContext(Dispatchers.IO) {
                        val uploadedFile = googleDriveService.files().create(fileMetadata, mediaContent)
                            .setFields("id")
                            .execute()

                        val fileId = uploadedFile.id

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "File uploaded to Google Drive with ID: $fileId",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (userAuthEx: UserRecoverableAuthIOException) {
                    startActivity(userAuthEx.intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("asdf", e.toString())
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Error occurred while uploading file: ${e.toString()}",
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
                            "Error occurred while importing database: ${e.toString()}",
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

            Toast.makeText(
                context,
                "Database imported from Google Drive",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to import database", Toast.LENGTH_SHORT).show()
        }
    }


}