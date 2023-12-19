package com.example.uas.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas.R
import com.example.uas.activities.UserPurchaseActivity
import com.example.uas.databinding.FragmentUserOrderBinding
import com.example.uas.network.OrderCollection
import com.example.uas.utils.TravelAdapter
import com.google.firebase.auth.FirebaseAuth

class UserOrderFragment : Fragment() {

    private lateinit var binding: FragmentUserOrderBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var orderCollection: OrderCollection

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserOrderBinding.inflate(layoutInflater)
//        firebaseAuth = FirebaseAuth.getInstance()
//        orderCollection = OrderCollection(requireContext())
//        orderCollection.getItemByUserId(firebaseAuth.currentUser!!.uid)
//        showUserOrder()
        return binding.root
    }

    private fun showUserOrder() {
        orderCollection.orderListLiveData.observe(this) { travelList ->
            val travelAdapter = TravelAdapter(travelList) { item ->
                val intent = Intent(requireContext(), UserPurchaseActivity::class.java)
                intent.putExtra("id", item.id)
                startActivity(intent)
            }
            binding.rvOrder.apply {
                adapter = travelAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }
}