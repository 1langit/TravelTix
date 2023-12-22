package com.example.uas.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas.activities.UserTicketActivity
import com.example.uas.databinding.FragmentUserOrderBinding
import com.example.uas.network.OrderCollection
import com.example.uas.utils.OrderAdapter
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

class UserOrderFragment : Fragment() {

    private lateinit var binding: FragmentUserOrderBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var orderCollection: OrderCollection

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserOrderBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        orderCollection = OrderCollection()
        orderCollection.getAllItem()
        showUserOrder()
        return binding.root
    }

    private fun showUserOrder() {
        orderCollection.orderListLiveData.observe(viewLifecycleOwner) { orderList ->
            val userOrder = orderList.filter {
                it.userId == firebaseAuth.currentUser!!.uid
            }.sortedBy {
                SimpleDateFormat("E, d MMM yyyy", Locale.getDefault()).parse(it.date)
            }

            val orderAdapter = OrderAdapter(userOrder) { item ->
                val intent = Intent(requireContext(), UserTicketActivity::class.java)
                intent.putExtra("id", item.id)
                startActivity(intent)
            }
            with(binding) {
                layoutEmpty.visibility = if (userOrder.isEmpty()) View.VISIBLE else View.GONE
                rvOrder.apply {
                    adapter = orderAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                }
            }
        }
    }
}