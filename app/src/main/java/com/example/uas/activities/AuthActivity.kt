package com.example.uas.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager2.widget.ViewPager2
import com.example.uas.utils.TabAdapter
import com.example.uas.databinding.ActivityAuthBinding
import com.example.uas.utils.PrefManager
import com.google.android.material.tabs.TabLayoutMediator

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var viewPager2: ViewPager2
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager.getInstance(this)
        checkLoginState()

        with(binding) {
            viewPager.adapter = TabAdapter(this@AuthActivity)
            viewPager2 = viewPager
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when(position) {
                    0 -> "Register"
                    1 -> "Login"
                    else -> ""
                }
            }.attach()
        }
    }

    fun navigateToLogin() {
        viewPager2.setCurrentItem(1, true)
    }

    fun checkLoginState() {
        if (prefManager.isLoggedIn()) {
            if (prefManager.getLevel() == "user") {
                startActivity(Intent(this@AuthActivity, UserDashboardActivity::class.java))
            } else {
                startActivity(Intent(this@AuthActivity, AdminDashboardActivity::class.java))
            }
            finish()
        }
    }
}