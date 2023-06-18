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
    private var isData: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCategoryListBinding.inflate(layoutInflater)
        val custom = binding.appBar
        custom.ivDelete.visibility = View.GONE


        val INCOME_ACTIVITY = arguments?.getString("SELECT_CAT_INC")
        val EXPENSE_ACTIVITY = arguments?.getString("SELECT_CAT_EXP")

//        vle = INCOME_ACTIVITY!!
        if (INCOME_ACTIVITY == null && EXPENSE_ACTIVITY == null) {
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
                    EXPENSE_ACTIVITY
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
//                Toast.makeText(requireContext(), "${dao.getAllPaymentMode()}", Toast.LENGTH_SHORT).show()
                val layoutManager = LinearLayoutManager(requireContext())
                val recy = binding.recyclerView
                recy.layoutManager = layoutManager

                val adapter = CategoryListAdapter(
                    requireContext(),
                    it,
                    INCOME_ACTIVITY,
                    EXPENSE_ACTIVITY
                ) { Category, buttonClicked ->

                    if (INCOME_ACTIVITY != null && INCOME_ACTIVITY == "001" ) {

                        if (buttonClicked != null) {
                            if (INCOME_ACTIVITY == "001") {
                                val ldf = IncomeActivity()
                                val args = Bundle()
                                args.putString("CATEGORY", "${Category.CategoryName}")
                                ldf.setArguments(args)
                                //Toast.makeText(requireContext(), "$args", Toast.LENGTH_SHORT).show()
                                activity?.supportFragmentManager?.beginTransaction()
                                    ?.replace(R.id.fragment_container, ldf)
                                    ?.commit()
                            }
                        }
                    }else{
                        custom.ivBack.visibility = View.GONE
                        binding.floatingActionButton.visibility = View.GONE

                        if (EXPENSE_ACTIVITY == "002"){
                            val EXP = ExpenseActivity()
                            val args = Bundle()
                            args.putString("CATEGORY1", "${Category.CategoryName}")
                            EXP.setArguments(args)
                            Toast.makeText(requireContext(), "$args", Toast.LENGTH_SHORT).show()
                            activity?.supportFragmentManager?.beginTransaction()
                                ?.replace(R.id.fragment_container, EXP)
                                ?.commit()
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

    fun getIsData(): String {
        return isData
    }


}