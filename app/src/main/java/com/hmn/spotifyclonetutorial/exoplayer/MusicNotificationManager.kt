package com.hmn.spotifyclonetutorial.exoplayer

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.hmn.spotifyclonetutorial.R
import com.hmn.spotifyclonetutorial.util.Consts.CHANNEL_DESCRIPTION_RESOURCE_ID
import com.hmn.spotifyclonetutorial.util.Consts.CHANNEL_NAME_RESOURCE_ID
import com.hmn.spotifyclonetutorial.util.Consts.NOTIFICATION_CHANNEL_ID
import com.hmn.spotifyclonetutorial.util.Consts.NOTIFICATION_ID

class MusicNotificationManager(
    private var context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener,
    private val newSongCallback: () -> Unit

) {
    //  val mediaController = MediaControllerCompat(context, sessionToken)

    private val notificationManager: PlayerNotificationManager
//    val dsfsd = PlayerNotificationManager.Builder(
//        context, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID,
//        DescriptionAdapter(mediaController)
//
//    ).apply {
//        setChannelNameResourceId(CHANNEL_NAME_RESOURCE_ID)
//        setChannelDescriptionResourceId(CHANNEL_DESCRIPTION_RESOURCE_ID)
//        setNotificationListener(notificationListener)
//
//    }.build()

//
//
//    fun test(){
//
//
//    }

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)
        notificationManager = PlayerNotificationManager.Builder(
            context, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID, DescriptionAdapter(mediaController)
        ).apply {
            setChannelNameResourceId(R.string.notification_channel_name)
            setChannelDescriptionResourceId(R.string.notification_channel_description)
            setNotificationListener(notificationListener)
        }.build().apply {
            setSmallIcon(R.drawable.ic_image)
            setMediaSessionToken(sessionToken)

        }
    }

    fun showNotification(player: Player){
        notificationManager.setPlayer(player)
    }

    inner class DescriptionAdapter(private val mediaController: MediaControllerCompat) :
        PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player): CharSequence {
            return mediaController.metadata.description.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            return mediaController.sessionActivity
        }

        override fun getCurrentContentText(player: Player): CharSequence? {
            return mediaController.metadata.description.subtitle.toString()
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            Glide.with(context).asBitmap()
                .load(mediaController.metadata.description.iconUri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        callback.onBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                })
            return null
        }

    }
}