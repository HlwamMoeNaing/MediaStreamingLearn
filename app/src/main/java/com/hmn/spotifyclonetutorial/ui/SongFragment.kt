package com.hmn.spotifyclonetutorial.ui

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
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
import com.hmn.spotifyclonetutorial.util.isPlaying
import com.hmn.spotifyclonetutorial.viewmodel.MainViewModel
import com.hmn.spotifyclonetutorial.viewmodel.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song) {
    @Inject
    lateinit var glide:RequestManager
    private lateinit var mainViewModel: MainViewModel

    private val songViewMoidel:SongViewModel by viewModels()
    private var currentPlayingSong: Song? = null
    private var playBackState:PlaybackStateCompat? = null
    private var shouldUpdateSeekBar = true

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
        ivPlayPauseDetail.setOnClickListener {
            currentPlayingSong?.let {
                mainViewModel.playOrToggleSong(it,true)

            }
        }

        ivSkip.setOnClickListener {
            mainViewModel.skipToNextSong()
        }
        ivSkipPrevious.setOnClickListener {
            mainViewModel.skipToPrevious()
        }

        seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    setCurPlayerTimeToTextView(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                shouldUpdateSeekBar = false
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                seekBar.let {
                    mainViewModel.seekTo(it.progress.toLong())
                    shouldUpdateSeekBar = true
                }
            }

        })





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


        mainViewModel.playbackState.observe(viewLifecycleOwner){
            playBackState = it
            ivPlayPauseDetail.setImageResource(
                if(playBackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
            seekBar.progress = it?.position?.toInt() ?: 0
        }

        songViewMoidel.curPlayerPosition.observe(viewLifecycleOwner){
            if(shouldUpdateSeekBar) {
                seekBar.progress = it.toInt()
                setCurPlayerTimeToTextView(it)
            }
        }
        songViewMoidel.curSongDuration.observe(viewLifecycleOwner){
            seekBar.max = it.toInt()
            val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
            tvSongDuration.text = dateFormat.format(it)
        }
    }

    fun setCurPlayerTimeToTextView(ms:Long){
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        tvCurTime.text = dateFormat.format(ms)
    }

}