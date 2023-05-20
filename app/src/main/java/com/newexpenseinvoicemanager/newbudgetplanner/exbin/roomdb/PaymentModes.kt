package com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PaymentModes")
data class PaymentModes(
    @PrimaryKey(autoGenerate = true) val paymentModeId: Int = 0,
    //  @ColumnInfo(name = "paymentMode")
    var paymentMode: String? = ""

)