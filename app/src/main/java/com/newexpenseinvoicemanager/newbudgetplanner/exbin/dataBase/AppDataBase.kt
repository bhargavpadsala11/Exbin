package com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dao.*
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [PaymentModes::class, Categories::class, incexpTbl::class, BudgetDb::class], version = 12)
abstract class AppDataBase : RoomDatabase() {

    companion object {
        private var database: AppDataBase? = null
        private const val DATABASE_NAME = "EXBIN"

        @Synchronized
        fun getInstance(context: Context): AppDataBase {
            if (database == null) {
                database = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    DATABASE_NAME
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val defaultPaymentModes = listOf(
                                PaymentModes(paymentMode = PaymentModes.CASH),
                                PaymentModes(paymentMode = PaymentModes.CREDIT_CARD),
                                PaymentModes(paymentMode = PaymentModes.DEBIT_CARD),
                                PaymentModes(paymentMode = PaymentModes.NET_BANKING),
                                PaymentModes(paymentMode = PaymentModes.UPI),
                                PaymentModes(paymentMode = PaymentModes.GOOGLE_PAY)
                            )
                            CoroutineScope(Dispatchers.IO).launch {
                                database?.paymentModesDao()?.insertAll(defaultPaymentModes)
                            }
                        }
                    })
                    .build()
            }
            return database!!
        }
    }

    abstract fun paymentModesDao(): paymentModeDao
    abstract fun categoriesDao(): categoriesDao
    abstract fun incexpTblDao(): incexpTblDao
    abstract fun budgetDao(): budgetDao
}
