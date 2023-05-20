package com.newexpenseinvoicemanager.newbudgetplanner.exbin.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.BudgetAndExpense
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.BudgetDb

@Dao
interface budgetDao {
    @Insert
    suspend fun inserBudget(budgetDb: BudgetDb)

    @Delete
    suspend fun deletetBudget(budgetDb: BudgetDb)


    @Query("SELECT * FROM BudgetDb")
    fun getAllBudget(): LiveData<List<BudgetDb>>

    @Query("SELECT BudgetDb._id AS budgetId, BudgetDb.budget, BudgetDb.budgetCat, BudgetDb.catColor, BudgetDb.currentDate, incexpTbl.Id AS expenseId, BudgetDb.budget, incexpTbl.category, incexpTbl.dType, SUM(incexpTbl.amount) AS amount1, BudgetDb.budget AS amount FROM BudgetDb INNER JOIN incexpTbl ON BudgetDb.budgetCat = incexpTbl.category WHERE incexpTbl.dType = 'EXPENSE' GROUP BY incexpTbl.category")
    suspend fun getBudgetAndExpense(): List<BudgetAndExpense>

}