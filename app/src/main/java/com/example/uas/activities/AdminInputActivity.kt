package com.example.uas.activities

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.uas.R
import com.example.uas.database.TravelDao
import com.example.uas.database.TravelRoomDatabase
import com.example.uas.databinding.ActivityAdminInputBinding
import com.example.uas.model.PendingTravel
import com.example.uas.model.Travel
import com.example.uas.network.TravelCollection
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs

class AdminInputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminInputBinding
    private lateinit var travelCollection: TravelCollection
    private lateinit var travelDao: TravelDao
    private lateinit var executorService: ExecutorService
    lateinit var travel: Travel
    var distancePrice = 0
    var finalPrice = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityAdminInputBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Travel Item"

        travelCollection = TravelCollection()
        executorService = Executors.newSingleThreadExecutor()
        travelDao = TravelRoomDatabase.getDatabase(this)!!.travelDao()!!

        with(binding) {
            val cardFacilities = arrayOf(cardFacility1, cardFacility2, cardFacility3, cardFacility4, cardFacility5, cardFacility6, cardFacility7)
            val facilityState = BooleanArray(cardFacilities.size)
            val facilityName = resources.getStringArray(R.array.facilities)
            val stationName = resources.getStringArray(R.array.stations)
            val stationDistance = resources.getIntArray(R.array.multiplier)
            val className = resources.getStringArray(R.array.classes)
            var facilities = ""
            var origin = 0
            var destination = 0
            var klass = 1.0

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
                        val selectedFacility = data.facilities.split(", ")
                        for ((index, station) in facilityName.withIndex()) {
                            if (selectedFacility.contains(station)) {
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
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

            for ((index, cardfacility) in cardFacilities.withIndex()) {
                cardfacility.setOnClickListener {
                    cardfacility.showNext()
                    facilityState[index] = !facilityState[index]
                    if (facilityState[index]) {
                        facilities += "${facilityName[index]}, "
                        finalPrice += 10000
                    } else {
                        facilities = facilities.replace("${facilityName[index]}, ", "")
                        finalPrice -= 10000
                    }
                    txtPrice.text = String.format("Rp%,d", finalPrice)
                }
            }

            btnSave.setOnClickListener {
                val inpName = edtName.text.toString()
                val inpTimeOrigin = timeOrigin.text.toString()
                val inpTimeDestination = timeDestination.text.toString()
                if (!facilities.isBlank()) {
                    facilities = facilities.substring(0, facilities.length - 2)
                }

                if (inpName.isBlank() || inpTimeOrigin.isBlank() || inpTimeDestination.isBlank()) {
                    Toast.makeText(this@AdminInputActivity, "Please fill in all required info", Toast.LENGTH_SHORT).show()
                } else {
                    if (isOnline(this@AdminInputActivity)) {
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
                    } else {

                    }
                    finish()
                }
            }
        }
    }

    private fun calculatePrice(origin: Int, destination: Int, klass: Double) {
        val distance = abs(origin - destination)
        finalPrice -= distancePrice
        distancePrice = ((200000 * (distance / 10)) * klass).toInt()
        finalPrice += distancePrice
        binding.txtPrice.text = String.format("Rp%,d", finalPrice)
        Toast.makeText(this@AdminInputActivity, "${(200000 * (distance / 10)) * klass}", Toast.LENGTH_SHORT).show()
    }

    private fun isOnline(context: Context) : Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                return true
            }
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

    private fun insert(travel: PendingTravel) {
        executorService.execute {
            travelDao.insert(travel)
        }
    }

    private fun update(travel: PendingTravel) {
        executorService.execute {
            travelDao.update(travel)
        }
    }

    private fun delete(travel: PendingTravel) {
        executorService.execute {
            travelDao.delete(travel)
        }
    }
}