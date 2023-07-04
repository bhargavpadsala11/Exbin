@file:Suppress("ClassName")

package com.newexpenseinvoicemanager.newbudgetplanner.exbin.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.Currency

@Dao
interface currencyDao {

    @Insert
    fun insertAll(curency: List<Currency>)

    @Query("SELECT * FROM Currency")
    fun getAllCurrency(): LiveData<List<Currency>>

    @Query("UPDATE Currency SET CurrencyStatus = '0' WHERE currencyId != :currencyId")
    fun resetOtherCurrencyStatus(currencyId: Int)

    @Query("UPDATE Currency SET CurrencyStatus = '1' WHERE currencyId = :currencyId")
    fun updateStatus(currencyId: Int)

    @Query("SELECT * FROM Currency WHERE CurrencyStatus = :id")
    fun getCurrencySym(id: Int = 1): LiveData<List<Currency>>


}