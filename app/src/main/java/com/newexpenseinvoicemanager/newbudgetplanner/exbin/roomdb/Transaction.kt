package com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb

data class Transaction(
    val id: Int,
    val amount: String?,
    val category: String?,
    val categoryIcon: ByteArray?,
    val date: String?,
    val note: String?,
    val currentDate: String?,
    val type: String // "income" or "expense"
)
