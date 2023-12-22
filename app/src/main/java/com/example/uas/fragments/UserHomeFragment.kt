package com.example.uas.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas.R
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
    private lateinit var dateFormatter: SimpleDateFormat
    private lateinit var travelCollection: TravelCollection
    private lateinit var prefManager: PrefManager
    private var origin = " "
    private var destination = " "

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserHomeBinding.inflate(layoutInflater)
        prefManager = PrefManager.getInstance(requireContext())
        travelCollection = TravelCollection()
        travelCollection.getAllItem()
        showAllTravelItem()

        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, listOf("All station") + resources.getStringArray(R.array.stations))

        with(binding) {
            val calendar = Calendar.getInstance()
            dateFormatter = SimpleDateFormat("E, d MMM yyyy", Locale.getDefault())
            edtDate.setText(dateFormatter.format(calendar.time))
            txtUsername.text = prefManager.getUsername()
            txtGreeting.text = if (calendar.get(Calendar.HOUR_OF_DAY) < 12) {
                "Good morning"
            } else if (calendar.get(Calendar.HOUR_OF_DAY) < 18) {
                "Good afternoon"
            } else {
                "Good night"
            }

            spnOrigin.adapter = adapter
            spnOrigin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    origin = if (position == 0) " " else spnOrigin.selectedItem as String
                    showAllTravelItem()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            spnDestination.adapter = adapter
            spnDestination.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    destination = if (position == 0) " " else spnDestination.selectedItem as String
                    showAllTravelItem()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            edtDate.setOnClickListener {
                val datePicker = DatePicker()
                datePicker.show(childFragmentManager, "datePicker")
            }
        }
        return binding.root
    }

    override fun onDateSet(view: android.widget.DatePicker?, year: Int, month: Int, date: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, date)
        binding.edtDate.setText(dateFormatter.format(calendar.time))
    }

    private fun showAllTravelItem() {
        travelCollection.travelListLiveData.observe(viewLifecycleOwner) { travelList ->
            val filteredTravelList = travelList.filter {
                it.stationOrigin.contains(origin) && it.stationDestination.contains(destination)
            }.sortedBy {
                it.timeOrigin
            }

            val travelAdapter = TravelAdapter(filteredTravelList) { item ->
                val intent = Intent(requireContext(), UserPurchaseActivity::class.java)
                intent.putExtra("id", item.id)
                intent.putExtra("date", binding.edtDate.text.toString())
                startActivity(intent)
            }

            with(binding) {
                layoutEmpty.visibility = if (filteredTravelList.isEmpty()) View.VISIBLE else View.GONE
                rvTravel.apply {
                    adapter = travelAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                }
            }
        }
    }
}