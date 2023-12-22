package com.example.uas.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import com.example.uas.R
import com.example.uas.database.FavoriteDao
import com.example.uas.database.FavoriteRoomDatabase
import com.example.uas.databinding.ActivityUserPurchaseBinding
import com.example.uas.model.Favorite
import com.example.uas.model.Order
import com.example.uas.network.OrderCollection
import com.example.uas.network.TravelCollection
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UserPurchaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserPurchaseBinding
    private lateinit var travelCollection: TravelCollection
    private lateinit var orderCollection: OrderCollection
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var favoriteDao: FavoriteDao
    private lateinit var executorService: ExecutorService
    private lateinit var travelId: String
    private val channelId = "TEST_NOTIF"
    private val notifId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityUserPurchaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.parseColor("#33000000")
        }
        title = "Order Ticket"

        travelCollection = TravelCollection()
        orderCollection = OrderCollection()
        firebaseAuth = FirebaseAuth.getInstance()
        executorService = Executors.newSingleThreadExecutor()
        favoriteDao = FavoriteRoomDatabase.getDatabase(this)!!.favoriteDao()!!

        travelId = intent.getStringExtra("id")!!
        var price = 0
        var seat = 1

        val notifmanager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val notifIntent = Intent(this, UserDashboardActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notifIntent, flag)

        with(binding) {
            txtDate.text = intent.getStringExtra("date")

            travelCollection.getItemById(travelId!!) { data ->
                if (data != null) {
                    txtName.text = data.trainName
                    txtOriginTime.text = data.timeOrigin
                    txtDestinationTime.text = data.timeDestination
                    txtStationOrigin.text = data.stationOrigin
                    txtStationDestination.text = data.stationDestination
                    txtClass.text = data.klass
                    txtFacilities.text = data.facilities.joinToString(", ")
                    txtPrice.text = String.format("Rp%,d", data.price)
                    price = data.price
                }
            }

            btnOrder.setOnClickListener {
                orderCollection.addItem(
                    Order(
                        userId = firebaseAuth.currentUser!!.uid,
                        travelId = travelId,
                        date = txtDate.text.toString(),
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

                Toast.makeText(this@UserPurchaseActivity, "Order success", Toast.LENGTH_SHORT).show()
                finish()
            }

            btnPlus.setOnClickListener {
                if (seat < 9) {
                    seat += 1
                    txtSeat.text = seat.toString()
                    txtPrice.text = String.format("Rp%,d", price * seat)
                }
            }

            btnMinus.setOnClickListener {
                if (seat > 1) {
                    seat -= 1
                    txtSeat.text = seat.toString()
                    txtPrice.text = String.format("Rp%,d", price * seat)
                }
            }

            btnBack.setOnClickListener {
                finish()
            }

            btnFavorite.setOnClickListener {
                insertFavorite(
                    Favorite(
                        userId = firebaseAuth.currentUser!!.uid,
                        travelId = travelId
                    )
                )
                Toast.makeText(this@UserPurchaseActivity, "Added to favorite", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun insertFavorite(favorite: Favorite) {
        executorService.execute {
            favoriteDao.insert(favorite)
        }
    }
}