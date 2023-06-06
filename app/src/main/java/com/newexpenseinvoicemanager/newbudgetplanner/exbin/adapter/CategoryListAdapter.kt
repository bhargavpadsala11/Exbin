package com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter


import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.CategoryItemLayoutBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.Categories
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.PaymentModes

class CategoryListAdapter(
    val context: Context,
    val list: List<Categories>,
    private val onImageClickListener: (Categories, String) -> Unit
) :
    RecyclerView.Adapter<CategoryListAdapter.CategoryListViewHolder>() {

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

        if (list[position].categoryId <= 6) {
        } else {
            holder.binding.catehoryItem.setOnClickListener {
                onImageClickListener(list[position], "EDIT")
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
