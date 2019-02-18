package com.nexters.ticktock.timer

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.widget.Toast
import com.nexters.ticktock.R

class AlarmService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Toast.makeText(this, "일어나세요!", Toast.LENGTH_SHORT).show()
        val timerIntent = Intent(this, PriorTimerActivity::class.java)
        timerIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        startActivity(timerIntent)

        return START_NOT_STICKY
    }
}
