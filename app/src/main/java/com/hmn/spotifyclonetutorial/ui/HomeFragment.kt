package com.hmn.spotifyclonetutorial.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hmn.spotifyclonetutorial.R
import com.hmn.spotifyclonetutorial.adapter.SongAdapter
import com.hmn.spotifyclonetutorial.util.Status
import com.hmn.spotifyclonetutorial.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    lateinit var mainViewModel:MainViewModel
    @Inject
    lateinit var songAdapter: SongAdapter
    lateinit var recyclerView:RecyclerView
    lateinit var progressBar:ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        songAdapter.setItemClickListener{
            mainViewModel.playOrToggleSong(it)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.rvAllSongs)
        progressBar = view.findViewById(R.id.allSongsProgressBar)
        setUpRecycleriew()
        subscribeToObserver()

//        lifecycleScope.launch {
//
//                mainViewModel.mediaItem.observe(viewLifecycleOwner){
//                    when(it.status){
//                        Status.SUCCESS->{
//                            progressBar.isVisible = false
//                            it.data?.let {
//                                songAdapter.songs = it
//                            }
//                        }
//                        Status.ERROR->Unit
//                        Status.LOADING->progressBar.isVisible = true
//                    }
//                }
//
//
//        }

    }

    private fun setUpRecycleriew(){
        recyclerView.apply {
            adapter = songAdapter
            layoutManager =LinearLayoutManager(requireContext())
        }
    }
    private fun subscribeToObserver(){

        mainViewModel.mediaItem.observe(viewLifecycleOwner){
            when(it.status){
                Status.SUCCESS->{
                    progressBar.isVisible = false
                    it.data?.let {
                        songAdapter.songs = it
                    }
                }
                Status.ERROR->Unit
                Status.LOADING->progressBar.isVisible = true
            }
        }
    }

}