package com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Types.NULL

@Entity(tableName = "incexpTbl")
data class incexpTbl(
    @PrimaryKey(autoGenerate = true) val Id: Int = 0,
    val amount: String? = "",
    val category: String? = "",
    val categoryIndex: String? = "",
    val date: String? = "" ,
    val time : String? = "",
    val paymentMode: String? = "",
    val paymentModeIndex: String? = "",
    val note : String? = "",
    val dType : String? = "",
    val currentDate : String? = ""
)
