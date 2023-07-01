package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.graphics.drawable.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.MainActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.ColorAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.IconAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.CustomAppBarBinding
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
    private var mInterstitialAd: InterstitialAd? = null
    private var FireBaseGooggleAdsInterId: String = ""
    private var isDataorNot: String = ""
    private var isCategory: Categories? = null
    private lateinit var customTitle: CustomAppBarBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddCategoriesBinding.inflate(layoutInflater)
        val preference =
            requireContext().getSharedPreferences("NativeId", AppCompatActivity.MODE_PRIVATE)
        FireBaseGooggleAdsInterId = preference.getString("inter_id", "")!!
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
            "#73BFFF",
            "#4DA6FF",
            "#FFA07A",
            "#FF7F50",
            "#FFB347",
            "#FF6347",
            "#EF9A9A",
            "#E57373",
            "#F48FB1",
            "#F06292",
            "#5B8ABE",
            "#6FA8DC",
            "#7FB2E5",
            "#8FBCE6",
            "#66CDAA",
            "#B76E79",
            "#C8AD7F",
            "#F48FB1",
            "#F06292"
            // Add more colors here
        )

        val custom = binding.appBar

        customTitle = binding.appBar


        custom.ivBack.setOnClickListener {
            loadFragment(CategoryListFragment())
        }
        custom.ivDelete.visibility = View.GONE
        custom.ivTitle.setText("Add Category")
        val value = arguments?.getString("EDIT")

        updateMergedIcon()
        loadAd()
        if (value != null) {
            isDataorNot = value
            custom.ivTitle.setText("Manage Category")
            custom.ivDelete.visibility = View.VISIBLE
            val db = AppDataBase.getInstance(requireContext()).categoriesDao()
            lifecycleScope.launch(Dispatchers.IO) {
                val category = db.getCategoryById(value.toInt())
                isCategory = category
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
                                deleteincomeexpense(category?.CategoryName)
                            }
                            container?.removeView(deleteCategoryView)
                            val ldf = CategoryListFragment()
                            val transaction =
                                activity?.supportFragmentManager?.beginTransaction()
                            transaction?.replace(R.id.fragment_container, ldf)
                            transaction?.addToBackStack(null)
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
                            if (mInterstitialAd != null) {
                                mInterstitialAd?.show(requireActivity())
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
                                updateincCat(
                                    category?.CategoryName,
                                    binding.addCategorytxt.text.toString()
                                )
                                Toast.makeText(
                                    requireContext(),
                                    "Category Updated",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                loadFragment(CategoryListFragment())
                            }
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
                    if (mInterstitialAd != null) {
                        mInterstitialAd?.show(requireActivity())
                    } else {
                        addCategory(
                            binding.addCategorytxt.text.toString()
                        )
                        Toast.makeText(requireContext(), "Category Added", Toast.LENGTH_SHORT)
                            .show()
                        clearText(binding.addCategorytxt)
                    }
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

    private fun deleteincomeexpense(categoryName: String?) {
        val db = AppDataBase.getInstance(requireContext()).incexpTblDao()
        lifecycleScope.launch(Dispatchers.IO) {
            db.deleteincomeexpense(categoryName!!)
        }
    }

    private fun updateBudget(categoryName: String?, newBudg: String, selectedColor: String?) {
        val db = AppDataBase.getInstance(requireContext()).budgetDao()
        lifecycleScope.launch(Dispatchers.IO) {
            db.updateBudgetOnName(newBudg, categoryName!!, selectedColor!!)
        }
    }

    private fun updateincCat(categoryName: String?, newBudg: String) {
        val db = AppDataBase.getInstance(requireContext()).incexpTblDao()
        lifecycleScope.launch(Dispatchers.IO) {
            db.updateIncExpOnName(newBudg, categoryName!!)
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

    fun loadAd() {
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            requireContext(),
            FireBaseGooggleAdsInterId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(ContentValues.TAG, adError?.toString()!!)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(ContentValues.TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(ContentValues.TAG, "Ad was clicked.")
                            }

                            override fun onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                Log.d(ContentValues.TAG, "Ad dismissed fullscreen content.")

                                mInterstitialAd = null

                                if (customTitle.ivTitle.text.toString() == "Add Category") {
                                    addCategory(binding.addCategorytxt.text.toString())
                                    Toast.makeText(
                                        requireContext(),
                                        "Category Added",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    clearText(binding.addCategorytxt)
                                    loadFragment(CategoryListFragment())
                                } else {
                                    updateCateFunction()
                                }

                            }

                            override fun onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(ContentValues.TAG, "Ad recorded an impression.")
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(ContentValues.TAG, "Ad showed fullscreen content.")
                            }
                        }

                }
            })
    }

    fun updateCateFunction() {
        val value = isDataorNot
        val category = isCategory
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
        updateincCat(
            category?.CategoryName,
            binding.addCategorytxt.text.toString()
        )
        Toast.makeText(
            requireContext(),
            "Category Updated",
            Toast.LENGTH_SHORT
        )
            .show()
        loadFragment(CategoryListFragment())
    }

}