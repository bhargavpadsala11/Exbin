package com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R

@Entity(tableName = "Currency")
data class Currency(
    @PrimaryKey val currencyId: Int = 0,
    val Currency: String? = "",
    val CurrencySymb : String = "",
    val CurrencyStatus: Int? = null,
    val currencyIndex: String? = ""
){
    companion object {
        fun getDefaultCurrency(): List<Currency> {
            return listOf(
                Currency(1, "Indian Rupee", "₹",1,"1"),
                Currency(2, "Rupiah", "Rp",0,"2"),
                Currency(3, "Dollar", "\$",0,"3"),
                Currency(4, "Real", "R\$",0,"4"),
                Currency(5, "Peso", "₱",0,"5"),
                Currency(6, "Pound", "£",0,"6"),
                Currency(7, "Euro", "€",0,"7"),
                Currency(8, "Yen", "¥",0,"8"),
                Currency(9, "Ruble", "₽",0,"9")
            )
        }
    }
}