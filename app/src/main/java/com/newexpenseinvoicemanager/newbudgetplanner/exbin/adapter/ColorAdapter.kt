package com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R

class ColorAdapter(
    private val colors: List<String>,
    private val mainlayout: View,
    private val onColorSelected: (String) -> Unit
) :
    RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorView: View = itemView.findViewById(R.id.colorImageView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.color_item, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.colorView.setBackgroundColor(Color.parseColor(colors[position]))
        holder.itemView.setOnClickListener {
            onColorSelected(colors[position])
            val cardView = mainlayout.findViewById<MaterialCardView>(R.id.colorItemCard)
            cardView.visibility = View.GONE
            val iconRecy = mainlayout.findViewById<RecyclerView>(R.id.colorRecyclerView)
            iconRecy.visibility = View.GONE

        }
    }

    override fun getItemCount(): Int = colors.size
}
