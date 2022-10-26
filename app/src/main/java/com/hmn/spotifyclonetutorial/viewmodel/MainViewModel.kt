package com.hmn.spotifyclonetutorial.viewmodel

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hmn.spotifyclonetutorial.data.entities.Song
import com.hmn.spotifyclonetutorial.exoplayer.MusicServiceConnection
import com.hmn.spotifyclonetutorial.util.Consts.MEDIA_ROOT_ID
import com.hmn.spotifyclonetutorial.util.Resource
import com.hmn.spotifyclonetutorial.util.isPlayEnabled
import com.hmn.spotifyclonetutorial.util.isPlaying
import com.hmn.spotifyclonetutorial.util.isPrepared
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {
    private val _mediaITem = MutableLiveData<Resource<List<Song>>>()
    val mediaItem: LiveData<Resource<List<Song>>> = _mediaITem

    val isConnected = musicServiceConnection.isConnected
    val networkError = musicServiceConnection.networkError
    val currentPlayingSong = musicServiceConnection.currentPlayingSong
    val playbackState = musicServiceConnection.playbackState

    init {
        _mediaITem.postValue(Resource.loading(null))
        musicServiceConnection.subScribe(MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    super.onChildrenLoaded(parentId, children)
                    val items = children.map {
                        Song(
                            it.mediaId!!,
                            it.description.title.toString(),
                            it.description.subtitle.toString(),
                            it.description.mediaUri.toString(),
                            it.description.iconUri.toString()

                        )
                    }
                    _mediaITem.postValue(Resource.success(items))
                }
            })
    }

    fun playOrToggleSong(mediaItem: Song, toggle: Boolean = false) {
        val isPrepared = playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaItem.mediaId ==
            currentPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)
        ) {
            playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> if (toggle) musicServiceConnection.transportControl.pause()
                    playbackState.isPlayEnabled -> musicServiceConnection.transportControl.play()
                    else -> Unit
                }

            }
        } else {
            musicServiceConnection.transportControl.playFromMediaId(mediaItem.mediaId, null)
        }
    }

    fun skipToNextSong() {
        musicServiceConnection.transportControl.skipToNext()
    }

    fun skipToPrevious() {
        musicServiceConnection.transportControl.skipToPrevious()
    }

    fun seekTo(pos: Long) {
        musicServiceConnection.transportControl.seekTo(pos)
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unSubScribe(MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {

            })
    }
}