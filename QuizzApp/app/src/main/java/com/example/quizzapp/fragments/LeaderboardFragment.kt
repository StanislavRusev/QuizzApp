package com.example.quizzapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzapp.databinding.FragmentHomeBinding
import com.example.quizzapp.databinding.FragmentLeaderboardBinding
import com.example.quizzapp.model.AuthenticationViewModel
import com.example.quizzapp.recyclerview.UserAdapter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LeaderboardFragment : Fragment() {
    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!
    private val authenticationViewModel: AuthenticationViewModel by sharedViewModel()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.recyclerView

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = UserAdapter(authenticationViewModel.allUsers!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}