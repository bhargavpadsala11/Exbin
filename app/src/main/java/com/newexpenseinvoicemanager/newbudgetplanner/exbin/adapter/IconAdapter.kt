package com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R

class IconAdapter(
    private val icons: List<Int>,
    private val mainLayout: View,
    private val onIconSelected: (Int) -> Unit
) :
    RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

    inner class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.icon_item, parent, false)
        return IconViewHolder(view)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.iconImageView.setImageResource(icons[position])
        holder.itemView.setOnClickListener {
//            Log.d("Icon Path","$icons[position]")
            onIconSelected(icons[position])
            val cardView = mainLayout.findViewById<MaterialCardView>(R.id.colorItemCard)
            cardView.visibility = View.GONE
            val iconRecy = mainLayout.findViewById<RecyclerView>(R.id.iconRecyclerView)
            iconRecy.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = icons.size
}
