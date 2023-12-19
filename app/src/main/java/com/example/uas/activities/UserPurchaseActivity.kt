package com.example.uas.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import com.example.uas.R
import com.example.uas.databinding.ActivityUserPurchaseBinding
import com.example.uas.model.Order
import com.example.uas.network.OrderCollection
import com.example.uas.network.TravelCollection
import com.google.firebase.auth.FirebaseAuth

class UserPurchaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserPurchaseBinding
    private lateinit var travelCollection: TravelCollection
    private lateinit var orderCollection: OrderCollection
    private lateinit var firebaseAuth: FirebaseAuth
    private val channelId = "TEST_NOTIF"
    private val notifId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityUserPurchaseBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)

        travelCollection = TravelCollection()
        orderCollection = OrderCollection(this@UserPurchaseActivity)
        firebaseAuth = FirebaseAuth.getInstance()

        val id = intent.getStringExtra("id")
        Log.d("aa", id ?: "null")
        var price = 0
        var seat = 1

        val notifmanager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val notifIntent = Intent(this, UserDashboardActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notifIntent, flag)



        with(binding) {
//            id?.let {
                travelCollection.getItemById(id!!) { data ->
                    if (data != null) {
                        txtName.text = data.trainName
                        txtOriginTime.text = data.timeOrigin
                        txtDestinationTime.text = data.timeDestination
                        txtStationOrigin.text = data.stationOrigin
                        txtStationDestination.text = data.stationDestination
                        txtClass.text = data.klass
                        txtFacilities.text = data.facilities
                        txtPrice.text = data.price.toString()
                        price = data.price
                    }
                }

                btnOrder.setOnClickListener {
                    orderCollection.addItem(
                        Order(
                            userId = firebaseAuth.currentUser!!.uid,
                            travelId = id,
                            seat = seat
                        )
                    )
                    val builder = NotificationCompat.Builder(this@UserPurchaseActivity, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Checkout Success")
                        .setContentText("$seat ticket for ${txtName.text.toString()} is purchased")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val notifChannel = NotificationChannel(channelId, "Notif", NotificationManager.IMPORTANCE_DEFAULT)
                        with(notifmanager) {
                            createNotificationChannel(notifChannel)
                            notify(notifId, builder.build())
                        }
                    } else {
                        notifmanager.notify(notifId, builder.build())
                    }
                    finish()
                }
//            }


            btnPlus.setOnClickListener {
                if (seat < 9) {
                    seat += 1
                    txtSeat.text = seat.toString()
                    txtPrice.text = (price * seat).toString()
                }
            }

            btnMinus.setOnClickListener {
                if (seat > 1) {
                    seat -= 1
                    txtSeat.text = seat.toString()
                    txtPrice.text = (price * seat).toString()
                }
            }
        }
    }
}