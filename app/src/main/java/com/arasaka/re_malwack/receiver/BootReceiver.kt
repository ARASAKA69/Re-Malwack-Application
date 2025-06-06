package com.arasaka.re_malwack.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.arasaka.re_malwack.utils.Constants
import com.arasaka.re_malwack.worker.HostsUpdateWorker
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                Log.d("BootReceiver", "Device booted or app updated, scheduling auto-update")
                scheduleAutoUpdate(context)
            }
        }
    }
    
    private fun scheduleAutoUpdate(context: Context) {
        try {
            val updateRequest = PeriodicWorkRequestBuilder<HostsUpdateWorker>(
                24, TimeUnit.HOURS
            )
                .addTag(Constants.WORK_TAG_AUTO_UPDATE)
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                Constants.WORK_TAG_AUTO_UPDATE,
                ExistingPeriodicWorkPolicy.KEEP,
                updateRequest
            )
            
            Log.d("BootReceiver", "Auto-update scheduled successfully")
        } catch (e: Exception) {
            Log.e("BootReceiver", "Failed to schedule auto-update", e)
        }
    }
} 