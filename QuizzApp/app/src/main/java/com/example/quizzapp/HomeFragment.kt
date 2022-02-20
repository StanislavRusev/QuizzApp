package com.example.quizzapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.quizzapp.databinding.FragmentHomeBinding
import com.example.quizzapp.model.AuthenticationViewModel
import com.example.quizzapp.model.GameViewModel
import com.example.quizzapp.model.Status
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val authenticationViewModel: AuthenticationViewModel by sharedViewModel()
    private val gameViewModel: GameViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profile.text = authenticationViewModel.currentUser?.name?.get(0).toString()

        binding.profile.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
        }

        binding.playGame.setOnClickListener {
            gameViewModel.setupEasyBiologyQuestions()
        }

        binding.leaderboard.setOnClickListener {
            authenticationViewModel.getAllUsers()
        }

        gameViewModel.status.observe(viewLifecycleOwner,
            {
                    status ->
                when(status) {
                    Status.SUCCESS -> {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToGameFragment())
                        gameViewModel.setNormalStatus()
                    }

                    Status.ERROR -> {
                        gameViewModel.setNormalStatus()
                    }

                    else -> {
                        // do nothing
                    }
                }
            })

        authenticationViewModel.status.observe(viewLifecycleOwner,
            {
                    status ->
                when(status) {
                    Status.RECEIVED_USERS -> {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLeaderboardFragment())
                        authenticationViewModel.setNormalStatus()
                    }

                    Status.ERROR -> {
                        gameViewModel.setNormalStatus()
                    }

                    else -> {
                        // do nothing
                    }
                }
            })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}