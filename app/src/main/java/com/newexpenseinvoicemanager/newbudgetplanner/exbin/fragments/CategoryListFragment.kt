package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.MainActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.CategoryListAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentCategoryListBinding


class CategoryListFragment : Fragment() {


    private lateinit var binding: FragmentCategoryListBinding
    var valueToCheck: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCategoryListBinding.inflate(layoutInflater)
        val custom = binding.appBar
        custom.ivDelete.visibility = View.GONE
        (activity as MainActivity?)!!.floatButtonHide()



        val INCOME_ACTIVITY = arguments?.getString("SELECT_CAT_INC")
        val INCOME_ACTIVITY_UPDATE = arguments?.getString("SELECT_CAT_INC_FOR_UPDATE")

        val EXPENSE_ACTIVITY_UPDATE = arguments?.getString("SELECT_CAT_EXP_FOR_UPDATE")
        val EXPENSE_ACTIVITY = arguments?.getString("SELECT_CAT_EXP")

//        vle = INCOME_ACTIVITY!!
        if (INCOME_ACTIVITY == null && EXPENSE_ACTIVITY == null && INCOME_ACTIVITY_UPDATE == null && EXPENSE_ACTIVITY_UPDATE == null) {
            custom.ivBack.visibility = View.VISIBLE
            custom.ivBack.setOnClickListener {
                loadFragment(MoreFragment())
            }
            custom.ivTitle.setText("Category")
            binding.floatingActionButton.visibility = View.VISIBLE
            val dao = AppDataBase.getInstance(requireContext()).categoriesDao()
            dao.getAllCategory().observe(requireActivity()) {
//                Toast.makeText(requireContext(), "${dao.getAllPaymentMode()}", Toast.LENGTH_SHORT).show()
                val layoutManager = LinearLayoutManager(requireContext())
                val recy = binding.recyclerView
                recy.layoutManager = layoutManager

                val adapter = CategoryListAdapter(
                    requireContext(),
                    it,
                    INCOME_ACTIVITY,
                    EXPENSE_ACTIVITY,
                    INCOME_ACTIVITY_UPDATE,
                    EXPENSE_ACTIVITY_UPDATE
                ) { Category, buttonClicked ->
                    if (buttonClicked == "EDIT") {
                        val ldf = AddCategoriesFragment()
                        val args = Bundle()
                        args.putString("EDIT", "${Category.categoryId}")
//                    args.putString("Icon","${Category.CategoryImage}")
//                    args.putString("Color","${Category.CategoryColor}")
                        ldf.setArguments(args)
                        val transaction = activity?.supportFragmentManager?.beginTransaction()
                        transaction?.replace(R.id.fragment_container, ldf)
                        transaction?.addToBackStack(null)
                        transaction?.commit()
                    }
                }
                recy.adapter = adapter
            }
            binding.floatingActionButton.setOnClickListener {

                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container, AddCategoriesFragment())
                    ?.addToBackStack(null)
                    ?.commit()
            }
        } else {
            custom.ivBack.visibility = View.GONE
            custom.ivTitle.setText("Select Category")
            binding.floatingActionButton.visibility = View.GONE
            val dao = AppDataBase.getInstance(requireContext()).categoriesDao()
            dao.getAllCategory().observe(requireActivity()) {
//                Toast.makeText(requireContext(), "$INCOME_ACTIVITY_UPDATE", Toast.LENGTH_SHORT).show()
//                Toast.makeText(requireContext(), "${dao.getAllPaymentMode()}", Toast.LENGTH_SHORT).show()
                val layoutManager = LinearLayoutManager(requireContext())
                val recy = binding.recyclerView
                recy.layoutManager = layoutManager

                val adapter = CategoryListAdapter(
                    requireContext(),
                    it,
                    INCOME_ACTIVITY,
                    EXPENSE_ACTIVITY,
                    INCOME_ACTIVITY_UPDATE,
                    EXPENSE_ACTIVITY_UPDATE
                ) { Category, buttonClicked ->

                    if (INCOME_ACTIVITY_UPDATE != null && INCOME_ACTIVITY_UPDATE == "11") {
                        valueToCheck = "INCOME_ACTIVITY_UPDATE"
//                        Toast.makeText(requireContext(), "Income Update", Toast.LENGTH_SHORT).show()
                        val ID_ = arguments?.getString("id")
                        val AMNT_ = arguments?.getString("amt")
                        val DATE_ = arguments?.getString("dt")
                        val TIME_ = arguments?.getString("time")
                        val PAY_ = arguments?.getString("pmd")
                        val PAY_MD_ = arguments?.getString("PMIND")
                        val NOTE_ = arguments?.getString("nt")
                        val SMONTH_ = arguments?.getString("month")
                        if (buttonClicked != null) {
                            if (INCOME_ACTIVITY_UPDATE == "11") {
                                val ldf = IncomeActivity()
                                val args = Bundle()
                                args.putString("INC_", "INCOME")
                                args.putString("id", ID_)
                                args.putString("amt", AMNT_)
                                args.putString("dt", DATE_)
                                args.putString("pmd", PAY_)
                                args.putString("nt", NOTE_)
                                args.putString("time", TIME_)
                                args.putString("month", SMONTH_)
                                args.putString("PMIND", PAY_MD_)
                                args.putString("CATEGORY_Update", "${Category.CategoryName}")
                                ldf.setArguments(args)
                                //Toast.makeText(requireContext(), "$args", Toast.LENGTH_SHORT).show()
                                activity?.supportFragmentManager?.beginTransaction()
                                    ?.replace(R.id.fragment_container, ldf)
                                    ?.commit()
                            }
                        }
                    } else if (EXPENSE_ACTIVITY_UPDATE != null && EXPENSE_ACTIVITY_UPDATE == "22") {
                        valueToCheck = "EXPENSE_ACTIVITY_UPDATE"

                        val ID_ = arguments?.getString("id")
                        val AMNT_ = arguments?.getString("amt")
                        val DATE_ = arguments?.getString("dt")
                        val TIME_ = arguments?.getString("time")
                        val PAY_ = arguments?.getString("pmd")
                        val PAY_MD_ = arguments?.getString("PMIND")
                        val NOTE_ = arguments?.getString("nt")
                        val SMONTH_ = arguments?.getString("month")
                        if (buttonClicked != null) {
                            if (EXPENSE_ACTIVITY_UPDATE == "22") {
                                val ldf = ExpenseActivity()
                                val args = Bundle()
                                args.putString("EXP_", "EXPENSE")
                                args.putString("id", ID_)
                                args.putString("amt", AMNT_)
                                args.putString("dt", DATE_)
                                args.putString("pmd", PAY_)
                                args.putString("nt", NOTE_)
                                args.putString("time", TIME_)
                                args.putString("month", SMONTH_)
                                args.putString("PMIND", PAY_MD_)
                                args.putString("CATEGORY_Update", "${Category.CategoryName}")
                                ldf.setArguments(args)
                                //Toast.makeText(requireContext(), "$args", Toast.LENGTH_SHORT).show()
                                activity?.supportFragmentManager?.beginTransaction()
                                    ?.replace(R.id.fragment_container, ldf)
                                    ?.commit()
                            }
                        }
                    } else {
                        if (INCOME_ACTIVITY != null && INCOME_ACTIVITY == "001") {
                            valueToCheck = "INCOME_ACTIVITY"

                            val amnt = arguments?.getString("AMNT_VL")
                            val nte = arguments?.getString("NOTE_VL")
                            val dte = arguments?.getString("DATE_VL")
                            val tme = arguments?.getString("TIME_VL")
                            val pay_ind = arguments?.getString("PAY_IND_VL")
                            if (buttonClicked != null && INCOME_ACTIVITY == "001") {
                                val ldf = IncomeActivity()
                                val args = Bundle()
                                args.putString("CATEGORY", Category.CategoryName)
                                args.putString("amnt_vle", amnt)
                                args.putString("nte_vle", nte)
                                args.putString("dte_vle", dte)
                                args.putString("tme_vle", tme)
                                args.putString("pay_ind_vle", pay_ind)
                                ldf.arguments = args
                                activity?.supportFragmentManager?.beginTransaction()
                                    ?.replace(R.id.fragment_container, ldf)
                                    ?.commit()
                            }

                        } else {
                            valueToCheck = "EXPENSE_ACTIVITY"
                            custom.ivBack.visibility = View.GONE
                            binding.floatingActionButton.visibility = View.GONE
                            val amnt = arguments?.getString("AMNT_VL")
                            val nte = arguments?.getString("NOTE_VL")
                            val dte = arguments?.getString("DATE_VL")
                            val tme = arguments?.getString("TIME_VL")
                            val pay_ind = arguments?.getString("PAY_IND_VL")
                            val ldf = ExpenseActivity()
                            if (buttonClicked != null && EXPENSE_ACTIVITY == "002") {
                                val args = Bundle()
                                args.putString("CATEGORY1", Category.CategoryName)
                                args.putString("amnt_vle", amnt)
                                args.putString("nte_vle", nte)
                                args.putString("dte_vle", dte)
                                args.putString("tme_vle", tme)
                                args.putString("pay_ind_vle", pay_ind)
                                ldf.arguments = args

                                activity?.supportFragmentManager?.beginTransaction()
                                    ?.replace(R.id.fragment_container, ldf)
                                    ?.commit()
                            }
                        }
                    }
                }
                recy.adapter = adapter
            }
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

    private fun loadFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }


}