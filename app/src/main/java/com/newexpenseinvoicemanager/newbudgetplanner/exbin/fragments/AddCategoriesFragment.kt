package com.newexpenseinvoicemanager.newbudgetplanner.exbin.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.MainActivity
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.ColorAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter.IconAdapter
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.FragmentAddCategoriesBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.Categories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


class AddCategoriesFragment : Fragment() {

    private lateinit var binding: FragmentAddCategoriesBinding
    private var selectedIcon: Int? = null
    private var selectedColor: String? = null
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
            R.drawable.ichome,
            R.drawable.ic_account_balance_wallet_24,
            R.drawable.ic_bank_account_balance_24,
            R.drawable.ic_airplane_ticket_24,
            R.drawable.ic_atm_24,
            R.drawable.ic_business_24,
            R.drawable.ic_car_crash_24,
            R.drawable.ic_camera_indoor_24,
            R.drawable.ic_cake_24,
            R.drawable.ic_celebration_24,
            R.drawable.ic_new_add_home_24,
            R.drawable.ic_shop_production_quantity_limits_24,
            R.drawable.ic_storefront_24,
            R.drawable.ic_train_24,
            R.drawable.ic_light_24,
            R.drawable.ic_local_gas_station_24,
            R.drawable.ic_person_add_alt_24,
            R.drawable.ic_savings_24,
            R.drawable.ic_warehouse_24,
            R.drawable.ic_water_damage_24,
            R.drawable.ic_currency_exchange_24,
            R.drawable.more
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
        binding.selectIcon.setOnClickListener {
            binding.colorItemCard.visibility = View.VISIBLE
            binding.iconRecyclerView.visibility = View.VISIBLE
            val iconAdapter = IconAdapter(icons, binding.root) { icon ->
                selectedIcon = icon
                updateMergedIcon()
                getSelectedIcon()
                getSelectedColor()
            }
            binding.iconRecyclerView.adapter = iconAdapter
            binding.iconRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)

            binding.iconRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        hideKeyboard()
                    }
                }
            })

        }




        binding.selectColor.setOnClickListener {
            binding.colorItemCard.visibility = View.VISIBLE
            binding.colorRecyclerView.visibility = View.VISIBLE

            val colorAdapter = ColorAdapter(colors, binding.root) { color ->
                selectedColor = color
                updateMergedIcon()
                getSelectedIcon()
                getSelectedColor()
            }
            binding.colorRecyclerView.adapter = colorAdapter
            binding.colorRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)

            binding.colorRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            }else if(updateMergedIcon() == null){
                Toast.makeText(
                    requireContext(),
                    "Please Select Icon and Color",
                    Toast.LENGTH_SHORT
                ).show()
            }else {
                addCategory(
                    binding.addCategorytxt.text.toString(),
                )
                Toast.makeText(requireContext(), "Category Added", Toast.LENGTH_SHORT).show()
                // clearText(binding.addCategorytxt, binding.addCategoryDesctxt)
            }
        }

        return binding.root
    }

    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun updateMergedIcon() {
        if (selectedIcon != null && selectedColor != null) {
            val iconDrawable = AppCompatResources.getDrawable(requireContext(), selectedIcon!!)
            val colorInt = Color.parseColor(selectedColor!!)
            val colorDrawable = ColorDrawable(colorInt)
            val mergedDrawable = mergeDrawables(iconDrawable!!, colorDrawable)
            binding.mergedImage.setImageDrawable(mergedDrawable)
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

    private fun mergeDrawables(drawable1: Drawable, drawable2: Drawable): Drawable {
        val layerDrawable = LayerDrawable(arrayOf(drawable2, drawable1))
        layerDrawable.setLayerInset(1, 0, 0, 0, 0)
        return layerDrawable
    }

    private fun addCategory(addcategory: String) {
        val db = AppDataBase.getInstance(requireContext()).categoriesDao()
        val data = Categories(
            CategoryName = addcategory,
            CategoryColor = "$selectedColor",
            CategoryImage = drawableToByteArray(binding.mergedImage.drawable)
        )
        lifecycleScope.launch(Dispatchers.IO) {
            db.inserCategory(data)
        }
    }

    private fun drawableToByteArray(drawable: Drawable?): ByteArray? {
        if (drawable == null) {
            return null
        }
        val bitmap: Bitmap = if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else if (drawable is LayerDrawable) {
            val width = drawable.intrinsicWidth
            val height = drawable.intrinsicHeight
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } else {
            throw IllegalArgumentException("Unsupported drawable type")
        }
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }


    private fun clearText(addcategory: EditText, addDes: EditText) {
        addcategory.setText("")
        addDes.setText("")
    }


    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)!!.hideBottomNavigationView()
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity?)!!.showBottomNavigationView()
    }

}