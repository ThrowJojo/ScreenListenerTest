package testing.screenlistener

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log

class ScreenService : Service() {

    val LOG_TAG = ScreenService::class.java.name
    var receiver: BroadcastReceiver? = null

    val CHANNEL_ID = "testing.screenlistener.NEW_CHANNEL"

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannel()
        enableForegroundTask()
        startListening()
        return START_STICKY
    }

    fun enableForegroundTask() {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(this, CHANNEL_ID) else Notification.Builder(this)
        val notification = builder.setContentTitle(getString(R.string.app_name))
                .setContentText("IN FOREGROUND")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()
        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (receiver != null) {
            unregisterReceiver(receiver)
        }
    }

    fun startListening() {
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_SCREEN_ON)
        receiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                    Log.e(LOG_TAG, "SCREEN OFF")
                    launchActivity()
                } else if (intent?.action == Intent.ACTION_SCREEN_ON) {
                    Log.e(LOG_TAG, "SCREEN ON")
                }
            }
        }
        registerReceiver(receiver, filter)
    }

    @TargetApi(26)
    fun createChannel() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val newChannel = NotificationChannel(CHANNEL_ID, "NEW CHANNEL", NotificationManager.IMPORTANCE_DEFAULT)
        newChannel.enableVibration(true)
        newChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        manager.createNotificationChannel(newChannel)
    }

    fun launchActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

}
