package com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter


import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.CategoryItemLayoutBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.Categories

class CategoryListAdapter(
    val context: Context,
    val list: List<Categories>
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
        holder.binding.descriptionTextView.text = list[position].CategoryDiscription
        val image = byteArrayToDrawable(list[position].CategoryImage)
        Glide.with(context).load(image).into(holder.binding.imageView)

    }




    override fun getItemCount(): Int {
        return list.size
    }

fun byteArrayToDrawable(byteArray: ByteArray?): Drawable {
    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
    return BitmapDrawable(Resources.getSystem(), bitmap)
}
}
