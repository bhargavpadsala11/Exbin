package com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Categories")
data class Categories(
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true) val categoryId: Int = 0,
    val CategoryName: String? = "",
    val CategoryDiscription: String? = "",
    val CategoryColor : String = "",
    val CategoryImage: ByteArray? = null

)