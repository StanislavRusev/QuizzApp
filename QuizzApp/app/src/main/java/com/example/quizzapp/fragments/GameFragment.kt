package com.example.quizzapp.fragments

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.quizzapp.databinding.FragmentGameBinding
import com.example.quizzapp.model.AuthenticationViewModel
import com.example.quizzapp.model.GameViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

const val COUNTDOWN: Long = 20000

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!
    private val authenticationViewModel: AuthenticationViewModel by sharedViewModel()
    private val gameViewModel: GameViewModel by sharedViewModel()
    private lateinit var countDownTimer: CountDownTimer
    private var timeLeftInMillis: Long = COUNTDOWN
    private var canEarnPoints: Boolean = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authenticationViewModel.updateLastPlayed()
        canEarnPoints = authenticationViewModel.canEarnPoints()
        setupQuestion()

        binding.answer1.setOnClickListener {
            chooseAnswer(binding.answer1.text.toString())
        }

        binding.answer2.setOnClickListener {
            chooseAnswer(binding.answer2.text.toString())
        }

        binding.answer3.setOnClickListener {
            chooseAnswer(binding.answer3.text.toString())
        }

        binding.answer4.setOnClickListener {
            chooseAnswer(binding.answer4.text.toString())
        }

        binding.addTime.setOnClickListener {
            bonusTime()
        }

        binding.fifty.setOnClickListener {
            bonusFifty()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        countDownTimer.cancel()
    }

    private fun chooseAnswer(answer: String) {
        if(gameViewModel.isRightAnswer(answer)) {
            Toast.makeText(context, "Correct", Toast.LENGTH_SHORT).show()
            if(canEarnPoints)
                gameViewModel.addPoints()
        } else {
            Toast.makeText(context, "Incorrect", Toast.LENGTH_SHORT).show()
        }
        if(!gameViewModel.toNextQuestion()) {
            Toast.makeText(context, "You got " + gameViewModel.earnedPoints + " points", Toast.LENGTH_SHORT).show()
            authenticationViewModel.updatePoints(gameViewModel.earnedPoints)
            findNavController().navigate(GameFragmentDirections.actionGameFragmentToHomeFragment())
            return
        }
        countDownTimer.cancel()
        setupQuestion()
    }


    private fun setupQuestion() {
        timeLeftInMillis = COUNTDOWN
        startCountDown()
        binding.question.text = gameViewModel.getQuestion()
        val distractors = gameViewModel.getDistractors()
        distractors.shuffle()
        binding.answer1.visibility = View.VISIBLE
        binding.answer2.visibility = View.VISIBLE
        binding.answer3.visibility = View.VISIBLE
        binding.answer4.visibility = View.VISIBLE
        binding.answer1.text = distractors[0]
        binding.answer2.text = distractors[1]
        binding.answer3.text = distractors[2]
        binding.answer4.text = distractors[3]
        binding.fifty.visibility = View.VISIBLE

    }

    private fun startCountDown() {
        countDownTimer = object: CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {
                timeLeftInMillis = 0
                updateTimer()
                if(!gameViewModel.toNextQuestion()) {
                    Toast.makeText(context, "You got " + gameViewModel.earnedPoints + " right", Toast.LENGTH_SHORT).show()
                    authenticationViewModel.updatePoints(gameViewModel.earnedPoints)
                    findNavController().navigate(GameFragmentDirections.actionGameFragmentToHomeFragment())
                    return
                }
                setupQuestion()
            }

        }.start()
    }

    private fun updateTimer() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val time = String.format("%02d:%02d", minutes, seconds)

        binding.timer.text = time
    }

    private fun bonusTime() {
        if(authenticationViewModel.currentUser?.points!! < 1) {
            Toast.makeText(context, "Not enough points", Toast.LENGTH_SHORT).show()
        } else {
            authenticationViewModel.updatePoints(-1)
            countDownTimer.cancel()
            timeLeftInMillis += COUNTDOWN
            startCountDown()
            updateTimer()
        }
    }

    private fun bonusFifty() {
        if(authenticationViewModel.currentUser?.points!! < 2) {
            Toast.makeText(context, "Not enough points", Toast.LENGTH_SHORT).show()
        } else {
            authenticationViewModel.updatePoints(-2)
            val toHide = gameViewModel.getBonusFifty()
            when(toHide[0]) {
                binding.answer1.text -> binding.answer1.visibility = View.INVISIBLE
                binding.answer2.text -> binding.answer2.visibility = View.INVISIBLE
                binding.answer3.text -> binding.answer3.visibility = View.INVISIBLE
                binding.answer4.text -> binding.answer4.visibility = View.INVISIBLE
            }
            when(toHide[1]) {
                binding.answer1.text -> binding.answer1.visibility = View.INVISIBLE
                binding.answer2.text -> binding.answer2.visibility = View.INVISIBLE
                binding.answer3.text -> binding.answer3.visibility = View.INVISIBLE
                binding.answer4.text -> binding.answer4.visibility = View.INVISIBLE
            }
            binding.fifty.visibility = View.INVISIBLE

        }
    }

}