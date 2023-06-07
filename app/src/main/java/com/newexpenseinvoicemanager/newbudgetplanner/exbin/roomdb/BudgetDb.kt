package com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "BudgetDb")
data class BudgetDb(
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true) val BudgetDbId: Int = 0,
    val budget : String = "",
    val budgetCat : String = "",
    val catColor : String = "",
    val currentDate : String? = "",
    val month :String = "",
    val dbMonth :String = ""
)
