package com.example.uas.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas.R
import com.example.uas.activities.UserPurchaseActivity
import com.example.uas.database.FavoriteDao
import com.example.uas.database.FavoriteRoomDatabase
import com.example.uas.databinding.FragmentUserFavoriteBinding
import com.example.uas.model.Favorite
import com.example.uas.network.TravelCollection
import com.example.uas.utils.FavoriteAdapter
import com.example.uas.utils.OrderAdapter
import com.example.uas.utils.TravelAdapter
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UserFavoriteFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: FragmentUserFavoriteBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var travelCollection: TravelCollection
    private lateinit var favoriteDao: FavoriteDao
    private lateinit var executorService: ExecutorService
    private lateinit var dateFormatter: SimpleDateFormat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserFavoriteBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        travelCollection = TravelCollection()
        executorService = Executors.newSingleThreadExecutor()
        favoriteDao = FavoriteRoomDatabase.getDatabase(requireContext())!!.favoriteDao()!!
        showFavorites()

        with(binding) {
            val calendar = Calendar.getInstance()
            dateFormatter = SimpleDateFormat("E, d MMM yyyy", Locale.getDefault())
            edtDate.setText(dateFormatter.format(calendar.time))

            edtDate.setOnClickListener {
                val datePicker = com.example.uas.utils.DatePicker()
                datePicker.show(childFragmentManager, "datePicker")
            }
        }

        return binding.root
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, date: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, date)
        binding.edtDate.setText(dateFormatter.format(calendar.time))
    }

    private fun showFavorites() {
        favoriteDao.getUserFavorite(firebaseAuth.currentUser!!.uid).observe(this) { favoriteList ->
            val travelAdapter = FavoriteAdapter(
                favoriteList,
                { favorite ->
                    val intent = Intent(requireContext(), UserPurchaseActivity::class.java)
                    intent.putExtra("id", favorite.travelId)
                    intent.putExtra("date", binding.edtDate.text.toString())
                    startActivity(intent)
                },
                { favorite ->
                    deleteFavorite(favorite)
                    Toast.makeText(requireContext(), "Removed from favorite", Toast.LENGTH_SHORT).show()
                }
            )
            with(binding) {
                layoutEmpty.visibility = if (favoriteList.isEmpty()) View.VISIBLE else View.GONE
                rvFavorite.apply {
                    adapter = travelAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                }
            }
        }
    }

    private fun deleteFavorite(favorite: Favorite) {
        executorService.execute {
            favoriteDao.delete(favorite)
        }
    }
}