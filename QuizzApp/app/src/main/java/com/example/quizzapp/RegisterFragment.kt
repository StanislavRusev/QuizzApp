package com.example.quizzapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.quizzapp.databinding.FragmentRegisterBinding
import com.example.quizzapp.model.AuthenticationViewModel
import com.example.quizzapp.model.Status
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthenticationViewModel by sharedViewModel()

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

            viewModel.registerUser(username, password, confPass)
        }

        viewModel.status.observe(viewLifecycleOwner,
            {
                status ->
                when(status) {
                    Status.SUCCESS -> {
                        findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
                        viewModel.setNormalStatus()
                    }

                    Status.ERROR -> {
                        invalidData()
                        viewModel.setNormalStatus()
                    }

                    else -> {
                        // do nothing
                    }
                }
            })
    }

    private fun invalidData() {
        binding.username.text = null
        binding.password.text = null
        binding.confirmPassword.text = null
        Toast.makeText(context, "Invalid Data", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}