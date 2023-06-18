package com.newexpenseinvoicemanager.newbudgetplanner.exbin.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R
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
    var selectedPosition = -1
//    private var previouslySelectedPosition = -1
    var symb :String? =""
//    private var selectedCurrencyId: Int? = null

    inner class CurrencyViewHolder(val binding: CurrencyItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
val binding =CurrencyItemLayoutBinding.inflate(LayoutInflater.from(context),parent,false)
        return CurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val currency = list[position]
        holder.binding.tvCurrency.text = "${currency.CurrencySymb} ${currency.Currency}"
        val radioButtonTint = ContextCompat.getColorStateList(context, R.color.main_color)
        holder.binding.rbCurrency.buttonTintList = radioButtonTint
        holder.binding.rbCurrency.isChecked = position == selectedPosition

        holder.binding.rbCurrency.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition

            if (previousPosition != -1) {
                notifyItemChanged(previousPosition)
            }
            notifyItemChanged(selectedPosition)

            onImageClickListener(currency.currencyId)
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