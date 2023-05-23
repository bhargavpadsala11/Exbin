package com.newexpenseinvoicemanager.newbudgetplanner.exbin.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.PaymentModes

@Dao
interface paymentModeDao {

    @Insert
    fun insertAll(paymentModes: List<PaymentModes>)

    @Insert
    suspend fun insertPaymentMode(paymentMode : PaymentModes)

    @Delete
    suspend fun deletetPaymentMode(paymentMode : PaymentModes)

//    @Update
//    suspend fun updatePaymentMode(paymentMode : PaymentModes)
//

    @Query("SELECT * FROM PaymentModes")
    fun getAllPaymentMode() : LiveData<List<PaymentModes>>

//    @Query("SELECT * FROM PaymentModes WHERE paymentModeId = :id")
//    fun isModeExists(id :Int) : PaymentModes

//    @Query("UPDATE PaymentModes SET paymentMode = : editModeValue WHERE paymentModeId = :id")
//    fun updatePaymentMode(id :Int, editModeValue:String) : LiveData<List<PaymentModes>>

    @Query("UPDATE PaymentModes SET paymentMode = :newPaymentMode WHERE paymentModeId = :id")
    fun updatePaymentMode(id: Int, newPaymentMode: String)
//
//    @Query("SELECT * PaymentModes WHERE id = :id")
//    fun selectOnePaymentMode(id :Int) : PaymentModes
@Insert(onConflict = OnConflictStrategy.IGNORE)
suspend fun insertDefaultPaymentModes(paymentModes: List<PaymentModes>)

//@Update
//suspend fun updatePaymentMode(paymentMode : PaymentModes)

}