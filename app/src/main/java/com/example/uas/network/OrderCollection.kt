package com.example.uas.network

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.uas.model.Order
import com.example.uas.model.Travel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.CountDownLatch

class OrderCollection(private val context: Context) {

    private val travelCollection = FirebaseFirestore.getInstance().collection("travel")
    private val orderCollection = FirebaseFirestore.getInstance().collection("orders")
    val orderListLiveData: MutableLiveData<List<Travel>> by lazy {
        MutableLiveData<List<Travel>>()
    }

    fun addItem(order: Order) {
        orderCollection.add(order).addOnSuccessListener {
            Toast.makeText(context, "Order success", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Order failed", Toast.LENGTH_SHORT).show()
            Log.d("Travel item", "Error adding item", it)
        }
    }

    fun getItemByUserId(id: String) {
        orderCollection.whereEqualTo("userId", id).get().addOnSuccessListener { snapshots ->
            val travelList = mutableListOf<Travel>()
            val latch = CountDownLatch(snapshots?.size() ?: 0)
            if (snapshots != null) {
                val orderList = snapshots.toObjects(Order::class.java)
                for (order in orderList) {
                    travelCollection.document(order.travelId).get().addOnSuccessListener { documentSnapshot ->
                        val travel: Travel? = documentSnapshot.toObject(Travel::class.java)
                        if (travel != null) {
                            travelList.add(travel)
                        }
                        latch.countDown()
                    }
                }
                latch.await()
                orderListLiveData.postValue(travelList)
            }
        }
    }
}