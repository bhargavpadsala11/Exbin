package com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Categories")
data class Categories(
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true) val categoryId: Int = 0,
    val CategoryName: String? = "",
    val CategoryColor : String = "",
    val CategoryImage: String = ""

){
    companion object {
        fun getDefaultCategories(): List<Categories> {
            return listOf(
                Categories(1, "Study", "#9400D3", "2131165360"),
                Categories(2, "Party", "#FF1744", "2131165337"),
                Categories(3, "Movie", "#00CEd1", "2131165334"),
                Categories(4, "Petrol/Diesel", "#4169E1", "2131165356"),
                Categories(5, "Shopping", "#FF4500", "2131165373")
            )
        }
    }
}