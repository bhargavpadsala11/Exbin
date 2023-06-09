package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.content.Context
import android.graphics.*
import android.graphics.drawable.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.MainActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.ColorAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.IconAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentAddCategoriesBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.Categories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AddCategoriesFragment : Fragment() {

    private lateinit var binding: FragmentAddCategoriesBinding
    private var selectedIcon: Int? = null
    private var selectedColor: String? = null
    private var deleteCategoryView: View? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddCategoriesBinding.inflate(layoutInflater)
        val icons = listOf(
            R.drawable.ic_cat_cancel,
            R.drawable.ic_add_24,
            R.drawable.ic_edit,
            R.drawable.ic_expense,
            R.drawable.ic_home,
            R.drawable.ic_income,
            R.drawable.ic_menu_24,
            R.drawable.ic_next,
            R.drawable.ic_save_24,
            R.drawable.ic_text,
            R.drawable.icadd,
            R.drawable.iccheck,
            R.drawable.ic_new_add_home_24,
            R.drawable.ic_shop_production_quantity_limits_24,
            R.drawable.ic_storefront_24,
            R.drawable.ic_local_gas_station_24,
            R.drawable.ic_person_add_alt_24,
            R.drawable.ic_savings_24,
            R.drawable.more,
            R.drawable.icons8,
            R.drawable.basket,
            R.drawable.egg,
            R.drawable.family,
            R.drawable.fastfood,
            R.drawable.fitness,
            R.drawable.mortarboard
            // Add more icons here
        )
        val colors = listOf(
            "#002F6C",
            "#0039A6",
            "#0047B2",
            "#0052CC",
            "#0066FF",
            "#1A8FFF",
            "#3399FF",
            "#73BFFF",
            "#99CCFF",
            "#C2E0FF",
            "#D9E5FF",
            "#E6F0FF",
            "#F0F5FF",
            "#F5FAFF",
            "#FAFDFF",
            "#FFFFFF",
            "#E6F7FF",
            "#B3E0FF",
            "#80CCFF",
            "#4DA6FF",
            "#FFA07A",
            "#FF7F50",
            "#FF6347",
            "#FF4500",
            "#FF8C00",
            "#FFA500",
            "#FFB347",
            "#FFC0CB",
            "#FFDAB9",
            "#FFDEAD",
            "#FFE4B5",
            "#FFE4C4",
            "#FFE4E1",
            "#FFDEAD",
            "#FFDAB9",
            "#FFC0CB",
            "#FFB347",
            "#FFA500",
            "#FF8C00",
            "#FF6347",
            "#FFCDD2",
            "#EF9A9A",
            "#E57373",
            "#EF5350",
            "#F44336",
            "#E53935",
            "#D32F2F",
            "#C62828",
            "#B71C1C",
            "#FF8A80",
            "#FF5252",
            "#FF1744",
            "#D50000",
            "#FCE4EC",
            "#F8BBD0",
            "#F48FB1",
            "#F06292",
            "#EC407A",
            "#E91E63",
            "#D81B60",
            "#006400",
            "#008000",
            "#228B22",
            "#2E8B57",
            "#3CB371",
            "#32CD32",
            "#00FF00",
            "#7CFC00",
            "#ADFF2F",
            "#9ACD32",
            "#556B2F",
            "#6B8E23",
            "#7FFF00",
            "#8FBC8F",
            "#98FB98",
            "#00FA9A",
            "#00FF7F",
            "#7FFFD4",
            "#66CDAA",
            "#20B2AA",
            "#FFD700",
            "#FFBF00",
            "#FFC300",
            "#FFA500",
            "#FF8C00",
            "#FF7F50",
            "#FFDAB9",
            "#FFE4B5",
            "#FFE4C4",
            "#FFE4E1",
            "#FFF8DC",
            "#FFFF00",
            "#FFFFE0",
            "#FFFFF0",
            "#FAFAD2",
            "#EEE8AA",
            "#BDB76B",
            "#F0E68C",
            "#DAA520",
            "#FFDF00",
            "#E6E6FA",
            "#D8BFD8",
            "#DDA0DD",
            "#EE82EE",
            "#DA70D6",
            "#FF00FF",
            "#FF00FF",
            "#BA55D3",
            "#9370DB",
            "#8A2BE2",
            "#9400D3",
            "#9932CC",
            "#8B008B",
            "#800080",
            "#4B0082",
            "#6A5ACD",
            "#483D8B",
            "#7B68EE",
            "#6B69D6",
            "#A020F0",
            "#FF7F50",
            "#FF6347",
            "#FF4500",
            "#FF8C00",
            "#FFA500",
            "#FFB347",
            "#FFC0CB",
            "#FFDAB9",
            "#FFDEAD",
            "#FFE4B5",
            "#FFE4C4",
            "#FFE4E1",
            "#FFDEAD",
            "#FFDAB9",
            "#FFC0CB",
            "#FFB347",
            "#FFA500",
            "#FF8C00",
            "#FF6347",
            "#FF4500",
            "#0077BE",
            "#008CBA",
            "#00A1C9",
            "#00AEEF",
            "#00BFFF",
            "#1D62F0",
            "#3D85C6",
            "#5B8ABE",
            "#6FA8DC",
            "#7FB2E5",
            "#8FBCE6",
            "#A9CCE3",
            "#BFD4E7",
            "#C9D9E8",
            "#D9E8F5",
            "#E5EDF5",
            "#F0F5FA",
            "#F5FAFF",
            "#F7FBFF",
            "#FFFFFF",
            "#DC143C",
            "#FF0000",
            "#FF2400",
            "#FF4500",
            "#FF6347",
            "#FF7F50",
            "#FF8C00",
            "#FFA07A",
            "#FFA500",
            "#FFB6C1",
            "#FFC0CB",
            "#FFDAB9",
            "#FFDEAD",
            "#FFE4B5",
            "#FFE4C4",
            "#FFE4E1",
            "#FFEBCD",
            "#FFEFD5",
            "#FFF0F5",
            "#FFF5EE",
            "#008080",
            "#008B8B",
            "#00CED1",
            "#00FA9A",
            "#00FF7F",
            "#20B2AA",
            "#40E0D0",
            "#48D1CC",
            "#66CDAA",
            "#7FFFD4",
            "#AFEEEE",
            "#BBFFFF",
            "#E0FFFF",
            "#E6FFFF",
            "#F0FFFF",
            "#00FFFF",
            "#00FFFF",
            "#7FFFD4",
            "#40E0D0",
            "#48D1CC",
            "#0F52BA",
            "#1E90FF",
            "#4169E1",
            "#6495ED",
            "#00BFFF",
            "#87CEEB",
            "#87CEFA",
            "#ADD8E6",
            "#B0C4DE",
            "#B0E0E6",
            "#B2DFEE",
            "#BFEFFF",
            "#CAE1FF",
            "#E6E6FA",
            "#F0F8FF",
            "#F8F8FF",
            "#F0FFFF",
            "#00CED1",
            "#00FFFF",
            "#00FFFF",
            "#B76E79",
            "#C8AD7F",
            "#D8C3A5",
            "#E8D9CB",
            "#F8EFE1",
            "#FCE4EC",
            "#F8BBD0",
            "#F48FB1",
            "#F06292",
            "#EC407A",
            "#E91E63",
            "#D81B60",
            "#C2185B",
            "#AD1457",
            "#880E4F",
            "#FF80AB",
            "#FF4081",
            "#F50057",
            "#C51162",
            "#FCE4EC"
            // Add more colors here
        )

        val custom = binding.appBar


        custom.ivBack.setOnClickListener {
            loadFragment(CategoryListFragment())
        }
        custom.ivDelete.visibility = View.GONE
        custom.ivTitle.setText("Add Category")
        val value = arguments?.getString("EDIT")
        updateMergedIcon()
        if (value != null) {
            custom.ivTitle.setText("Manage Category")
            custom.ivDelete.visibility = View.VISIBLE
            val db = AppDataBase.getInstance(requireContext()).categoriesDao()
            lifecycleScope.launch(Dispatchers.IO) {
                val category = db.getCategoryById(value.toInt())
                withContext(Dispatchers.Main) {
                    if (category?.CategoryColor != null && category?.CategoryImage != null) {
                        val colorInt = Color.parseColor(category?.CategoryColor)
                        binding.selectColor.setBackgroundColor(colorInt)
                        binding.selectIcon.setImageResource(category.CategoryImage.toInt())
                    }
                    binding.addCategorytxt.setText(category?.CategoryName)
                    selectedIcon = category?.CategoryImage?.toInt()
                    selectedColor = category?.CategoryColor
                    updateMergedIcon()



                    custom.ivDelete.setOnClickListener {
                        deleteCategoryView =
                            inflater.inflate(R.layout.custom_delete_dialog, container, false)
                        container?.addView(deleteCategoryView)
                        val deleteBtn =
                            deleteCategoryView?.findViewById<MaterialButton>(R.id.btn_delete)
                        val cancelBtn =
                            deleteCategoryView?.findViewById<MaterialButton>(R.id.btncancel)
                        val hintText =
                            deleteCategoryView?.findViewById<AppCompatTextView>(R.id.tv_delete_title)
                        hintText?.setText("Are you sure you want to delete this Category? If you delete this category it also delete all data regarding to this category")
                        deleteBtn?.setOnClickListener {
                            val db = AppDataBase.getInstance(requireContext()).categoriesDao()
                            lifecycleScope.launch(Dispatchers.IO) {
                                db.deleteCategory(value.toInt())
                                deleteBudget(category?.CategoryName)
                            }
                            container?.removeView(deleteCategoryView)
                            val ldf = CategoryListFragment()
                            val transaction = activity?.supportFragmentManager?.beginTransaction()
                            transaction?.replace(R.id.fragment_container, ldf)
                            transaction?.disallowAddToBackStack()
                            transaction?.commit()
                        }
                        cancelBtn?.setOnClickListener {
                            container?.removeView(deleteCategoryView)
                        }
                    }

                    binding.selectIcon.setOnClickListener {
                        binding.colorItemCard.visibility = VISIBLE
                        binding.iconRecyclerView.visibility = VISIBLE
                        val iconAdapter = IconAdapter(icons, binding.root) { icon ->
                            selectedIcon = icon
                            updateMergedIcon()
                            getSelectedIcon()
                            getSelectedColor()
                        }
                        binding.iconRecyclerView.adapter = iconAdapter
                        binding.iconRecyclerView.layoutManager =
                            GridLayoutManager(requireContext(), 4)

                        binding.iconRecyclerView.addOnScrollListener(object :
                            RecyclerView.OnScrollListener() {
                            override fun onScrollStateChanged(
                                recyclerView: RecyclerView,
                                newState: Int
                            ) {
                                super.onScrollStateChanged(recyclerView, newState)
                                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                                    hideKeyboard()
                                }
                            }
                        })

                    }

                    binding.selectColor.setOnClickListener {
                        binding.colorItemCard.visibility = VISIBLE
                        binding.colorRecyclerView.visibility = VISIBLE

                        val colorAdapter = ColorAdapter(colors, binding.root) { color ->
                            selectedColor = color
                            updateMergedIcon()
                            getSelectedIcon()
                            getSelectedColor()
                        }
                        binding.colorRecyclerView.adapter = colorAdapter
                        binding.colorRecyclerView.layoutManager =
                            GridLayoutManager(requireContext(), 4)

                        binding.colorRecyclerView.addOnScrollListener(object :
                            RecyclerView.OnScrollListener() {
                            override fun onScrollStateChanged(
                                recyclerView: RecyclerView,
                                newState: Int
                            ) {
                                super.onScrollStateChanged(recyclerView, newState)
                                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                                    hideKeyboard()
                                }
                            }
                        })

                    }

                    binding.btnSave.setOnClickListener {
                        if (binding.addCategorytxt.text?.isEmpty() == true) {
                            binding.addCategorytxt.requestFocus()
                            Toast.makeText(
                                requireContext(),
                                "Please write Category name",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            updateCategory(
                                value.toInt(),
                                binding.addCategorytxt.text.toString(),
                                selectedIcon.toString(),
                                selectedColor
                            )
                            updateBudget(
                                category?.CategoryName,
                                binding.addCategorytxt.text.toString(), selectedColor
                            )
                            Toast.makeText(requireContext(), "Category Updated", Toast.LENGTH_SHORT)
                                .show()
                            loadFragment(CategoryListFragment())
                        }
                    }

                }
            }
        }

        binding.selectIcon.setOnClickListener {
            binding.colorItemCard.visibility = VISIBLE
            binding.iconRecyclerView.visibility = VISIBLE
            val iconAdapter = IconAdapter(icons, binding.root) { icon ->
                selectedIcon = icon
                updateMergedIcon()
                getSelectedIcon()
                getSelectedColor()
            }
            binding.iconRecyclerView.adapter = iconAdapter
            binding.iconRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)

            binding.iconRecyclerView.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        hideKeyboard()
                    }
                }
            })

        }

        binding.selectColor.setOnClickListener {
            binding.colorItemCard.visibility = VISIBLE
            binding.colorRecyclerView.visibility = VISIBLE

            val colorAdapter = ColorAdapter(colors, binding.root) { color ->
                selectedColor = color
                updateMergedIcon()
                getSelectedIcon()
                getSelectedColor()
            }
            binding.colorRecyclerView.adapter = colorAdapter
            binding.colorRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)

            binding.colorRecyclerView.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        hideKeyboard()
                    }
                }
            })

        }

        binding.btnSave.setOnClickListener {
            if (binding.addCategorytxt.text?.isEmpty() == true) {
                binding.addCategorytxt.requestFocus()
                Toast.makeText(
                    requireContext(),
                    "Please write Category name",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (selectedIcon == null) {
                Toast.makeText(
                    requireContext(),
                    "Please Select Icon",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (selectedColor == null) {
                Toast.makeText(
                    requireContext(),
                    "Please Select Color",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (updateMergedIcon() == null) {
                Toast.makeText(
                    requireContext(),
                    "Please Select Icon and Color",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val db = AppDataBase.getInstance(requireContext()).categoriesDao()
                val existingCategory = db.getCategoryByName(binding.addCategorytxt.text.toString())

                if (existingCategory == null) {
                    addCategory(
                        binding.addCategorytxt.text.toString(),
                    )
                    Toast.makeText(requireContext(), "Category Added", Toast.LENGTH_SHORT).show()
                    clearText(binding.addCategorytxt)
                } else {
                    Toast.makeText(requireContext(), "Category Already Exists", Toast.LENGTH_SHORT)
                        .show()
                }

            }
        }

        return binding.root
    }

    private fun deleteBudget(categoryName: String?) {
        val db = AppDataBase.getInstance(requireContext()).budgetDao()
        lifecycleScope.launch(Dispatchers.IO) {
            db.deleteBudgetOnName(categoryName!!)
        }
    }

    private fun updateBudget(categoryName: String?, newBudg: String, selectedColor: String?) {
        val db = AppDataBase.getInstance(requireContext()).budgetDao()
        lifecycleScope.launch(Dispatchers.IO) {
            db.updateBudgetOnName(newBudg, categoryName!!, selectedColor!!)
        }
    }

    private fun updateCategory(
        id: Int,
        addcategory: String,
        selectedIcon: String?,
        selectedColor: String?
    ) {
        val db = AppDataBase.getInstance(requireContext()).categoriesDao()
        lifecycleScope.launch(Dispatchers.IO) {
            db.updateCategory1(id, addcategory, selectedIcon, selectedColor)
        }
    }

    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun updateMergedIcon() {
        val imageView = binding.mergedImage
        if (selectedIcon == null && selectedColor == null) {
            binding.selectIcon.setImageResource(R.drawable.ic_home)
            val color = "#FF6200EE"
            val colorInt = Color.parseColor(color!!)
            binding.selectColor.setBackgroundColor(colorInt)
            val hsl = FloatArray(3)
            ColorUtils.colorToHSL(colorInt, hsl)

            hsl[2] += 0.2f // Increase the lightness value by 20%
            if (hsl[2] > 1.0f) {
                hsl[2] = 1.0f // Cap the lightness value at 100%
            }
            val lightColor = ColorUtils.HSLToColor(hsl)
            imageView.setImageResource(R.drawable.ic_home)
            imageView.setColorFilter(colorInt, PorterDuff.Mode.SRC_IN)
            imageView.setBackgroundColor(lightColor)

            selectedIcon = R.drawable.ic_home
            selectedColor = color
        } else if (selectedIcon != null && selectedColor == null) {
            val color = "#FF6200EE"
            val colorInt = Color.parseColor(color!!)
            val hsl = FloatArray(3)
            ColorUtils.colorToHSL(colorInt, hsl)

            hsl[2] += 0.2f // Increase the lightness value by 20%
            if (hsl[2] > 1.0f) {
                hsl[2] = 1.0f // Cap the lightness value at 100%
            }
            val lightColor = ColorUtils.HSLToColor(hsl)

            imageView.setImageResource(selectedIcon!!)
            imageView.setColorFilter(colorInt, PorterDuff.Mode.SRC_IN)
            imageView.setBackgroundColor(lightColor)
            selectedColor = color
            binding.selectColor.setBackgroundColor(colorInt)
        } else if (selectedColor != null && selectedIcon == null) {
            val colorInt = Color.parseColor(selectedColor!!)
            val hsl = FloatArray(3)
            ColorUtils.colorToHSL(colorInt, hsl)

            hsl[2] += 0.2f // Increase the lightness value by 20%
            if (hsl[2] > 1.0f) {
                hsl[2] = 1.0f // Cap the lightness value at 100%
            }
            val lightColor = ColorUtils.HSLToColor(hsl)

            binding.selectIcon.setImageResource(R.drawable.ic_home)
            imageView.setImageResource(R.drawable.ic_home)
            imageView.setColorFilter(colorInt, PorterDuff.Mode.SRC_IN)
            imageView.setBackgroundColor(lightColor)
            selectedIcon = R.drawable.ic_home
        } else if (selectedIcon != null && selectedColor != null) {
            val colorInt = Color.parseColor(selectedColor!!)
            val hsl = FloatArray(3)
            ColorUtils.colorToHSL(colorInt, hsl)

            hsl[2] += 0.2f // Increase the lightness value by 20%
            if (hsl[2] > 1.0f) {
                hsl[2] = 1.0f // Cap the lightness value at 100%
            }
            val lightColor = ColorUtils.HSLToColor(hsl)



            imageView.setImageResource(selectedIcon!!)
            imageView.setColorFilter(colorInt, PorterDuff.Mode.SRC_IN)
            imageView.setBackgroundColor(lightColor)
        }
    }

    private fun getSelectedIcon() {
        if (selectedIcon != null) {
            val iconDrawable = AppCompatResources.getDrawable(requireContext(), selectedIcon!!)
            binding.selectIcon.setImageDrawable(iconDrawable)
        }
    }

    private fun getSelectedColor() {
        if (selectedColor != null) {
            val colorInt = Color.parseColor(selectedColor!!)
            val colorDrawable = ColorDrawable(colorInt)
            binding.selectColor.setImageDrawable(colorDrawable)
        }
    }

    private fun addCategory(addcategory: String) {
        val db = AppDataBase.getInstance(requireContext()).categoriesDao()
        val data = Categories(
            CategoryName = addcategory,
            CategoryColor = "$selectedColor",
            CategoryImage = "$selectedIcon"
        )
        lifecycleScope.launch(Dispatchers.IO) {


            db.inserCategory(data)
//            }
//            db.inserCategory(data)
        }
    }


    private fun clearText(addcategory: EditText) {
        addcategory.setText("")
    }


    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.hideBottomNavigationView()
    }

    override fun onStop() {
        (activity as MainActivity?)!!.showBottomNavigationView()
        super.onStop()
        deleteCategoryView?.visibility = View.GONE

        binding.mainLayoutAddCategory.visibility = View.GONE

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (deleteCategoryView?.isVisible == true) {
                    deleteCategoryView?.visibility = View.GONE
                } else {
                    loadFragment(CategoryListFragment())
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun loadFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

//    else if( category.CategoryImage !=null && category.CategoryColor!=null || selectedIcon == null && selectedColor == null) {
//        Toast.makeText(
//            requireContext(),
//            "Please Select Color and Icon",
//            Toast.LENGTH_SHORT
//        ).show()
//    }

}