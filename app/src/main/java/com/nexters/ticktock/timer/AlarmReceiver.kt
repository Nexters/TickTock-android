package com.nexters.ticktock.timer

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat.getSystemService
import com.nexters.ticktock.R
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        val serviceIntent = Intent(context, AlarmService::class.java)
        context.startService(serviceIntent)
        val pIntent : PendingIntent = PendingIntent.getActivity(context, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val b : NotificationCompat.Builder = NotificationCompat.Builder(context)
        val notification : Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        b.setSound(notification)
                .setContentTitle(context.getString(R.string.timer_finished))
                .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                .setContentIntent(pIntent)

        val n : Notification = b.build()
        val mNotificationManager : NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(0, n)

    }
}
