package com.example.quizzapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.quizzapp.databinding.FragmentLoginBinding
import com.example.quizzapp.model.AuthenticationViewModel
import com.example.quizzapp.model.Status
import com.facebook.login.LoginResult
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import android.content.Intent
import android.util.Log
import com.facebook.*
import com.facebook.login.LoginManager
import org.json.JSONObject


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthenticationViewModel by sharedViewModel()
    private lateinit var callbackManager: CallbackManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonReg.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        binding.buttonLog.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()

            viewModel.loginUser(username, password)
        }

        viewModel.status.observe(viewLifecycleOwner,
            {
                status ->
                when(status) {
                    Status.SUCCESS -> {
                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
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

        binding.loginButton.fragment = this
        callbackManager = CallbackManager.Factory.create();
        binding.loginButton.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onCancel() {
                println("canceled")
            }

            override fun onError(error: FacebookException) {
                println("error")
            }

            override fun onSuccess(result: LoginResult) {
                val graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { obj, _ ->
                    val name = obj?.getString("name")
                    val id = obj?.getString("id")

                    viewModel.getFacebook(name!!, id!!)
                }

                val bundle = Bundle()
                bundle.putString("fields", "name, id")

                graphRequest.parameters = bundle
                graphRequest.executeAsync()

                LoginManager.getInstance().logOut()
            }
        })

    }

    private fun invalidData() {
        binding.username.text = null
        binding.password.text = null
        Toast.makeText(context, "Invalid data", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

}