package com.nexters.ticktock.timer

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.support.v4.app.NotificationCompat
import com.nexters.ticktock.R

class TimerExpiredReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val i : Intent = Intent(context, TimerActivity::class.java)
        i.putExtra("isAlarmed", true)
        intent!!.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pIntent : PendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
        //val pIntent : PendingIntent = PendingIntent.getBroadcast(context!!.applicationContext, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)

        val b : NotificationCompat.Builder = NotificationCompat.Builder(context)
        val notification : Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        b.setSound(notification)
                .setContentTitle(context!!.getString(R.string.timer_finished))
                .setSmallIcon(android.R.drawable.ic_notification_clear_all)
                .setContentIntent(pIntent)

        val n : Notification = b.build()
        val mNotificationManager : NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(0, n)
    }
}