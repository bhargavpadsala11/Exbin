package com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter


import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.CategoryItemLayoutBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.Categories

class CategoryListAdapter(
    val context: Context,
    val list: List<Categories>,
    INCOME_ACTIVITY: String?,
    private val onImageClickListener: (Categories, String) -> Unit
) :
    RecyclerView.Adapter<CategoryListAdapter.CategoryListViewHolder>() {
    val INCOMEACTIVITY = INCOME_ACTIVITY

    inner class CategoryListViewHolder(val binding: CategoryItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val binding =
            CategoryItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return CategoryListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        holder.binding.titleTextView.text = list[position].CategoryName
        val image = list[position].CategoryImage
        val color = list[position].CategoryColor

        val colorInt = Color.parseColor(color!!)
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(colorInt, hsl)

        hsl[2] += 0.2f // Increase the lightness value by 20%
        if (hsl[2] > 1.0f) {
            hsl[2] = 1.0f // Cap the lightness value at 100%
        }
        val lightColor = ColorUtils.HSLToColor(hsl)

        //Log.d("Image Code", image)
        val imageView = holder.binding.imageView
        imageView.setImageResource(image.toInt())
        imageView.setColorFilter(colorInt, PorterDuff.Mode.SRC_IN)
        imageView.setBackgroundColor(lightColor)
        if (INCOMEACTIVITY != null) {
            holder.binding.catehoryItem.setOnClickListener {
                onImageClickListener(list[position], "EDIT")
            }
        } else {
            if (list[position].categoryId < 6) {
                holder.binding.btneditmode.visibility = View.GONE
            } else {
                holder.binding.btneditmode.visibility = View.VISIBLE
                holder.binding.btneditmode.setOnClickListener {
                    onImageClickListener(list[position], "EDIT")
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
