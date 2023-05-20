package com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter


import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.CategoryItemLayoutBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.TransectionItemLayoutBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.Categories
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.incexpTbl

class TransectionListAdapter(
    val context: Context,
    val list: List<incexpTbl>,
    val categoryMap: Map<String, Categories>
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
        val item = list[position]
        val category = categoryMap[item.category]

        // Set category icon
        if (category != null && category.CategoryImage != null) {
            holder.binding.imageView.setImageDrawable(byteArrayToDrawable(category.CategoryImage))
        }

        // Set amount text and color based on dType
        if (item.dType == "EXPENSE") {
            holder.binding.traAmount.setTextColor(ContextCompat.getColor(context, R.color.transectionRed))
            holder.binding.traAmount.text = "-${item.amount}"
        } else {
            holder.binding.traAmount.setTextColor(ContextCompat.getColor(context, R.color.transectionGreen))
            holder.binding.traAmount.text = item.amount
        }

        holder.binding.traCategory.text = item.category
        holder.binding.traDate.text = item.date
        holder.binding.traNote.text = item.note
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun byteArrayToDrawable(byteArray: ByteArray?): Drawable {
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
        return BitmapDrawable(Resources.getSystem(), bitmap)
    }
}
