package com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb

data class BudgetAndExpense(
    val budgetId: Int,
    val budget: String,
    val budgetCat: String,
    val catColor: String,
    val currentDate: String?,
    val expenseId: Int,
    val amount: String?,
    val category: String?,
    val dType: String?,
    val amount1: String?
)
