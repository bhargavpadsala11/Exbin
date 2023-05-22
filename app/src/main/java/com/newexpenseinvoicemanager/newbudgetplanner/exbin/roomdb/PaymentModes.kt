package com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PaymentModes")
data class PaymentModes(
    @PrimaryKey(autoGenerate = true) val paymentModeId: Int = 0,
    //  @ColumnInfo(name = "paymentMode")
    var paymentMode: String? = ""

) {
    companion object {
        const val CASH = "Cash"
        const val CREDIT_CARD = "Credit Card"
        const val DEBIT_CARD = "Debit Card"
        const val NET_BANKING = "Net Banking"
        const val UPI = "UPI"
        const val GOOGLE_PAY = "Google Pay"
    }
}