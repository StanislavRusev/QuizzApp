package com.example.quizzapp.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.quizzapp.R
import com.example.quizzapp.databinding.FragmentHomeBinding
import com.example.quizzapp.model.UserViewModel
import com.example.quizzapp.model.GameViewModel
import com.example.quizzapp.model.Status
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by sharedViewModel()
    private val gameViewModel: GameViewModel by sharedViewModel()
    private lateinit var dialog: AlertDialog
    private val runnable = Runnable{ userViewModel.checkStatus() }
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profile.text = userViewModel.currentUser?.name?.get(0).toString()

        binding.profile.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
        }

        binding.playGame.setOnClickListener {
            gameModeDialogBuilder()
        }

        binding.playMultiplayer.setOnClickListener {
            userViewModel.joinMultiplayer()
        }

        binding.leaderboard.setOnClickListener {
            userViewModel.getAllUsers()
        }

        gameViewModel.status.observe(viewLifecycleOwner
        ) { status ->
            when (status) {
                Status.SUCCESS -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToGameFragment())
                    gameViewModel.setNormalStatus()
                }

                Status.NORMAL -> {
                    // everything is clear
                }

                else -> {
                    gameViewModel.setNormalStatus()
                }
            }
        }

        userViewModel.status.observe(viewLifecycleOwner
        ) { status ->
            when (status) {
                Status.RECEIVED_USERS -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLeaderboardFragment())
                    userViewModel.setNormalStatus()
                }

                Status.WAITING -> {
                    waitingDialogBuilder(getString(R.string.multiplayer_waiting_join))
                    makeCall()
                    userViewModel.setNormalStatus()
                }

                Status.PLAYING -> {
                    dialog.dismiss()
                    gameViewModel.setGameMode("easyBiology")
                    gameViewModel.setupQuestions()
                    gameViewModel.gameType = "multiplayer"
                    userViewModel.setNormalStatus()
                }

                Status.ONE_FINISHED -> {
                    waitingDialogBuilder(getString(R.string.multiplayer_waiting_finish))
                    makeCall()
                    userViewModel.setNormalStatus()
                }

                Status.ALL_FINISHED -> {
                    dialog.dismiss()
                    userViewModel.setNormalStatus()
                    userViewModel.getEnemyPoints()
                }

                Status.SHOW_RESULTS -> {
                    resultDialogBuilder()
                    userViewModel.setNormalStatus()
                    userViewModel.removeMultiplayer()
                }

                Status.NORMAL -> {
                    // everything is clear
                }

                else -> {
                    userViewModel.setNormalStatus()
                }
            }
        }

        dialog = AlertDialog.Builder(context).create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacks(runnable)
    }

    private fun gameModeDialogBuilder() {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.pick_gamemode)
        dialog.findViewById<Button>(R.id.easyBiology).setOnClickListener {
            setupQuestions(dialog, "easyBiology")
        }

        dialog.findViewById<Button>(R.id.hardBiology).setOnClickListener {
            setupQuestions(dialog, "hardBiology")
        }
        dialog.show()
    }

    private fun waitingDialogBuilder(title: String) {
        dialog.setTitle(title)
        dialog.setButton(Dialog.BUTTON_NEUTRAL, getString(R.string.cancel)
        ) { _, _ -> userViewModel.removeMultiplayer() }

        dialog.show()
    }

    private fun resultDialogBuilder() {
        val resultDialog = AlertDialog.Builder(context).create()
        when {
            gameViewModel.earnedPoints > userViewModel.enemyPoints -> {
                resultDialog.setTitle(getString(R.string.winner))
            }
            gameViewModel.earnedPoints == userViewModel.enemyPoints -> {
                resultDialog.setTitle(getString(R.string.draw))
            }
            else -> {
                resultDialog.setTitle(getString(R.string.loser))
            }
        }
        resultDialog.setMessage(getString(R.string.multiplayer_result, gameViewModel.earnedPoints, userViewModel.enemyPoints))
        resultDialog.setButton(Dialog.BUTTON_NEUTRAL, "OK") { _: DialogInterface, _: Int -> }
        resultDialog.show()
    }

    private fun setupQuestions(dialog: Dialog, mode: String) {
        gameViewModel.setGameMode(mode)
        gameViewModel.setupQuestions()
        dialog.dismiss()
    }

    private fun makeCall() {
        handler.postDelayed(runnable, 3000)
    }

}