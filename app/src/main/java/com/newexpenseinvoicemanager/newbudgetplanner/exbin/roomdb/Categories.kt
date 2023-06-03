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
                Categories(1, "Study", "#9400D3", "${R.drawable.ic_menu_24}"),
                Categories(2, "Party", "#FF1744", "${R.drawable.ic_save_24}"),
                Categories(3, "Movie", "#00CEd1", "${R.drawable.ic_storefront_24}"),
                Categories(4, "Petrol/Diesel", "#4169E1", "${R.drawable.ic_local_gas_station_24}"),
                Categories(5, "Shopping", "#FF4500", "${R.drawable.ic_shop_production_quantity_limits_24}")
            )
        }
    }
}