package com.arasaka.re_malwack.worker

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arasaka.re_malwack.ReMalwackApplication
import com.arasaka.re_malwack.service.HostsUpdateService
import com.arasaka.re_malwack.utils.Constants

class HostsUpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            val app = applicationContext as ReMalwackApplication
            val rootRepository = app.rootRepository
            

            if (!rootRepository.isRootAvailable()) {
                return Result.failure()
            }
            
            val intent = Intent(applicationContext, HostsUpdateService::class.java).apply {
                action = Constants.ACTION_UPDATE_HOSTS
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                applicationContext.startForegroundService(intent)
            } else {
                applicationContext.startService(intent)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
} 