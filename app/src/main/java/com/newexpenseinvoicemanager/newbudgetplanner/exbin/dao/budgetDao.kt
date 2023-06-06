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

    @Query("SELECT * FROM BudgetDb WHERE _id = :id")
    fun getAllBudgetById(id:Int): LiveData<List<BudgetDb>>

//    @Query("SELECT BudgetDb._id AS budgetId, BudgetDb.budget, BudgetDb.budgetCat, BudgetDb.catColor, BudgetDb.currentDate, incexpTbl.Id AS expenseId, BudgetDb.budget, incexpTbl.category, incexpTbl.dType, SUM(incexpTbl.amount) AS amount1, BudgetDb.budget AS amount FROM BudgetDb INNER JOIN incexpTbl ON BudgetDb.budgetCat = incexpTbl.category WHERE incexpTbl.dType = 'EXPENSE' GROUP BY incexpTbl.category")
//    suspend fun getBudgetAndExpense(): List<BudgetAndExpense>

    //SELECT BudgetDb._id AS budgetId, BudgetDb.budget, BudgetDb.budgetCat, BudgetDb.catColor, BudgetDb.currentDate, incexpTbl.Id AS expenseId, BudgetDb.budget, incexpTbl.category, incexpTbl.dType, SUM(incexpTbl.amount) AS amount1, BudgetDb.budget AS amount FROM BudgetDb LEFT JOIN incexpTbl ON BudgetDb.budgetCat = incexpTbl.category AND incexpTbl.dType = 'EXPENSE' GROUP BY BudgetDb.budgetCat
@Query("SELECT BudgetDb._id AS budgetId, BudgetDb.budget, BudgetDb.budgetCat, BudgetDb.catColor, BudgetDb.currentDate, incexpTbl.Id AS expenseId, BudgetDb.budget, incexpTbl.category, incexpTbl.dType, SUM(incexpTbl.amount) AS amount1, BudgetDb.budget AS amount,Categories.CategoryImage AS catImage \n" +
        "FROM BudgetDb \n" +
        "LEFT JOIN incexpTbl ON BudgetDb.budgetCat = incexpTbl.category AND incexpTbl.dType = 'EXPENSE' \n" +
        "LEFT JOIN Categories ON BudgetDb.budgetCat = Categories.CategoryName\n" +
        "WHERE BudgetDb.budgetCat = Categories.CategoryName\n" +
        "GROUP BY BudgetDb.budgetCat\n")
suspend fun getBudgetAndExpense(): List<BudgetAndExpense>

    @Query("DELETE FROM BudgetDb WHERE _id = :id")
    fun deleteBudget(id: Int)

    @Query("UPDATE BudgetDb SET budget = :budgetAmt WHERE _id = :id")
    fun updateBudget(id: Int, budgetAmt: String)

    @Query("UPDATE BudgetDb SET budgetCat = :budgetC WHERE budgetCat = :oldBud")
    fun updateBudgetOnName(budgetC: String,oldBud: String)

    @Query("DELETE FROM BudgetDb WHERE budgetCat = :budgetC")
    fun deleteBudgetOnName(budgetC: String)
//SELECT BudgetDb._id AS budgetId, BudgetDb.budget, BudgetDb.budgetCat, BudgetDb.catColor, BudgetDb.currentDate, incexpTbl.Id AS expenseId, BudgetDb.budget, categorise.image, incexpTbl.category, incexpTbl.dType, SUM(incexpTbl.amount) AS amount1, BudgetDb.budget AS amount
//FROM BudgetDb
//LEFT JOIN incexpTbl ON BudgetDb.budgetCat = incexpTbl.category AND incexpTbl.dType = 'EXPENSE'
//LEFT JOIN categorise ON BudgetDb.budgetCat = categorise.category
//WHERE BudgetDb.budgetCat = categorise.category
//GROUP BY BudgetDb.budgetCat
}