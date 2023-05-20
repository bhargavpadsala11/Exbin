package com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.dao.*
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.*

@Database(entities = [PaymentModes::class,Categories::class,incexpTbl::class,BudgetDb::class], version = 9)
abstract class AppDataBase : RoomDatabase() {

    companion object{
        private var database : AppDataBase? = null
        private val DATABASE_NAME = "EXBIN"

        @Synchronized
        fun getInstance(context: Context): AppDataBase {
            if (database == null){
                database = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    DATABASE_NAME
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return database!!
        }
    }

    abstract fun paymentModesDao() : paymentModeDao
    abstract fun categoriesDao() : categoriesDao
    abstract fun incexpTblDao() : incexpTblDao
    abstract fun budgetDao() : budgetDao
}