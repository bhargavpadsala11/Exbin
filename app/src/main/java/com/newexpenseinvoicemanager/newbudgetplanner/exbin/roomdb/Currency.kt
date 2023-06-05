package com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R

@Entity(tableName = "Currency")
data class Currency(
    @PrimaryKey val currencyId: Int = 0,
    val Currency: String? = "",
    val CurrencySymb : String = "",
    val CurrencyStatus: Int? = null
){
    companion object {
        fun getDefaultCurrency(): List<Currency> {
            return listOf(
                Currency(1, "Indian Rupee", "₹",0),
                Currency(2, "Rupiah", "Rp",0),
                Currency(3, "Dollar", "\$",0),
                Currency(4, "Real", "R\$",0),
                Currency(5, "Peso", "₱",0),
                Currency(6, "Pound", "£",0),
                Currency(7, "Euro", "€",0),
                Currency(8, "Yen", "¥",0),
                Currency(9, "Ruble", "₽",0)
            )
        }
    }
}