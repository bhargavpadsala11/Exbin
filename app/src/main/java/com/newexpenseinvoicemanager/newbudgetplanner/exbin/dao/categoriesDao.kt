package com.newexpenseinvoicemanager.newbudgetplanner.exbin.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.Categories
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.PaymentModes

@Dao
interface categoriesDao {
    @Insert
    fun insertAll(categories: List<Categories>)
    @Insert
    suspend fun inserCategory(categories: Categories)

    @Delete
    suspend fun deletetCategory(categories: Categories)


    @Query("SELECT * FROM Categories")
    fun getAllCategory(): LiveData<List<Categories>>

    @Query("SELECT * FROM Categories WHERE CategoryName = :name")
    fun getCategoryByName(name: String): Categories?

    @Query("SELECT * FROM Categories WHERE _id = :id")
    fun getCategoryById(id: Int): Categories?

    @Query("DELETE FROM Categories WHERE _id = :id")
    fun deleteCategory(id: Int)

    @Query("UPDATE Categories SET CategoryName = :categoryName,CategoryImage = :udateImage,CategoryColor = :updateColor WHERE _id = :id")
    fun updateCategory(id: Int, categoryName: String,udateImage: ByteArray?,updateColor :String?)

    @Query("UPDATE Categories SET CategoryName = :categoryName WHERE _id = :id")
    fun updateCategory1(id: Int, categoryName: String)

}