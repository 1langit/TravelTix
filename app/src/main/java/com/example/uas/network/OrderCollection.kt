package com.example.uas.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.uas.model.Order
import com.example.uas.model.Travel
import com.google.firebase.firestore.FirebaseFirestore

class OrderCollection {

    private val orderCollection = FirebaseFirestore.getInstance().collection("orders")
    val orderListLiveData: MutableLiveData<List<Order>> by lazy {
        MutableLiveData<List<Order>>()
    }

    fun addItem(order: Order) {
        orderCollection.add(order).addOnSuccessListener { document ->
            order.id = document.id
            document.set(order).addOnFailureListener {
                Log.d("Travel item", "Error adding item", it)
            }
        }.addOnFailureListener {
            Log.d("Travel item", "Error adding item", it)
        }
    }

    fun getAllItem() {
        orderCollection.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("Order item", "Error listening to changes")
            }
            if (snapshots != null) {
                val orderList = snapshots.toObjects(Order::class.java)
                orderListLiveData.postValue(orderList)
            }
        }
    }

    fun getItemById(id: String, callback: (Order?) -> Unit) {
        var item: Order? = null
        orderCollection.document(id).get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                item = documentSnapshot.toObject(Order::class.java)
                callback(item)
            } else {
                Log.d("Travel item", "Item does not exist")
                callback(null)
            }
        }.addOnFailureListener {
            Log.d("Travel item", "Error get item", it)
            callback(null)
        }
    }
}