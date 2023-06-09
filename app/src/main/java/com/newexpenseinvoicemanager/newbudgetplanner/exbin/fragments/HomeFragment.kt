package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments


import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.ads.nativetemplates.rvadapter.AdmobNativeAdAdapter
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.MainActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.TransectionListAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.getCurrencyClass
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentHomeBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.Categories


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var pieChart: PieChart
    private var inc: Double = 0.0
    private var exp: Double = 0.0
    var crnSymb: String? = ""
    private var FireBaseGooggleAdsId: String = ""
    private lateinit var nativeAd: NativeAd
    private var isAds: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        (activity as MainActivity?)!!.getIdofNativeAds()
        val preference =
            requireContext().getSharedPreferences("NativeId", AppCompatActivity.MODE_PRIVATE)
        FireBaseGooggleAdsId = preference.getString("Na_tive_id", "")!!
        isAds = preference.getBoolean("isShow", false)


        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (isAds == true) {
            if (networkInfo != null && networkInfo.isConnected) {

                val adLoader = AdLoader.Builder(requireContext(), FireBaseGooggleAdsId)
                    .forNativeAd { nativeAd ->
                        val styles =
                            NativeTemplateStyle.Builder().build()
                        val template: TemplateView = binding.myTemplate
                        template.setStyles(styles)
                        template.setNativeAd(nativeAd)
                    }
                    .build()

                adLoader.loadAd(AdRequest.Builder().build())
            }else {
                binding.myTemplate.visibility = View.GONE
            }
        } else {
            binding.myTemplate.visibility = View.GONE
        }
        (activity as MainActivity?)!!.showBottomNavigationView()
        val currencyClass = getCurrencyClass(viewLifecycleOwner, requireContext())
        // showData()
        getTotalIncome(currencyClass)
        getDailyAvg(currencyClass)
        getDailyAvgExp(currencyClass)
        getTotalExpense(currencyClass)
        getCurrentBalance(currencyClass)
        // createPieChart()
        //val currencyClass = getCurrencyClass(viewLifecycleOwner, requireContext())

        // crnSymb = currencyClass.getCurrencies()!!

        val dao = AppDataBase.getInstance(requireContext())
        val categoryMap = mutableMapOf<String, Categories>()
        dao.categoriesDao().getAllCategory().observe(requireActivity()) { categories ->
            categories?.forEach { category ->
                categoryMap[category.CategoryName!!] = category
            }
        }

        dao.incexpTblDao().getAllDataHome().observe(requireActivity()) { transactions ->
            if (transactions != null && transactions.isNotEmpty()) {
                showData()

                if (isAds == true) {
                    val adapter = TransectionListAdapter(
                        requireContext(),
                        transactions,
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
                                ?.addToBackStack(null)
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
                                ?.addToBackStack(null)
                                ?.commit()
                        }
                    }
                    val admobNativeAdAdapter = AdmobNativeAdAdapter.Builder.with(
                        FireBaseGooggleAdsId,
                        adapter,
                        "small"   // "medium" it can also used
                    ).adItemInterval(2).build()
                    binding.homeTranRecy.adapter = admobNativeAdAdapter
                } else {
                    val adapter = TransectionListAdapter(
                        requireContext(),
                        transactions,
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
                                ?.addToBackStack(null)
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
                                ?.addToBackStack(null)
                                ?.commit()
                        }
                    }
                    binding.homeTranRecy.adapter = adapter
                }
            } else {
                // handle empty transaction list
                hideData()
            }
        }

        binding.incCard.setOnClickListener {
            if (getTotalIncome(currencyClass) != null) {
                val ldf = TransectionListFragment()
                val args = Bundle()
                args.putString("TRANSECTIONKEY", "INCOME")
                ldf.setArguments(args)
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.fragment_container, ldf)
                transaction?.addToBackStack(null)
                transaction?.commit()
            }
        }

        binding.expCard.setOnClickListener {
            if (getTotalExpense(currencyClass) != null) {
                val ldf = TransectionListFragment()
                val args = Bundle()
                args.putString("TRANSECTIONKEY", "EXPENSE")
                ldf.setArguments(args)
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.fragment_container, ldf)
                transaction?.addToBackStack(null)
                transaction?.commit()
            }
        }

        binding.tvSeeAll.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragment_container, TransectionFragment())
            transaction?.addToBackStack(null)
            transaction?.commit()
            (activity as MainActivity?)!!.setBottomNavigationAsDesire(R.id.navigation_transection)
        }

        return binding.root
    }

    fun getTotalIncome(currencyClass: getCurrencyClass) {
        val dao = AppDataBase.getInstance(requireContext()).incexpTblDao()

        dao.getTotalIncomeAmount().observe(requireActivity()) { income ->
            if (income != null) {
                inc = income
                val formattedAvg = String.format("%.2f", inc)
                currencyClass.getCurrencies { crnSymb ->
                    binding.totalIncome.setText("$crnSymb $formattedAvg")
                }
                createPieChart()
            } else {
                // handle empty income
                hideData()
                currencyClass.getCurrencies { crnSymb ->
                    binding.totalIncome.setText("$crnSymb 00.00")
                }
            }
        }
    }

    fun getTotalExpense(currencyClass: getCurrencyClass) {
        val dao = AppDataBase.getInstance(requireContext()).incexpTblDao()

        dao.getTotalExpenseAmount().observe(requireActivity()) { expense ->
            if (expense != null) {
                exp = expense
                val formattedAvg = String.format("%.2f", exp)
                currencyClass.getCurrencies { crnSymb ->
                    binding.expTtl.setText("$crnSymb $formattedAvg")
                }
                createPieChart()
            } else {
                // handle empty expense
                hideData()
                currencyClass.getCurrencies { crnSymb ->

                    binding.expTtl.setText("$crnSymb 00.00")
                }
            }
        }
    }

    fun getDailyAvg(currencyClass: getCurrencyClass) {
        val dao = AppDataBase.getInstance(requireContext()).incexpTblDao()
        dao.getDailyAverage().observe(requireActivity()) { dailyAverage ->
            if (dailyAverage != null) {
                val avg = dailyAverage
                val formattedAvg = String.format("%.2f", avg)
                currencyClass.getCurrencies { crnSymb ->
                    binding.avgTtl.text = "$crnSymb $formattedAvg"
                }
            } else {
                hideData()
                currencyClass.getCurrencies { crnSymb ->

                    binding.avgTtl.text = "$crnSymb 00.00"
                }
            }
        }
    }

    fun getDailyAvgExp(currencyClass: getCurrencyClass) {
        val dao = AppDataBase.getInstance(requireContext()).incexpTblDao()

        dao.getDailyAverageExp().observe(requireActivity()) { dailyAverage ->
            if (dailyAverage != null) {
                val avg = dailyAverage
                val formattedAvg = String.format("%.2f", avg)
                currencyClass.getCurrencies { crnSymb ->
                    binding.expAvg.text = "$crnSymb $formattedAvg"
                }
            } else {
                hideData()
                currencyClass.getCurrencies { crnSymb ->

                    binding.expAvg.text = "$crnSymb 00.00"
                }
            }
        }
    }

    private fun getCurrentBalance(currencyClass: getCurrencyClass) {
        val dao = AppDataBase.getInstance(requireContext()).incexpTblDao()

        dao.getIncomeExpenseDifference().observe(requireActivity()) { differnce ->

            if (differnce != null) {
                if (differnce >= 0) {
                    currencyClass.getCurrencies { crnSymb ->
                        binding.currentBalanceTxt.text = "$crnSymb $differnce"
                    }
                    binding.currentBalanceTxt.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.transectionGreen
                        )
                    )
                } else {
                    currencyClass.getCurrencies { crnSymb ->
                        binding.currentBalanceTxt.text = "$crnSymb ${differnce}"
                    }
                    binding.currentBalanceTxt.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            com.newexpenseinvoicemanager.newbudgetplanner.exbin.R.color.transectionRed
                        )
                    )

                }
            } else {
                hideData()
            }
        }
    }


    fun hideData() {
        binding.textviewdata.visibility = View.VISIBLE
        binding.seeAll.visibility = View.GONE
        binding.piechartay.visibility = View.GONE
        binding.homeTranRecy.visibility = View.GONE
    }

    fun showData() {
        binding.textviewdata.visibility = View.GONE
        binding.seeAll.visibility = View.VISIBLE
        binding.piechartay.visibility = View.VISIBLE
        binding.homeTranRecy.visibility = View.VISIBLE
    }

    private fun createPieChart() {
        pieChart = binding.pieChart
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = false

        val entries = ArrayList<PieEntry>()
        val dataSet = PieDataSet(entries, "")

        if (inc == 0.0 && exp != 0.0) {
            entries.add(PieEntry(exp.toFloat(), "Expense"))
            dataSet.colors = listOf(ColorTemplate.rgb("#F44336"))
        } else if (inc != 0.0 && exp == 0.0) {
            entries.add(PieEntry(inc.toFloat(), "Income"))
            dataSet.colors = listOf(ColorTemplate.rgb("#4CAF50"))
        } else if (inc != 0.0 && exp != 0.0) {
            entries.add(PieEntry(inc.toFloat(), "Income"))
            entries.add(PieEntry(exp.toFloat(), "Expense"))
            dataSet.colors = listOf(ColorTemplate.rgb("#4CAF50"), ColorTemplate.rgb("#F44336"))
        }

        val data = PieData(dataSet)
        data.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("%.2f%%", value)
            }
        })
        data.setValueTextSize(14f)
        data.setValueTextColor(Color.WHITE)

        pieChart.data = data
        pieChart.animateY(1000, Easing.EaseInOutQuad)
        pieChart.setHoleColor(Color.TRANSPARENT) // set hole color to transparent
        pieChart.setHoleRadius(50f) // set hole radius
        pieChart.invalidate()
    }


}

