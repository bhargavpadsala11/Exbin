package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.ads.nativetemplates.rvadapter.AdmobNativeAdAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.MainActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.TransectionListAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.getCurrencyClass
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentTransectionListBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.Categories
import java.text.SimpleDateFormat
import java.util.*

class TransectionListFragment : Fragment() {


    private lateinit var binding: FragmentTransectionListBinding
    private var inc: Double = 0.0
    private var isAds: Boolean = false
    private var sDate: String = ""
    private var lDate: String = ""
    private var FireBaseGooggleAdsId: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTransectionListBinding.inflate(layoutInflater)
        (activity as MainActivity?)!!.floatButtonHide()
        val preference =
            requireContext().getSharedPreferences("NativeId", AppCompatActivity.MODE_PRIVATE)
        FireBaseGooggleAdsId = preference.getString("Na_tive_id", "")!!
        isAds = preference.getBoolean("isShow", false)



        val custom = binding.appBar
        custom.ivBack.setOnClickListener {
            loadFragment(HomeFragment())
        }
        custom.ivDelete.visibility = View.GONE

        val currencyClass = getCurrencyClass(viewLifecycleOwner, requireContext())

        val dao = AppDataBase.getInstance(requireContext())
        val categoryMap = mutableMapOf<String, Categories>()
        dao.categoriesDao().getAllCategory().observe(requireActivity()) { categories ->
            categories.forEach { category ->
                categoryMap[category.CategoryName!!] = category
            }
        }

        val value = arguments?.getString("TRANSECTIONKEY")

        if (value == "INCOME") {
            custom.ivTitle.setText("Income Transection")
            getIncomeData(dao, categoryMap, currencyClass)

        } else if (value == "EXPENSE") {
            custom.ivTitle.setText("Expense Transection")
            getExpenseData(dao, categoryMap, currencyClass)

        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.hideBottomNavigationView()
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity?)!!.showBottomNavigationView()
    }

//    fun getTotalIncome() {
//        val dao = AppDataBase.getInstance(requireContext()).incexpTblDao()
//
//        dao.getTotalIncomeAmount().observe(requireActivity()) { income ->
//            if (income != null) {
//                inc = income
//                val formattedAvg = String.format("%.2f", inc)
//                binding.totalIncome.setText(formattedAvg)
//            } else {
//                // handle empty income
//                binding.totalIncome.setText("00.00")
//            }
//        }
//    }
//
//    fun getTotalExpense() {
//        val dao = AppDataBase.getInstance(requireContext()).incexpTblDao()
//
//        dao.getTotalExpenseAmount().observe(requireActivity()) { expense ->
//            if (expense != null) {
//                exp = expense
//                val formattedAvg = String.format("%.2f", exp)
//                binding.totalIncome.setText(formattedAvg)
//
//            } else {
//                // handle empty expense
//                binding.totalIncome.setText("00.00")
//            }
//        }
//    }
//
//    fun getDailyAvg() {
//        val dao = AppDataBase.getInstance(requireContext()).incexpTblDao()
//
//        dao.getDailyAverage().observe(requireActivity()) { dailyAverage ->
//            if (dailyAverage != null) {
//                val avg = dailyAverage
//                val formattedAvg = String.format("%.2f", avg)
//                binding.avgTtl.text = formattedAvg
//            } else {
//                binding.avgTtl.text = "00.00"
//            }
//        }
//    }
//
//    fun getDailyAvgExp() {
//        val dao = AppDataBase.getInstance(requireContext()).incexpTblDao()
//
//        dao.getDailyAverageExp().observe(requireActivity()) { dailyAverage ->
//            if (dailyAverage != null) {
//                val avg = dailyAverage
//                val formattedAvg = String.format("%.2f", avg)
//                binding.avgTtl.text = formattedAvg
//            } else {
//                binding.avgTtl.text = "00.00"
//            }
//        }
//    }

    private fun loadFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)
            ?.addToBackStack(null)
            ?.commit()
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
                binding.tvAddEnddate.setText("$lDate")

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
                binding.tvStartdate.setText("$sDate")

            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }

    fun getExpenseData(
        dao: AppDataBase,
        categoryMap: MutableMap<String, Categories>,
        currencyClass: getCurrencyClass
    ) {
        dao.incexpTblDao().getAllExpenseData().observe(requireActivity()) {
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
                if (mode == "EXPENSE") {
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
            val admobNativeAdAdapter = AdmobNativeAdAdapter.Builder.with(
                FireBaseGooggleAdsId,
                adapter,
                "small"   // "medium" it can also used
            ).adItemInterval(4).build()
            binding.transectionRecy.adapter = admobNativeAdAdapter


        }

    }

    fun getIncomeData(
        dao: AppDataBase,
        categoryMap: MutableMap<String, Categories>,
        currencyClass: getCurrencyClass
    ) {


        dao.incexpTblDao().getAllIncomeData().observe(requireActivity()) {

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
                }
            }
            val admobNativeAdAdapter = AdmobNativeAdAdapter.Builder.with(
                FireBaseGooggleAdsId,
                adapter,
                "small"   // "medium" it can also used
            ).adItemInterval(4).build()

            binding.transectionRecy.adapter = admobNativeAdAdapter

        }

    }

}