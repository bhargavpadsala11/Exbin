package com.newexpenseinvoicemanager.newbudgetplanner.exbin.dataBase

import com.google.firebase.database.*
import com.newexpenseinvoicemanager.newbudgetplanner.exbin.roomdb.AdKeys

class FirebaseDataFetcher {

    interface DataListener {
        fun onDataLoaded(data: AdKeys?)
        fun onCancelled(error: DatabaseError)
    }

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef: DatabaseReference = database.getReference("Keys")
    private var dataCallback: ((AdKeys?) -> Unit)? = null
    private val valueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val value = dataSnapshot.getValue(AdKeys::class.java)
            dataCallback?.invoke(value)
        }

        override fun onCancelled(error: DatabaseError) {
            dataCallback?.invoke(null)
        }
    }

    fun fetchData(callback: (AdKeys?) -> Unit) {
        dataCallback = callback
        myRef.addValueEventListener(valueEventListener)
    }

    fun removeDataListener() {
        dataCallback = null
        myRef.removeEventListener(valueEventListener)
    }
}


