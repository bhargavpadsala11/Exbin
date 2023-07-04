package com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner

class getCurrencyClass(val lifecycleOwner: LifecycleOwner, val context: Context) {
    private var Symb : String? = ""
    private val dao = AppDataBase.getInstance(context).currencyDao()
    fun getCurrencies(callback: (String?) -> Unit) {
        dao.getCurrencySym().observe(lifecycleOwner) { currencies ->
            for (currency in currencies) {
                Symb = currency.CurrencySymb
            }
            callback(Symb)
        }
    }
}

