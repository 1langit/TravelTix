package com.example.uas.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.example.uas.R
import com.example.uas.databinding.ActivityUserTicketBinding
import com.example.uas.network.OrderCollection
import com.example.uas.network.TravelCollection

class UserTicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserTicketBinding
    private lateinit var travelCollection: TravelCollection
    private lateinit var orderCollection: OrderCollection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityUserTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = ""

        travelCollection = TravelCollection()
        orderCollection = OrderCollection()
        val id = intent.getStringExtra("id")

        with(binding) {
            orderCollection.getItemById(id!!) { order ->
                if (order != null) {
                    txtId.text = order.id
                    txtSeat.text = order.seat.toString()
                    txtDate.text = order.date

                    travelCollection.getItemById(order.travelId!!) { travel ->
                        if (travel != null) {
                            txtTrainName.text = travel.trainName
                            txtTimeOrigin.text = travel.timeOrigin
                            txtTimeDestintion.text = travel.timeDestination
                            txtStationOrigin.text = travel.stationOrigin
                            txtStationDestination.text = travel.stationDestination
                            txtClass.text = travel.klass
                        }
                    }
                }
            }

            btnBack.setOnClickListener {
                finish()
            }
        }
    }
}