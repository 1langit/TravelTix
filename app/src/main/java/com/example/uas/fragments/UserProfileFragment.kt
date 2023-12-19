package com.example.uas.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.uas.activities.AuthActivity
import com.example.uas.databinding.FragmentUserProfileBinding
import com.example.uas.utils.PrefManager
import com.google.firebase.auth.FirebaseAuth

class UserProfileFragment : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        prefManager = PrefManager.getInstance(requireContext())

        with(binding) {
            txtUsername.text = prefManager.getUsername()
            txtEmail.text = prefManager.getEmail()

            btnLogout.setOnClickListener {
                firebaseAuth.signOut()
                prefManager.clear()
                val intent = Intent(requireContext(), AuthActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }
        return binding.root
    }

}