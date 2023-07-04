package com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.PaymentModeItemLayoutBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.PaymentModes


class PaymentModesAdapter(
    val context: Context,
    val list: List<PaymentModes>,
    private val onImageClickListener: (PaymentModes,String) -> Unit
) :
    RecyclerView.Adapter<PaymentModesAdapter.PaymentModeViewHolder>() {

    inner class PaymentModeViewHolder(val binding: PaymentModeItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentModeViewHolder {
        val binding =
            PaymentModeItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)


        return PaymentModeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentModeViewHolder, position: Int) {
       // notifyDataSetChanged()
        holder.binding.pmntdtextview.text = list[position].paymentMode

        if (list[position].paymentModeId <= 6) {
            holder.binding.btnDelete.visibility = View.GONE
            holder.binding.btneditmode.visibility = View.GONE
        } else {
            holder.binding.btnDelete.visibility = View.VISIBLE
            holder.binding.btneditmode.visibility = View.VISIBLE
        }
        holder.binding.btneditmode.setOnClickListener {
            onImageClickListener(list[position],"EDIT")
        }
        holder.binding.btnDelete.setOnClickListener {
            onImageClickListener(list[position],"DELETE")
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

}
