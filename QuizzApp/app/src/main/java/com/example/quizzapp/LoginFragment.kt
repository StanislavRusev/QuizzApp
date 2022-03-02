package com.example.quizzapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quizzapp.databinding.FragmentLoginBinding
import com.example.quizzapp.model.AuthenticationViewModel
import com.example.quizzapp.model.Status
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


const val RC_GOOGLE = 1000

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthenticationViewModel by sharedViewModel()
    private lateinit var callbackManager: CallbackManager
    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient

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

        viewModel.status.observe(viewLifecycleOwner
        ) { status ->
            when (status) {
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
        }

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

                    viewModel.getSocialMedia(name!!, id!!)
                }

                val bundle = Bundle()
                bundle.putString("fields", "name, id")

                graphRequest.parameters = bundle
                graphRequest.executeAsync()

                LoginManager.getInstance().logOut()
            }
        })

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.googleButton.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE)

        }

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

        if (requestCode == RC_GOOGLE) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                viewModel.getSocialMedia(account.displayName!!, account.id!!)
                mGoogleSignInClient.signOut()
            } catch (e: ApiException) {
                println("failed")
            }
        }
    }

}