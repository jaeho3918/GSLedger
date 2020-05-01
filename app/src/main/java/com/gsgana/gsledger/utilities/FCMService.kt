package com.gsgana.gsledger.utilities

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.gsgana.gsledger.R


class FCMService : FirebaseMessagingService() {
    /**
     * 구글 토큰을 얻는 값입니다.
     * 아래 토큰은 앱이 설치된 디바이스에 대한 고유값으로 푸시를 보낼때 사용됩니다.
     */
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.e("Firebase", "FirebaseInstanceIDService : $s")
    }

    /**
     * 메세지를 받았을 경우 그 메세지에 대하여 구현하는 부분입니다.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage != null && remoteMessage.data.size > 0) {
            sendNotification(remoteMessage)
        }
    }

    /**
     * remoteMessage 메세지 안애 getData와 getNotification이 있습니다.
     * 이부분은 차후 테스트 날릴때 설명 드리겠습니다.
     */
    private fun sendNotification(remoteMessage: RemoteMessage) {
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]
        /**
         * 오레오 버전부터는 Notification Channel이 없으면 푸시가 생성되지 않는 현상이 있습니다.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = "채널"
            val channel_nm = "채널명"
            val notichannel =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelMessage = NotificationChannel(
                channel, channel_nm,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channelMessage.description = "채널에 대한 설명."
            channelMessage.enableLights(true)
            channelMessage.enableVibration(true)
            channelMessage.setShowBadge(false)
            channelMessage.vibrationPattern = longArrayOf(100, 200, 100, 200)
            notichannel.createNotificationChannel(channelMessage)
            val notificationBuilder =
                NotificationCompat.Builder(this, channel)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setChannelId(channel)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(9999, notificationBuilder.build())
        } else {
            val notificationBuilder =
                NotificationCompat.Builder(this, "")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(9999, notificationBuilder.build())
        }
    }
}

//class FCMService : FirebaseMessagingService() {
//
//    override fun onNewToken(p0: String) {
//        super.onNewToken(p0)
//    }
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        if (remoteMessage!!.notification != null) {
//            sendNotification(remoteMessage)
//        }
//    }
//
//    private fun sendNotification(remoteMessage: RemoteMessage) {
//
//        val title = remoteMessage.data["title"]
//        val message = remoteMessage.data["message"]
//
//        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notificationBuilder =
//            NotificationCompat.Builder(this, "")
//                .setSmallIcon(R.drawable.ic_launcher)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
//
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.notify(999, notificationBuilder.build())
//
//    }
//}
