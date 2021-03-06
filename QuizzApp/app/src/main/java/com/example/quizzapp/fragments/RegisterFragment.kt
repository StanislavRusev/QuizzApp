package com.example.quizzapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.quizzapp.R
import com.example.quizzapp.databinding.FragmentRegisterBinding
import com.example.quizzapp.model.UserViewModel
import com.example.quizzapp.model.Status
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonReg.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            val confPass = binding.confirmPassword.text.toString()

            userViewModel.registerUser(username, password, confPass)
        }

        userViewModel.status.observe(viewLifecycleOwner
        ) { status ->
            when (status) {
                Status.SUCCESS -> {
                    findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
                    userViewModel.setNormalStatus()
                }

                Status.ERROR_USER_EXISTS -> {
                    invalidData(getString(R.string.already_exists))
                    userViewModel.setNormalStatus()
                }

                Status.ERROR_PASSWORD_MATCH -> {
                    invalidData(getString(R.string.not_match_password))
                    userViewModel.setNormalStatus()
                }

                Status.ERROR_NULL_FIELDS -> {
                    invalidData(getString(R.string.not_empty_fields))
                    userViewModel.setNormalStatus()
                }

                Status.ERROR_CANNOT_CONNECT -> {
                    invalidData(getString(R.string.cannot_connect))
                    userViewModel.setNormalStatus()
                }

                Status.NORMAL -> {
                    // everything is clear
                }

                else -> {
                    userViewModel.setNormalStatus()
                }
            }
        }
    }

    private fun invalidData(error: String) {
        binding.password.text = null
        binding.confirmPassword.text = null
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}