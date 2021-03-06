package com.example.quizzapp.fragments

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.quizzapp.R
import com.example.quizzapp.databinding.FragmentGameBinding
import com.example.quizzapp.model.UserViewModel
import com.example.quizzapp.model.GameViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

const val COUNTDOWN: Long = 20000

class GameFragment : Fragment() {
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by sharedViewModel()
    private val gameViewModel: GameViewModel by sharedViewModel()
    private lateinit var countDownTimer: CountDownTimer
    private var timeLeftInMillis: Long = COUNTDOWN
    private var canEarnPoints: Boolean = true
    private val setupQuestionRunnable = Runnable { setupQuestion() }
    private val finishGameRunnable = Runnable { lastQuestionAnswered() }
    private val handler = Handler(Looper.getMainLooper())
    private var canAnswer = true
    private var shouldChange = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if(shouldChange) {
            shouldChange = false
            findNavController().navigate(GameFragmentDirections.actionGameFragmentToHomeFragment())
        }
    }

    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel.updateLastPlayed()
        canEarnPoints = userViewModel.canEarnPoints()
        binding.currentPoints.text = getString(R.string.points_profile, userViewModel.currentUser?.points)
        setupQuestion()
        if(canEarnPoints) {
            binding.pointsHint.visibility = View.INVISIBLE
        } else {
            binding.pointsHint.visibility = View.VISIBLE
        }

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
            bonusFiftyFifty()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        countDownTimer.cancel()
        if(gameViewModel.gameType == "multiplayer") {
            gameViewModel.gameType = "singleplayer"
            userViewModel.finishMultiplayer(0)
        }
        handler.removeCallbacksAndMessages(null)
    }

    private fun chooseAnswer(answer: String) {
        if(!canAnswer) {
            return
        }
        canAnswer = false

        if(gameViewModel.isRightAnswer(answer)) {
            colorAnswers(answer, Color.GREEN)
            gameViewModel.addPoints()
        } else {
            colorAnswers(answer, Color.RED)
            colorAnswers(gameViewModel.getRightAnswer(), Color.GREEN)
        }

        countDownTimer.cancel()

        if(!gameViewModel.toNextQuestion()) {
            handler.postDelayed(finishGameRunnable, 2000)
            return
        }

        handler.postDelayed(setupQuestionRunnable, 2000)
    }

    private fun lastQuestionAnswered() {
        finishGame()
        findNavController().navigate(GameFragmentDirections.actionGameFragmentToHomeFragment())
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
        binding.answer1.setBackgroundColor(Color.BLUE)
        binding.answer2.setBackgroundColor(Color.BLUE)
        binding.answer3.setBackgroundColor(Color.BLUE)
        binding.answer4.setBackgroundColor(Color.BLUE)
        binding.fifty.visibility = View.VISIBLE
        canAnswer = true
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
                    finishGame()
                    findNavController().navigate(GameFragmentDirections.actionGameFragmentToHomeFragment())
                    shouldChange = true
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
        if(!canAnswer) {
            return
        }
        if(userViewModel.currentUser?.points!! < 1) {
            Toast.makeText(context, getString(R.string.not_enough_points), Toast.LENGTH_SHORT).show()
        } else {
            binding.currentPoints.text = getString(R.string.points_profile, userViewModel.currentUser?.points!! - 1)
            userViewModel.updatePoints(-1)
            countDownTimer.cancel()
            timeLeftInMillis += COUNTDOWN
            startCountDown()
            updateTimer()
        }
    }

    private fun bonusFiftyFifty() {
        if(!canAnswer) {
            return
        }
        if(userViewModel.currentUser?.points!! < 2) {
            Toast.makeText(context, getString(R.string.not_enough_points), Toast.LENGTH_SHORT).show()
        } else {
            binding.currentPoints.text = getString(R.string.points_profile, userViewModel.currentUser?.points!! - 2)
            userViewModel.updatePoints(-2)
            val questionsToHide = gameViewModel.getBonusFiftyFifty()
            hideWrongAnswer(questionsToHide[0])
            hideWrongAnswer(questionsToHide[1])
            binding.fifty.visibility = View.INVISIBLE

        }
    }

    private fun hideWrongAnswer(toHide: String) {
        when(toHide) {
            binding.answer1.text -> binding.answer1.visibility = View.INVISIBLE
            binding.answer2.text -> binding.answer2.visibility = View.INVISIBLE
            binding.answer3.text -> binding.answer3.visibility = View.INVISIBLE
            binding.answer4.text -> binding.answer4.visibility = View.INVISIBLE
        }
    }

    private fun finishGame() {
        Toast.makeText(context, getString(R.string.earned_points, gameViewModel.earnedPoints), Toast.LENGTH_SHORT).show()
        if(canEarnPoints) {
            userViewModel.updatePoints(gameViewModel.earnedPoints)
        }
        if(gameViewModel.gameType == "multiplayer") {
            gameViewModel.gameType = "singleplayer"
            userViewModel.finishMultiplayer(gameViewModel.earnedPoints)
        }
    }

    private fun colorAnswers(answer: String, color: Int) {
        when(answer) {
            binding.answer1.text -> binding.answer1.setBackgroundColor(color)
            binding.answer2.text -> binding.answer2.setBackgroundColor(color)
            binding.answer3.text -> binding.answer3.setBackgroundColor(color)
            binding.answer4.text -> binding.answer4.setBackgroundColor(color)
        }
    }

}