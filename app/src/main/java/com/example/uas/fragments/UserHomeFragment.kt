package com.example.uas.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas.R
import com.example.uas.activities.AdminInputActivity
import com.example.uas.activities.UserPurchaseActivity
import com.example.uas.databinding.FragmentUserHomeBinding
import com.example.uas.network.TravelCollection
import com.example.uas.utils.DatePicker
import com.example.uas.utils.PrefManager
import com.example.uas.utils.TravelAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UserHomeFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: FragmentUserHomeBinding
    private lateinit var calendar: Calendar
    private lateinit var dateFormatter: SimpleDateFormat
    private lateinit var travelCollection: TravelCollection
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserHomeBinding.inflate(layoutInflater)
        prefManager = PrefManager.getInstance(requireContext())
        travelCollection = TravelCollection()
        travelCollection.getAllItem()
        showAllTravelItem()

        with(binding) {
            calendar = Calendar.getInstance()
            dateFormatter = SimpleDateFormat("E, d MMM", Locale.getDefault())
            edtDate.setText(dateFormatter.format(calendar.time))
            txtUsername.text = prefManager.getUsername()
            txtGreeting.text = if (calendar.get(Calendar.HOUR_OF_DAY) < 12) {
                "Good morning"
            } else if (calendar.get(Calendar.HOUR_OF_DAY) < 18) {
                "Good afternoon"
            } else {
                "Good night"
            }

            edtDate.setOnClickListener {
                val datePicker = DatePicker()
                datePicker.show(childFragmentManager, "datePicker")
            }
        }
        return binding.root
    }

    override fun onDateSet(view: android.widget.DatePicker?, year: Int, month: Int, date: Int) {
        binding.edtDate.setText(dateFormatter.format(calendar.time))
    }

    private fun showAllTravelItem() {
        travelCollection.travelListLiveData.observe(viewLifecycleOwner) { travelList ->
            val travelAdapter = TravelAdapter(travelList) { item ->
                val intent = Intent(requireContext(), UserPurchaseActivity::class.java)
                intent.putExtra("id", item.id)
                Log.d("aa", item.id)
                startActivity(intent)
            }
            binding.rvTravel.apply {
                adapter = travelAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }
}