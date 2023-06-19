package com.newexpenseinvoicemanager.newbudgetplanner.exbin.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.Categories
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.WeeklyAverage
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.incexpTbl

@Dao
interface incexpTblDao {
    @Insert
    suspend fun inserincexpTbl(incexpTbl : incexpTbl)

    @Delete
    suspend fun deletetincexpTbl(incexpTbl : incexpTbl)

    @Query("SELECT * FROM incexpTbl ORDER BY date DESC")
    fun getAllData():LiveData<List<incexpTbl>>

    @Query("SELECT * FROM incexpTbl WHERE date = :sDate")
    fun getAllDataByCat(sDate:String):LiveData<List<incexpTbl>>

//    @Query("SELECT * FROM incexpTbl ORDER BY date DESC LIMIT 5")
//    fun getAllDataHome():LiveData<List<incexpTbl>>

    @Query("SELECT * FROM incexpTbl ORDER BY date DESC, currentDate DESC LIMIT 5")
    fun getAllDataHome(): LiveData<List<incexpTbl>>

    @Query("SELECT * FROM incexpTbl WHERE dType = 'INCOME'")
    fun getAllIncomeData(): LiveData<List<incexpTbl>>

    @Query("SELECT * FROM incexpTbl WHERE date BETWEEN :startDate AND :endDate")
    fun getAllDataByTwoDate(startDate: String, endDate: String): LiveData<List<incexpTbl>>

    @Query("SELECT * FROM incexpTbl WHERE dType = 'INCOME' AND date BETWEEN :startDate AND :endDate")
    fun getAllIncomeDataByDate(startDate: String, endDate: String): LiveData<List<incexpTbl>>



    @Query("SELECT * FROM incexpTbl WHERE dType = 'EXPENSE' AND date BETWEEN :startDate AND :endDate")
    fun getAllExpenseDataByDate(startDate: String, endDate: String): LiveData<List<incexpTbl>>
    @Query("SELECT * FROM incexpTbl WHERE dType = 'EXPENSE'")
    fun getAllExpenseData(): LiveData<List<incexpTbl>>

    @Query("SELECT SUM(amount) FROM incexpTbl WHERE dType = 'INCOME'")
    fun getTotalIncomeAmount(): LiveData<Double>

    @Query("SELECT SUM(amount) FROM incexpTbl WHERE dType = 'EXPENSE'")
    fun getTotalExpenseAmount(): LiveData<Double>

    @Query("SELECT AVG(amount) AS daily_average FROM incexpTbl WHERE dType = 'EXPENSE'")
    fun getDailyAverageExp(): LiveData<Double>

    @Query("SELECT AVG(amount) AS daily_average FROM incexpTbl WHERE dType = 'INCOME'")
    fun getDailyAverage(): LiveData<Double>

    @Query("SELECT SUM(CASE WHEN dType = 'INCOME' THEN amount ELSE 0 END) - SUM(CASE WHEN dType = 'EXPENSE' THEN amount ELSE 0 END) AS difference FROM incexpTbl")
    fun getIncomeExpenseDifference(): LiveData<Double>

    @Query("DELETE FROM incexpTbl WHERE category = :budgetC")
    fun deleteincomeexpense(budgetC: String)

    @Query("UPDATE incexpTbl SET category = :budgetC WHERE category = :oldBud")
    fun updateIncExpOnName(budgetC: String,oldBud: String)




}