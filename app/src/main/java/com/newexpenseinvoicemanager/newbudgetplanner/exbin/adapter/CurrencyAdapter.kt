package com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.AppDataBase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase.getCurrencyClass
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.databinding.CurrencyItemLayoutBinding
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.Currency
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.PaymentModes

class CurrencyAdapter(
    val context: Context,
    val list: List<Currency>,
    private val onImageClickListener: (Int) -> Unit
): RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {
    private var selectedPosition = -1
    var symb :String? =""
    inner class CurrencyViewHolder(val binding: CurrencyItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
val binding =CurrencyItemLayoutBinding.inflate(LayoutInflater.from(context),parent,false)
        return CurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        symb = list[position].CurrencySymb
        holder.binding.tvCurrency.setText("$symb "+" "+" ${list[position].Currency}")
        holder.binding.rbCurrency.isChecked = position == selectedPosition
        holder.binding.rbCurrency.setOnClickListener {
//            selectedPosition = list[position].currencyId
            selectedPosition = holder.adapterPosition
//            updateCurrencyStatus(list[position].currencyId)
            notifyDataSetChanged()

            onImageClickListener(list[position].currencyId)
        }

    }

    override fun getItemCount(): Int {
       return list.size
    }

    fun updateCurrencyStatus(currencyId: Int) {
        val dao = AppDataBase.getInstance(context).currencyDao()
        dao.updateStatus(currencyId)
        dao.resetOtherCurrencyStatus(currencyId)
    }


}