package com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.R

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
                Categories(1, "Food", "#FF7F50", "${R.drawable.fastfood}"),
                Categories(2, "Shopping", "#73BFFF", "${R.drawable.grocerystore}"),
                Categories(3, "Petrol/Diesel", "#C8AD7F", "${R.drawable.gasolinepump}"),
                Categories(4, "Movie", "#66CDAA", "${R.drawable.movies}"),
                Categories(5, "Study", "#F48FB1", "${R.drawable.mortarboard}")
            )
        }
    }
}