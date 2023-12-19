package com.example.uas.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas.R
import com.example.uas.databinding.ActivityAdminDashboardBinding
import com.example.uas.network.TravelCollection
import com.example.uas.utils.PrefManager
import com.example.uas.utils.TravelAdapter
import com.google.firebase.auth.FirebaseAuth

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var travelCollection: TravelCollection
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Admin Dashboard"

        prefManager = PrefManager.getInstance(this)
        firebaseAuth = FirebaseAuth.getInstance()
        travelCollection = TravelCollection()
        travelCollection.getAllItem()
        showAllTravelItem()

        with(binding) {
            btnAdd.setOnClickListener {
                val intent = Intent(this@AdminDashboardActivity, AdminInputActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showAllTravelItem() {
        travelCollection.travelListLiveData.observe(this) { travelList ->
            val travelAdapter = TravelAdapter(
                travelList,
                { item ->
                    val intent = Intent(this@AdminDashboardActivity, AdminInputActivity::class.java)
                    intent.putExtra("id", item.id)
                    startActivity(intent)
                }
            )
            binding.rvTravel.apply {
                adapter = travelAdapter
                layoutManager = LinearLayoutManager(this@AdminDashboardActivity)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.admin_option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_profile -> {
                true
            }
            R.id.menu_logout -> {
                firebaseAuth.signOut()
                prefManager.clear()
                val intent = Intent(this@AdminDashboardActivity, AuthActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}