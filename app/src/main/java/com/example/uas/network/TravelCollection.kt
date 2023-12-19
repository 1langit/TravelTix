package com.example.uas.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.uas.model.Travel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class TravelCollection {

    private val travelCollection = FirebaseFirestore.getInstance().collection("travel")
    val travelListLiveData: MutableLiveData<List<Travel>> by lazy {
        MutableLiveData<List<Travel>>()
    }

    fun addItem(travel: Travel) {
        travelCollection.add(travel).addOnSuccessListener { document ->
            travel.id = document.id
            document.set(travel).addOnFailureListener {
                Log.d("Travel item", "Error adding item", it)
            }
        }.addOnFailureListener {
            Log.d("Travel item", "Error adding item", it)
        }
    }

    fun updateItem(travel: Travel) {
        travelCollection.document(travel.id).set(travel).addOnFailureListener {
            Log.d("Travel item", "Error updating item", it)
        }
    }

    fun deleteItem(travel: Travel) {
        if (travel.id.isEmpty()) {
            Log.d("Travel item", "Error deleting item")
            return
        }
        travelCollection.document(travel.id).delete().addOnFailureListener {
            Log.d("Travel item", "Error deleting item", it)
        }
    }

    fun getAllItem() {
        travelCollection.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("Travel item", "Error listening to changes")
            }
            if (snapshots != null) {
                val travelList = snapshots.toObjects(Travel::class.java)
                travelListLiveData.postValue(travelList)
            }
        }
    }

    fun getItemById(id: String, callback: (Travel?) -> Unit) {
        var item: Travel? = null
        travelCollection.document(id).get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                item = documentSnapshot.toObject(Travel::class.java)
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