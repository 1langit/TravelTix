package com.example.uas.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.uas.activities.AdminDashboardActivity
import com.example.uas.activities.AuthActivity
import com.example.uas.activities.UserDashboardActivity
import com.example.uas.databinding.FragmentLoginBinding
import com.example.uas.model.User
import com.example.uas.utils.PrefManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userCollection: CollectionReference
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        userCollection = FirebaseFirestore.getInstance().collection("users")
        prefManager = PrefManager.getInstance(requireContext())

        with(binding) {
            btnLogin.setOnClickListener {
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()

                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(requireContext(), "Please fill in all required info", Toast.LENGTH_SHORT).show()
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                        login()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return binding.root
    }

    private fun login() {
        val userId = firebaseAuth.currentUser?.uid
        val userDocument = userCollection.document(userId!!)
        val authActivity = requireActivity() as AuthActivity

        userDocument.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val user = document.toObject(User::class.java)
                prefManager.saveUsername(user!!.username)
                prefManager.saveEmail(firebaseAuth.currentUser?.email!!)
                prefManager.saveLevel(user.level)
                prefManager.setLoggedIn(true)
                authActivity.checkLoginState()
                Toast.makeText(requireContext(), "Login success", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
        }
    }
}