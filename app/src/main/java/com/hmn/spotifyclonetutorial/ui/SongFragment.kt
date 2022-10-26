package com.hmn.spotifyclonetutorial.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.RequestManager
import com.google.android.material.datepicker.MaterialTextInputPicker
import com.google.android.material.textview.MaterialTextView
import com.hmn.spotifyclonetutorial.R
import com.hmn.spotifyclonetutorial.data.entities.Song
import com.hmn.spotifyclonetutorial.exoplayer.toSong
import com.hmn.spotifyclonetutorial.util.Status
import com.hmn.spotifyclonetutorial.viewmodel.MainViewModel
import com.hmn.spotifyclonetutorial.viewmodel.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song) {
    @Inject
    lateinit var glide:RequestManager
    private lateinit var mainViewModel: MainViewModel

    private val songViewMoidel:SongViewModel by viewModels()
    private var currentPlayingSong: Song? = null

    //views
    lateinit var tvSongName:MaterialTextView
    lateinit var ivSongImage:ImageView
    lateinit var tvCurTime:MaterialTextView
    lateinit var seekBar:SeekBar
    lateinit var tvSongDuration:MaterialTextView
    lateinit var ivPlayPauseDetail:ImageView
    lateinit var ivSkipPrevious:ImageView
    lateinit var ivSkip:ImageView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProviders.of(requireActivity())[MainViewModel::class.java]

        tvSongName = view.findViewById(R.id.tvSongName)

        ivSongImage = view.findViewById(R.id.ivSongImage)
        tvCurTime = view.findViewById(R.id.tvCurTime)
        seekBar = view.findViewById(R.id.seekBar)
        tvSongDuration = view.findViewById(R.id.tvSongDuration)
        ivPlayPauseDetail = view.findViewById(R.id.ivPlayPauseDetail)
        ivSkipPrevious = view.findViewById(R.id.ivSkipPrevious)
        ivSkip = view.findViewById(R.id.ivSkip)

        subscribeToObservers()


    }

    private fun updateTitleAndSongImage(song:Song){
        val title = "${song.title} - ${song.subtitle}"
        tvSongName.text = title
        glide.load(song.imageUrl).into(ivSongImage)
    }

    private fun subscribeToObservers(){
        mainViewModel.mediaItem.observe(viewLifecycleOwner){
            it?.let {result->
                when(result.status){
                    Status.SUCCESS->{
                        result.data?.let {songs->
                            if(currentPlayingSong == null && songs.isNotEmpty()){
                                currentPlayingSong = songs[0]
                                updateTitleAndSongImage(songs[0])
                            }


                        }
                    }
                    else->Unit
                }


            }

        }

        mainViewModel.currentPlayingSong.observe(viewLifecycleOwner){
            if(it==null) return@observe
            currentPlayingSong =it.toSong()
            updateTitleAndSongImage(currentPlayingSong!!)
        }
    }

}