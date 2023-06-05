package com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter


import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.getCurrencyClass
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.CategoryItemLayoutBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.TransectionItemLayoutBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.Categories
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.incexpTbl

class TransectionListAdapter(
    val context: Context,
    val list: List<incexpTbl>,
    val categoryMap: Map<String, Categories>,
    val currencyClass: getCurrencyClass
) :
    RecyclerView.Adapter<TransectionListAdapter.TransectionListViewHolder>() {

    inner class TransectionListViewHolder(val binding: TransectionItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransectionListViewHolder {
        val binding =
            TransectionItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return TransectionListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransectionListViewHolder, position: Int) {
        val symb = currencyClass.getCurrencySymbol()
        val item = list[position]
        val category = categoryMap[item.category]

        // Set category icon
        if (category != null && category.CategoryImage != null) {
           // holder.binding.imageView.setImageDrawable(byteArrayToDrawable(category.CategoryImage))
            val image = category.CategoryImage
            val color = category.CategoryColor
            val colorInt = Color.parseColor(color!!)
            val hsl = FloatArray(3)
            ColorUtils.colorToHSL(colorInt, hsl)

            hsl[2] += 0.2f // Increase the lightness value by 20%
            if (hsl[2] > 1.0f) {
                hsl[2] = 1.0f // Cap the lightness value at 100%
            }
            val lightColor = ColorUtils.HSLToColor(hsl)


            val imageView = holder.binding.imageView
            imageView.setImageResource(image.toInt())
            imageView.setColorFilter(colorInt, PorterDuff.Mode.SRC_IN)
            imageView.setBackgroundColor(lightColor)

        }

        // Set amount text and color based on dType
        if (item.dType == "EXPENSE") {
            holder.binding.traAmount.setTextColor(ContextCompat.getColor(context, R.color.transectionRed))
            holder.binding.traAmount.text = "$symb -${item.amount}"
        } else {
            holder.binding.traAmount.setTextColor(ContextCompat.getColor(context, R.color.transectionGreen))
            holder.binding.traAmount.text = "$symb $item.amount"
        }

        holder.binding.traCategory.text = item.category
        holder.binding.traDate.text = item.date
        holder.binding.traNote.text = item.note
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
