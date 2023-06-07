package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.MainActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.CategoryListAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentCategoryListBinding


class CategoryListFragment : Fragment() {


    private lateinit var binding : FragmentCategoryListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCategoryListBinding.inflate(layoutInflater)
        val custom = binding.appBar
        custom.ivDelete.visibility = View.GONE
        custom.ivBack.setOnClickListener {
            loadFragment(MoreFragment())
        }
        custom.ivTitle.setText("Category")
        val dao = AppDataBase.getInstance(requireContext()).categoriesDao()
        dao.getAllCategory().observe(requireActivity()) {
//                Toast.makeText(requireContext(), "${dao.getAllPaymentMode()}", Toast.LENGTH_SHORT).show()
            val layoutManager = LinearLayoutManager(requireContext())
            val recy = binding.recyclerView

            recy.layoutManager = layoutManager
            val adapter = CategoryListAdapter(requireContext(),it){Category, buttonClicked ->

                if(buttonClicked == "EDIT"){
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