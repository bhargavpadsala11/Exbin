package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments


import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.TransectionListAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.getCurrencyClass
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentTransectionBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.Categories
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.incexpTbl
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class TransectionFragment : Fragment() {


    private lateinit var binding: FragmentTransectionBinding
    private var sDate: String = ""
    private var lDate: String = ""
    private var tMode: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTransectionBinding.inflate(layoutInflater)

        val custom = binding.appBar
        val filter = binding.clFliter

        custom.ivBack.setOnClickListener { loadFragment(HomeFragment()) }
        custom.ivTitle.setText("Transaction List")

        custom.ivDelete.setImageResource(R.drawable.ic_filter)


        val currencyClass = getCurrencyClass(viewLifecycleOwner, requireContext())
        val dao = AppDataBase.getInstance(requireContext())
        val categoryMap = mutableMapOf<String, Categories>()

        val inComeButton = binding.btnIncome
        val exPenseButton = binding.btnExpense

        custom.ivTitle.setOnClickListener {
            //generatePdf(it, categoryMap, currencyClass)
            dao.incexpTblDao().getAllData().observe(requireActivity()) {
            val adapter =
                TransectionListAdapter(
                    requireContext(),
                    it,
                    categoryMap,
                    currencyClass
                ) { value, mode ->

                }

                generatePdf(it,categoryMap,currencyClass)
        }

        }
        dao.categoriesDao().getAllCategory().observe(requireActivity()) { categories ->
            categories.forEach { category ->
                categoryMap[category.CategoryName!!] = category
            }
        }

        dao.incexpTblDao().getAllData().observe(requireActivity()) {
            binding.transectionItem.adapter =
                TransectionListAdapter(
                    requireContext(),
                    it,
                    categoryMap,
                    currencyClass
                ) { value, mode ->

                }
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
            ViewCompat.setBackgroundTintList(inComeButton, ContextCompat.getColorStateList(requireContext(), R.color.dark_main_color))
            exPenseButton.backgroundTintList = null

        }

        binding.btnExpense.setOnClickListener {
            tMode = "EXPENSE"
            binding.btnExpense.requestFocus()
            ViewCompat.setBackgroundTintList(exPenseButton, ContextCompat.getColorStateList(requireContext(), R.color.dark_main_color))
            inComeButton.backgroundTintList = null
        }

        binding.btnReset.setOnClickListener {
            sDate = ""
            lDate = ""
            tMode = ""
            filter.visibility = View.GONE
            getTransecton(currencyClass)
            inComeButton.backgroundTintList = null
            exPenseButton.backgroundTintList = null
        }

        binding.btnApply.setOnClickListener {
            filter.visibility = View.GONE
            if (sDate != null && lDate != null && tMode == "INCOME") {
                dao.incexpTblDao().getAllIncomeDataByDate(sDate, lDate).observe(requireActivity()) {
                    binding.transectionItem.adapter =
                        TransectionListAdapter(
                            requireContext(),
                            it,
                            categoryMap,
                            currencyClass
                        ) { value, mode ->

                        }
                }
            } else if (sDate != null && lDate != null && tMode == "EXPENSE") {

                dao.incexpTblDao().getAllExpenseDataByDate(sDate, lDate)
                    .observe(requireActivity()) {
                        binding.transectionItem.adapter =
                            TransectionListAdapter(
                                requireContext(),
                                it,
                                categoryMap,
                                currencyClass
                            ) { value, mode ->

                            }
                    }
            } else if (sDate != null && lDate != null) {
                dao.incexpTblDao().getAllDataByTwoDate(sDate, lDate).observe(requireActivity()) {
                    binding.transectionItem.adapter =
                        TransectionListAdapter(
                            requireContext(),
                            it,
                            categoryMap,
                            currencyClass
                        ) { value, mode ->

                        }
                }
            }
        }

        custom.ivDelete.setOnClickListener {
            filter.visibility = View.VISIBLE

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
            binding.transectionItem.adapter =
                TransectionListAdapter(
                    requireContext(),
                    it,
                    categoryMap,
                    currencyClass
                ) { value, mode ->

                }
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

//    private fun generatePdf(
//        incexpTbls: List<incexpTbl>,
//        categoryMap: MutableMap<String, Categories>,
//        currencyClass: getCurrencyClass
//    ) {
//        val document = Document()
//        val fileName = "transaction_list.pdf"
//        val filePath = requireContext().getExternalFilesDir(null)?.absolutePath + "/" + fileName
//        val outputStream = FileOutputStream(filePath)
//        PdfWriter.getInstance(document, outputStream)
//
//        document.open()
//
//        // Add adapter data to PDF
//        val adapter = TransectionListAdapter(requireContext(), incexpTbls, categoryMap, currencyClass){a,b ->}
//        val table = PdfPTable(4)
//        table.addCell("Category")
//        table.addCell("Date")
//        table.addCell("Note")
//        table.addCell("Amount")
//
//        //add Empty Cell
//        val emptyCell = PdfPCell()
//        emptyCell.border = PdfPCell.NO_BORDER
//        emptyCell.fixedHeight = 20f
//        table.addCell(emptyCell)
//
//        for (i in 0 until adapter.itemCount) {
//            val item = adapter.list[i]
//            val category = adapter.categoryMap[item.category]
//
//            // Add item data to PDF
//            table.addCell(item.category)
//            table.addCell(item.date)
//            table.addCell(item.note)
//            table.addCell(item.amount.toString())
//        }
//
//        document.add(table)
//        document.close()
//
//        document.close()
//
//        // Open the PDF file
//        val file = File(filePath)
//        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.setDataAndType(uri, "application/pdf")
//        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//        startActivity(intent)
//    }

    private fun generatePdf(
        incexpTbls: List<incexpTbl>,
        categoryMap: MutableMap<String, Categories>,
        currencyClass: getCurrencyClass
    ) {
        val document = Document()
        val fileName = "transaction_list.pdf"
        val filePath = requireContext().getExternalFilesDir(null)?.absolutePath + "/" + fileName
        val outputStream = FileOutputStream(filePath)
        PdfWriter.getInstance(document, outputStream)

        document.open()

        // Add adapter data to PDF
        val adapter = TransectionListAdapter(requireContext(), incexpTbls, categoryMap, currencyClass){a,b ->}
        val font = Font(Font.FontFamily.TIMES_ROMAN, 22f)
        val table = PdfPTable(4)
        table.addCell(Phrase("Category",font))
        table.addCell(Phrase("Date",font))
        table.addCell(Phrase("Note",font))
//        val notCell = PdfPCell(Phrase("Note",font))
//        notCell.minimumHeight = 10f
//        table.addCell(notCell)
        table.addCell(Phrase("Amount",font))

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
            val category = adapter.categoryMap[item.category]

            // Add item data to PDF
            table.addCell(Phrase(item.category,font))
            table.addCell(Phrase(item.date,font))
            table.addCell(Phrase(item.note,font))
//            val noteCell = PdfPCell(Phrase(item.note,font))
//            noteCell.minimumHeight = 10f
//            table.addCell(noteCell)
            table.addCell(Phrase(item.amount.toString(),font))
        }

        document.add(table)
        document.close()

        // Open the PDF file
        val file = File(filePath)
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(intent)
    }


}