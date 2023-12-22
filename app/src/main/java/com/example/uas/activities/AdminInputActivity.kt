package com.example.uas.activities

import android.app.TimePickerDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.uas.R
import com.example.uas.databinding.ActivityAdminInputBinding
import com.example.uas.model.Travel
import com.example.uas.network.TravelCollection
import kotlin.math.abs

class AdminInputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminInputBinding
    private lateinit var travelCollection: TravelCollection
    private lateinit var travel: Travel
    private var price = 0.0
    private var finalPrice = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityAdminInputBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Travel Item"

        travelCollection = TravelCollection()

//        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listOf("---select a station---") + resources.getStringArray(R.array.stations)) {
//            override fun isEnabled(position: Int): Boolean {
//                return position != 0
//            }
//            override fun getDropDownView(
//                position: Int,
//                convertView: View?,
//                parent: ViewGroup
//            ): View {
//                val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
//                if(position == 0) view.setTextColor(Color.GRAY)
//                return view
//            }
//        }

        with(binding) {
            val cardFacilities = arrayOf(cardFacility1, cardFacility2, cardFacility3, cardFacility4, cardFacility5, cardFacility6, cardFacility7)
            val facilityState = BooleanArray(cardFacilities.size)
            val facilityName = resources.getStringArray(R.array.facilities)
            val stationName = resources.getStringArray(R.array.stations)
            val stationDistance = resources.getIntArray(R.array.distance_multiplier)
            val className = resources.getStringArray(R.array.classes)
            var facilities = arrayListOf<String>()
            var origin = 0
            var destination = 0
            var klass = 1.0
//            spnOrigin.adapter = adapter
//            spnDestination.adapter = adapter

            val id = intent.getStringExtra("id")
            if (!id.isNullOrEmpty()) {
                travelCollection.getItemById(id) { data ->
                    if (data != null) {
                        travel = data
                        edtName.setText(data.trainName)
                        timeOrigin.setText(data.timeOrigin)
                        timeDestination.setText(data.timeDestination)
                        spnOrigin.setSelection(stationName.indexOf(data.stationOrigin))
                        spnDestination.setSelection(stationName.indexOf(data.stationDestination))
                        spnClass.setSelection(className.indexOf(data.klass))
                        facilities = data.facilities
                        for ((index, facility) in facilityName.withIndex()) {
                            if (facilities.contains(facility)) {
                                facilityState[index] = true
                                cardFacilities[index].showNext()
                                finalPrice += 10000
                            }
                        }
                        txtPrice.text = String.format("Rp%,d", finalPrice)
                    }
                }
                btnSave.text = "SAVE ITEM"
            }

            timeOrigin.setOnClickListener {
                TimePickerDialog(this@AdminInputActivity, { _, hourOfDay, minute ->
                    timeOrigin.setText(String.format("%d.%02d", hourOfDay, minute))
                }, 8, 0, true).show()
            }
            timeDestination.setOnClickListener {
                TimePickerDialog(this@AdminInputActivity, { _, hourOfDay, minute ->
                    timeDestination.setText(String.format("%d.%02d", hourOfDay, minute))
                }, 8, 0, true).show()
            }

            spnOrigin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    origin = stationDistance[position]
                    calculatePrice(origin, destination, klass)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

            spnDestination.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    destination = stationDistance[position]
                    calculatePrice(origin, destination, klass)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

            spnClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    klass = when(position) {
                        0 -> 1.0
                        1 -> 1.1
                        2 -> 1.2
                        else -> 1.4
                    }
                    calculatePrice(origin, destination, klass)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            for ((index, cardfacility) in cardFacilities.withIndex()) {
                cardfacility.setOnClickListener {
                    cardfacility.showNext()
                    facilityState[index] = !facilityState[index]
                    if (facilityState[index]) {
                        finalPrice += 10000
                    } else {
                        finalPrice -= 10000
                    }
                    txtPrice.text = String.format("Rp%,d", finalPrice)
                }
            }

            btnSave.setOnClickListener {
                val inpName = edtName.text.toString()
                val inpTimeOrigin = timeOrigin.text.toString()
                val inpTimeDestination = timeDestination.text.toString()
                for ((index, facility) in facilityState.withIndex()) {
                    if (facility) facilities.add(facilityName[index])
                }

                if (inpName.isBlank() || inpTimeOrigin.isBlank() || inpTimeDestination.isBlank()) {
                    Toast.makeText(this@AdminInputActivity, "Please fill in all required info", Toast.LENGTH_SHORT).show()
                } else {
                    if (id.isNullOrEmpty()) {
                        travelCollection.addItem(
                            Travel(
                                trainName = inpName,
                                timeOrigin = inpTimeOrigin,
                                timeDestination = inpTimeDestination,
                                stationOrigin = spnOrigin.selectedItem as String,
                                stationDestination = spnDestination.selectedItem as String,
                                klass = spnClass.selectedItem as String,
                                facilities = facilities,
                                price = finalPrice
                            )
                        )
                        Toast.makeText(
                            this@AdminInputActivity,
                            "Add item success!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        travelCollection.updateItem(
                            Travel(
                                id = id,
                                trainName = inpName,
                                timeOrigin = inpTimeOrigin,
                                timeDestination = inpTimeDestination,
                                stationOrigin = spnOrigin.selectedItem as String,
                                stationDestination = spnDestination.selectedItem as String,
                                klass = spnClass.selectedItem as String,
                                facilities = facilities,
                                price = finalPrice
                            )
                        )
                        Toast.makeText(
                            this@AdminInputActivity,
                            "Update item success!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    finish()
                }
            }
        }
    }

    private fun calculatePrice(origin: Int, destination: Int, klass: Double) {
        val initialPrice = resources.getInteger(R.integer.initial_price)
        val distanceMult = abs(origin.toDouble() - destination.toDouble()) / 10
        finalPrice -= price.toInt()
        price = initialPrice * distanceMult * klass
        finalPrice += price.toInt()
        binding.txtPrice.text = String.format("Rp%,d", finalPrice)
    }

    private fun checkNetwork(context: Context) : Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            return true
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!intent.getStringExtra("id").isNullOrEmpty()) {
            menuInflater.inflate(R.menu.admin_delete_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                travelCollection.deleteItem(travel)
                Toast.makeText(this@AdminInputActivity, "Delete success!", Toast.LENGTH_SHORT).show()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}