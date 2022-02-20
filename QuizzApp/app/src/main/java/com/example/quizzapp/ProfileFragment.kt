package com.example.quizzapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.quizzapp.databinding.FragmentProfileBinding
import com.example.quizzapp.model.AuthenticationViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthenticationViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.userTextView.text = getString(R.string.username, viewModel.currentUser?.name)
        binding.points.text = getString(R.string.points, viewModel.currentUser?.points)
        binding.title.text = getString(R.string.title, viewModel.getTitle(viewModel.currentUser!!))

        binding.signOut.setOnClickListener {
            signOutConfirmation()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signOutConfirmation() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Sign out")
        builder.setMessage("Do you want to sign out?")
        builder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            signOut()
        }
        builder.setNegativeButton("No") { _: DialogInterface, _: Int -> }
        builder.show()
    }

    private fun signOut() {
        viewModel.clearCurrentUser()
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToLoginFragment())
    }

}