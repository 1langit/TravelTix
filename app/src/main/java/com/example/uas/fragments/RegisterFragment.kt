package com.example.uas.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.uas.activities.AuthActivity
import com.example.uas.databinding.FragmentRegisterBinding
import com.example.uas.model.User
import com.example.uas.utils.DatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class RegisterFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userCollection: CollectionReference
    private var isDateValid = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        userCollection = FirebaseFirestore.getInstance().collection("users")
        val authActivity = requireActivity() as AuthActivity

        with(binding) {
            edtDate.setOnClickListener {
                val datePicker = DatePicker()
                datePicker.show(childFragmentManager, "datePicker")
//                DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
//                    edtDate.setText("$dayOfMonth/${month + 1}/$year")
//                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show()
            }

            btnRegister.setOnClickListener {
                val username = edtUsername.text.toString()
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()

                if (username.isBlank() || email.isBlank() || password.isBlank()) {
                    Toast.makeText(requireContext(), "Please fill in all required info", Toast.LENGTH_SHORT).show()
                } else if (!isDateValid) {
                    Toast.makeText(requireContext(), "Not meeting the age requirement", Toast.LENGTH_SHORT).show()
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                        val userId = firebaseAuth.currentUser?.uid
                        val userDocument = userCollection.document(userId!!)
                        val user = User(username, "user")
                        userDocument.set(user).addOnSuccessListener {
                            Toast.makeText(requireContext(), "Account created! Please login", Toast.LENGTH_SHORT).show()
                            authActivity.navigateToLogin()
                            resetField()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return binding.root
    }

    override fun onDateSet(view: android.widget.DatePicker?, year: Int, month: Int, date: Int) {
        binding.edtDate.setText("$date/${month + 1}/$year")
        val calendar = Calendar.getInstance()
        isDateValid = calendar.get(Calendar.YEAR) - 15 > year
    }

    private fun resetField() {
        with(binding) {
            edtUsername.setText("")
            edtEmail.setText("")
            edtPassword.setText("")
            edtDate.setText("")
            checkTerms.isChecked = false
        }
    }
}