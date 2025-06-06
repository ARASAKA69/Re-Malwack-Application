package com.arasaka.re_malwack

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.arasaka.re_malwack.data.repository.RootRepository
import com.arasaka.re_malwack.utils.Constants
import com.topjohnwu.superuser.Shell

class ReMalwackApplication : Application() {
    
    val rootRepository by lazy { RootRepository() }
    
    override fun onCreate() {
        super.onCreate()
        
        initializeLibSU()
        
        createNotificationChannels()
    }
    
    private fun initializeLibSU() {
        Shell.enableVerboseLogging = false
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
        )
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            
            val hostsUpdateChannel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_HOSTS_UPDATE,
                getString(R.string.notification_channel_hosts_update),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_hosts_update_description)
                setShowBadge(false)
            }
            
            val statusChannel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_STATUS,
                getString(R.string.notification_channel_status),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.notification_channel_status_description)
            }
            
            val errorChannel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ERROR,
                getString(R.string.notification_channel_error),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.notification_channel_error_description)
            }
            
            notificationManager.createNotificationChannels(
                listOf(hostsUpdateChannel, statusChannel, errorChannel)
            )
        }
    }
} 