package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments


import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.google.android.ads.nativetemplates.rvadapter.AdmobNativeAdAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.MainActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.TransectionListAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.getCurrencyClass
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentTransectionBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.Categories
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.incexpTbl
import com.opencsv.CSVWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class TransectionFragment : Fragment() {


    private lateinit var binding: FragmentTransectionBinding
    private var sDate: String = ""
    private var lDate: String = ""
    private var tMode: String = ""
    private var PDF_OR_EXCEL: Boolean = true
    private var FireBaseGooggleAdsId: String = ""
    private var FireBaseGooggleAdsInterId: String = ""
    private var cDate: String? = ""
    private var mInterstitialAd: InterstitialAd? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTransectionBinding.inflate(layoutInflater)
        (activity as MainActivity?)!!.showBottomNavigationView()
        val sdf = SimpleDateFormat("ddMyyyyhhmmss")
        cDate = sdf.format(Date())
        val preference =
            requireContext().getSharedPreferences("NativeId", AppCompatActivity.MODE_PRIVATE)
        FireBaseGooggleAdsId = preference.getString("Na_tive_id", "")!!
        FireBaseGooggleAdsInterId = preference.getString("inter_id", "")!!


        val custom = binding.appBar
        custom.ivPdf.visibility = VISIBLE
        val filter = binding.clFliter
        val createFilter = binding.clConverter

        custom.ivBack.setOnClickListener {
            (activity as MainActivity?)!!.setBottomNavigationAsHome()
            loadFragment(HomeFragment())
        }
        custom.ivTitle.setText("Transaction List")

        custom.ivDelete.setImageResource(R.drawable.ic_filter)


        val currencyClass = getCurrencyClass(viewLifecycleOwner, requireContext())
        val dao = AppDataBase.getInstance(requireContext())
        val categoryMap = mutableMapOf<String, Categories>()
        dao.categoriesDao().getAllCategory().observe(requireActivity()) { categories ->
            categories.forEach { category ->
                categoryMap[category.CategoryName!!] = category
            }
        }
//        getIdofNativeAds()

        loadAd(dao, currencyClass, createFilter, categoryMap)
        getTransecton(currencyClass)

        val inComeButton = binding.btnIncome
        val exPenseButton = binding.btnExpense




        dao.incexpTblDao().getAllData().observe(requireActivity()) {
            val adapter = adapterOfTransection(it, categoryMap, currencyClass)
//            MobileAds.initialize(requireContext()) {
            binding.transectionItem.adapter
            //adapter.loadAds()
//            }

        }

        binding.tvStartdate.setOnClickListener {
            getStartDate()
        }

        binding.tvEnddate.setOnClickListener {
            getLastDate()
        }

        binding.tvAddStartdate.setOnClickListener {
            getStartDate()
        }

        binding.tvAddEnddate.setOnClickListener {
            getLastDate()
        }

        binding.btnIncome.setOnClickListener {
            tMode = "INCOME"
//            binding.btnIncome.requestFocus()
            ViewCompat.setBackgroundTintList(
                inComeButton,
                ContextCompat.getColorStateList(requireContext(), R.color.dark_main_color)
            )
            ViewCompat.setBackgroundTintList(
                exPenseButton,
                ContextCompat.getColorStateList(requireContext(), R.color.white)
            )

        }

        binding.btnExpense.setOnClickListener {
            tMode = "EXPENSE"
            binding.btnExpense.requestFocus()
            ViewCompat.setBackgroundTintList(
                exPenseButton,
                ContextCompat.getColorStateList(requireContext(), R.color.dark_main_color)
            )
            ViewCompat.setBackgroundTintList(
                inComeButton,
                ContextCompat.getColorStateList(requireContext(), R.color.white)
            )

        }

        binding.btnReset.setOnClickListener {
            sDate = ""
            lDate = ""
            tMode = ""
            binding.tvAddStartdate.setText("00/00/0000")
            binding.tvAddEnddate.setText("00/00/0000")
            filter.visibility = View.GONE
            getTransecton(currencyClass)
            ViewCompat.setBackgroundTintList(
                inComeButton,
                ContextCompat.getColorStateList(requireContext(), R.color.white)
            )
            ViewCompat.setBackgroundTintList(
                exPenseButton,
                ContextCompat.getColorStateList(requireContext(), R.color.white)
            )
            (activity as MainActivity?)!!.showBottomNavigationView()

        }

        binding.btnApply.setOnClickListener {
            filter.visibility = View.GONE
            if (sDate != null && lDate != null && tMode == "INCOME") {
                dao.incexpTblDao().getAllIncomeDataByDate(sDate, lDate).observe(requireActivity()) {
                    binding.transectionItem.adapter =
                        adapterOfTransection(it, categoryMap, currencyClass)

                }
            } else if (sDate != null && lDate != null && tMode == "EXPENSE") {

                dao.incexpTblDao().getAllExpenseDataByDate(sDate, lDate)
                    .observe(requireActivity()) {
                        binding.transectionItem.adapter =
                            adapterOfTransection(it, categoryMap, currencyClass)

                    }
            } else if (sDate != null && lDate != null) {
                dao.incexpTblDao().getAllDataByTwoDate(sDate, lDate).observe(requireActivity()) {
                    binding.transectionItem.adapter =
                        adapterOfTransection(it, categoryMap, currencyClass)

                }
            }
            (activity as MainActivity?)!!.showBottomNavigationView()

        }

        custom.ivDelete.setOnClickListener {
            filter.visibility = View.VISIBLE
            (activity as MainActivity?)!!.hideBottomNavigationView()

        }

        custom.ivPdf.setOnClickListener {
            createFilter.visibility = VISIBLE
            binding.rdbPdf.setOnClickListener {
                PDF_OR_EXCEL = true
            }

            binding.rdbExcel.setOnClickListener {
                PDF_OR_EXCEL = false
            }



            binding.btnDelete.setOnClickListener {
                if (PDF_OR_EXCEL == true) {
                    //add ads here
                    if (mInterstitialAd != null) {
                        mInterstitialAd?.show(requireActivity())
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.")
                        dao.incexpTblDao().getAllData().observe(requireActivity()) {
                            val adapter = adapterOfTransection(it, categoryMap, currencyClass)
                            generatePdf(it, categoryMap, currencyClass, adapter)
                        }
                        (activity as MainActivity?)!!.showBottomNavigationView()

                    }

                } else {
                    if (mInterstitialAd != null) {
                        mInterstitialAd?.show(requireActivity())
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.")
                        dao.incexpTblDao().getAllData().observe(requireActivity()) {
                            val adapter = adapterOfTransection(it, categoryMap, currencyClass)
                            exportToExcel(adapter, requireContext())
                        }
                        (activity as MainActivity?)!!.showBottomNavigationView()

                    }

                }
            }
            val filePath = File(requireContext().filesDir, "YOUR_FILE")
            binding.actFilePath.setText("File Path: " + "$filePath")

            binding.btncancel.setOnClickListener {
                createFilter.visibility = View.GONE
                (activity as MainActivity?)!!.showBottomNavigationView()

            }
            (activity as MainActivity?)!!.hideBottomNavigationView()

        }

        return binding.root
    }

    private fun loadFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    fun getTransecton(currencyClass: getCurrencyClass) {

        val dao = AppDataBase.getInstance(requireContext())
        val categoryMap = mutableMapOf<String, Categories>()
        dao.categoriesDao().getAllCategory().observe(requireActivity()) { categories ->
            categories.forEach { category ->
                categoryMap[category.CategoryName!!] = category
            }
        }

        dao.incexpTblDao().getAllData().observe(requireActivity()) {
            val adapter = adapterOfTransection(it, categoryMap, currencyClass)
            val admobNativeAdAdapter = AdmobNativeAdAdapter.Builder.with(
                FireBaseGooggleAdsId,
                adapter,
                "small"   // "medium" it can also used
            ).adItemInterval(4).build()
            binding.transectionItem.adapter = admobNativeAdAdapter
        }
    }

    fun getLastDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                lDate =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                binding.tvAddEnddate.setText(lDate)

            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()


    }

    fun getStartDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                sDate =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                binding.tvAddStartdate.setText(sDate)

            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.show()

    }


    private fun generatePdf(
        incexpTbls: List<incexpTbl>,
        categoryMap: MutableMap<String, Categories>,
        currencyClass: getCurrencyClass,
        adapter: TransectionListAdapter
    ) {
        val document = Document()
        val fileName = "EXBIN" + "$cDate" + "Transaction.pdf"
        val filePath = File(requireContext().filesDir, fileName)
        val outputStream = FileOutputStream(filePath)
        PdfWriter.getInstance(document, outputStream)
//        requireContext().getExternalFilesDir(null)?.absolutePath + "/" + fileName
        document.open()

        // Add adapter data to PDF

        val font = Font(Font.FontFamily.TIMES_ROMAN, 22f)
        val table = PdfPTable(4)
        table.addCell(Phrase("Category", font))
        table.addCell(Phrase("Date", font))
        table.addCell(Phrase("Note", font))
//        val notCell = PdfPCell(Phrase("Note",font))
//        notCell.minimumHeight = 10f
//        table.addCell(notCell)
        table.addCell(Phrase("Amount", font))

        // Add empty cell with height of 20f
        val emptyCell = PdfPCell()
        emptyCell.border = PdfPCell.NO_BORDER
        emptyCell.fixedHeight = 20f

        // table.addCell(emptyCell)

        table.addCell(emptyCell)
        table.addCell(emptyCell)
        table.addCell(emptyCell)
        table.addCell(emptyCell)


        for (i in 0 until adapter.itemCount) {
            val item = adapter.list[i]
            //val category = adapter.categoryMap[item.category]

            // Add item data to PDF
            table.addCell(Phrase(item.category, font))
            table.addCell(Phrase(item.date, font))
            table.addCell(Phrase(item.note, font))
//            val noteCell = PdfPCell(Phrase(item.note,font))
//            noteCell.minimumHeight = 10f
//            table.addCell(noteCell)
            table.addCell(Phrase(item.amount.toString(), font))
        }

        document.add(table)
        document.close()

        // Open the PDF file
//        val file = File(filePath)
//        val uri = FileProvider.getUriForFile(
//            requireContext(),
//            "${requireContext().packageName}.provider",
//            file
//        )
//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.setDataAndType(uri, "provider/pdf")
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            filePath
        )

        // Create an intent to view the CSV file
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            // Start the activity
            requireContext().startActivity(intent)

            // Display a toast message with the file path
            Toast.makeText(context, "File saved to $filePath", Toast.LENGTH_LONG).show()
//            Log.d("Path Tag","$filePath")
        } else {
            // Show an error message
            Toast.makeText(context, "No app found to open PDF file", Toast.LENGTH_SHORT).show()
        }
    }

    fun exportToExcel(adapter: TransectionListAdapter, context: Context) {
        // Get the data from your adapter
        val data = adapter.list


        // Create a new CSV writer
        val fileName = "EXBIN" + "$cDate" + "Transaction.csv"
        val file = File(context.filesDir, fileName)
        val writer = CSVWriter(FileWriter(file))

        // Write the header row
        writer.writeNext(arrayOf("Category", "Amount", "Date", "Note"))

        // Write the data to the CSV file
        for (item in data) {
            val category = item.category
            val amount = if (item.dType == "EXPENSE") "-${item.amount}" else "${item.amount}"
            val date = item.date
            val note = item.note
            writer.writeNext(arrayOf(category, amount, date, note))
        }

        // Close the writer
        writer.close()

        // Get the file URI using a FileProvider
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        // Create an intent to view the CSV file
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "text/csv")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        // Check if there is an app that can handle the intent
        if (intent.resolveActivity(context.packageManager) != null) {
            // Start the activity
            context.startActivity(intent)

            // Display a toast message with the file path
            val filePath = file.absolutePath
            Toast.makeText(context, "File saved to $filePath", Toast.LENGTH_LONG).show()
            Log.d("Path Tag", "$filePath")
        } else {
            // Show an error message
            Toast.makeText(context, "No app found to open CSV file", Toast.LENGTH_SHORT).show()
        }
    }

    fun adapterOfTransection(
        it: List<incexpTbl>,
        categoryMap: MutableMap<String, Categories>,
        currencyClass: getCurrencyClass
    ): TransectionListAdapter {
        val adapter = TransectionListAdapter(
            requireContext(),
            it,
            categoryMap,
            currencyClass
        ) { value, mode ->
            val id = value.Id
            val amaount = value.amount
            val category = value.category
            val month = value.sMonth
            val date = value.date
            val pMode = value.paymentMode
            val note = value.note
            val time = value.time
            val pmtIndex = value.paymentModeIndex
            if (mode == "INCOME") {
                val ldf = IncomeActivity()
                val args = Bundle()
                args.putString("INC_", "INCOME")
                args.putString("id", "$id")
                args.putString("amt", amaount)
                args.putString("cty", category)
                args.putString("dt", date)
                args.putString("pmd", pMode)
                args.putString("nt", note)
                args.putString("time", time)
                args.putString("month", month)
                args.putString("PMIND", pmtIndex)
                ldf.setArguments(args)
                //Toast.makeText(requireContext(), "$args", Toast.LENGTH_SHORT).show()
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container, ldf)
                    ?.commit()
            } else if (mode == "EXPENSE") {
                val ldf = ExpenseActivity()
                val args = Bundle()
                args.putString("EXP_", "EXPENSE")
                args.putString("id", "$id")
                args.putString("amt", amaount)
                args.putString("cty", category)
                args.putString("dt", date)
                args.putString("pmd", pMode)
                args.putString("nt", note)
                args.putString("time", time)
                args.putString("month", month)
                args.putString("PMIND", pmtIndex)
                ldf.setArguments(args)
                //Toast.makeText(requireContext(), "$args", Toast.LENGTH_SHORT).show()
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container, ldf)
                    ?.commit()
            }
        }
        return adapter
    }


    fun loadAd(
        dao: AppDataBase,
        currencyClass: getCurrencyClass,
        createFilter: ConstraintLayout,
        categoryMap: MutableMap<String, Categories>
    ) {
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            requireContext(),
            FireBaseGooggleAdsInterId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError?.toString()!!)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(TAG, "Ad was clicked.")
                            }

                            override fun onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                Log.d(TAG, "Ad dismissed fullscreen content.")
                                createFilter.visibility = View.GONE
                                if (PDF_OR_EXCEL) {
                                    Log.d(TAG, "PDF_OR_EXCEL == true")

                                    dao.incexpTblDao().getAllData().observe(requireActivity()) {
                                        val adapter =
                                            adapterOfTransection(it, categoryMap, currencyClass)
                                        generatePdf(it, categoryMap, currencyClass, adapter)

                                    }
                                    mInterstitialAd = null
                                } else {
                                    Log.d(TAG, "PDF_OR_EXCEL == false")

                                    dao.incexpTblDao().getAllData().observe(requireActivity()) {
                                        val adapter =
                                            adapterOfTransection(it, categoryMap, currencyClass)
                                        exportToExcel(adapter, requireContext())
                                    }
                                    mInterstitialAd = null

                                }
                                PDF_OR_EXCEL = true
                                binding.rdbPdf.isChecked = false
                                binding.rdbExcel.isChecked = false

                            }

                            override fun onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(TAG, "Ad recorded an impression.")
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad showed fullscreen content.")
                            }
                        }

                }
            })
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.showBottomNavigationView()
    }
}