package com.example.quizzapp.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.quizzapp.R
import com.example.quizzapp.databinding.FragmentProfileBinding
import com.example.quizzapp.model.UserViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.userTextView.text = getString(R.string.username_profile, userViewModel.currentUser?.name)
        binding.points.text = getString(R.string.points_profile, userViewModel.currentUser?.points)
        binding.title.text = getString(R.string.title_profile, userViewModel.getTitle(userViewModel.currentUser!!))

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
        userViewModel.clearCurrentUser()
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToLoginFragment())
    }

}