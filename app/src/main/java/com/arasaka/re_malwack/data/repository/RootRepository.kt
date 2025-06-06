package com.arasaka.re_malwack.data.repository

import com.topjohnwu.superuser.Shell
import com.arasaka.re_malwack.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class RootRepository {
    
    suspend fun isRootAvailable(): Boolean = withContext(Dispatchers.IO) {
        Shell.getShell().isRoot
    }
    
    suspend fun requestRootAccess(): Boolean = withContext(Dispatchers.IO) {
        try {
            Shell.getShell().isRoot
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun isMagiskModuleInstalled(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val moduleCheck = Shell.cmd("ls /data/adb/modules/re-malwack/module.prop").exec()
            val serviceCheck = Shell.cmd("ls /data/adb/modules/re-malwack/service.sh").exec()
            Result.success(moduleCheck.isSuccess && serviceCheck.isSuccess)
        } catch (e: Exception) {
            Result.success(false)
        }
    }
    
    suspend fun getMagiskModuleVersion(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = Shell.cmd("grep 'version=' /data/adb/modules/re-malwack/module.prop | cut -d'=' -f2").exec()
            if (result.isSuccess && result.out.isNotEmpty()) {
                Result.success(result.out[0].trim())
            } else {
                Result.failure(Exception("Module not found or version not readable"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun enableMagiskModule(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = Shell.cmd("rm -f /data/adb/modules/re-malwack/disable").exec()
            if (result.isSuccess) {
                Result.success("Magisk module enabled")
            } else {
                Result.failure(Exception("Failed to enable module"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun disableMagiskModule(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = Shell.cmd("touch /data/adb/modules/re-malwack/disable").exec()
            if (result.isSuccess) {
                Result.success("Magisk module disabled")
            } else {
                Result.failure(Exception("Failed to disable module"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun mountSystemRW(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val commands = listOf(
                "magisk --mount",
                "mount -o rw,remount /system",
                "mount -o rw,remount /",
                "nsenter -t 1 -m mount -o rw,remount /system",
                "nsenter -t 1 -m mount -o rw,remount /"
            )
            
            for (cmd in commands) {
                val result = Shell.cmd(cmd).exec()
                if (result.isSuccess) {
                    val testResult = Shell.cmd("touch /system/.rw_test && rm /system/.rw_test").exec()
                    if (testResult.isSuccess) {
                        return@withContext Result.success("System mounted as RW using: $cmd")
                    }
                }
            }
            
            Result.failure(Exception("Failed to mount system as RW with all methods"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun backupHostsFile(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = Shell.cmd(Constants.CMD_BACKUP_HOSTS).exec()
            if (result.isSuccess) {
                Result.success("Hosts file backed up successfully")
            } else {
                Result.failure(Exception("Failed to backup hosts file: ${result.err.joinToString()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun restoreHostsFile(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = Shell.cmd(Constants.CMD_RESTORE_HOSTS).exec()
            if (result.isSuccess) {
                Result.success("Hosts file restored successfully")
            } else {
                Result.failure(Exception("Failed to restore hosts file: ${result.err.joinToString()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateHostsFile(hostsContent: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val moduleInstalled = isMagiskModuleInstalled().getOrNull() ?: false
            
            if (moduleInstalled) {
                return@withContext updateHostsViaMagisk(hostsContent)
            } else {
                return@withContext updateHostsDirectly(hostsContent)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun updateHostsViaMagisk(hostsContent: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val magiskHostsPath = "/data/adb/modules/re-malwack/system/etc/hosts"
            val tempFile = File(Constants.TEMP_HOSTS_PATH)
            tempFile.writeText(hostsContent)
            
            val copyResult = Shell.cmd("cp ${Constants.TEMP_HOSTS_PATH} $magiskHostsPath").exec()
            Shell.cmd("chmod 644 $magiskHostsPath").exec()
            
            tempFile.delete()
            
            if (copyResult.isSuccess) {
                Result.success("Hosts file updated via Magisk module")
            } else {
                Result.failure(Exception("Failed to update hosts via Magisk"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun updateHostsDirectly(hostsContent: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val mountResult = mountSystemRW()
            if (mountResult.isFailure) {
                return@withContext mountResult
            }
            
            val tempFile = File(Constants.TEMP_HOSTS_PATH)
            tempFile.writeText(hostsContent)
            
            val copyResult = Shell.cmd("cp ${Constants.TEMP_HOSTS_PATH} ${Constants.HOSTS_FILE_PATH}").exec()
            
            Shell.cmd("chmod 644 ${Constants.HOSTS_FILE_PATH}").exec()
            Shell.cmd("chown root:root ${Constants.HOSTS_FILE_PATH}").exec()
            
            Shell.cmd(Constants.CMD_MOUNT_SYSTEM_RO).exec()
            
            tempFile.delete()
            
            if (copyResult.isSuccess) {
                Result.success("Hosts file updated directly")
            } else {
                Result.failure(Exception("Failed to update hosts file: ${copyResult.err.joinToString()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCurrentHostsContent(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = Shell.cmd("cat ${Constants.HOSTS_FILE_PATH}").exec()
            if (result.isSuccess) {
                Result.success(result.out.joinToString("\n"))
            } else {
                Result.failure(Exception("Failed to read hosts file: ${result.err.joinToString()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getHostsFileSize(): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val result = Shell.cmd("wc -l ${Constants.HOSTS_FILE_PATH}").exec()
            if (result.isSuccess && result.out.isNotEmpty()) {
                val lines = result.out[0].split(" ")[0].toLongOrNull() ?: 0L
                Result.success(lines)
            } else {
                Result.failure(Exception("Failed to get hosts file size"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun flushDnsCache(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val commands = listOf(
                "ndc resolver flushdefaultif",
                "ndc resolver flushif wlan0",
                "ndc resolver flushif rmnet0"
            )
            
            commands.forEach { cmd ->
                Shell.cmd(cmd).exec()
            }
            
            Result.success("DNS cache flushed successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun checkMagiskModule(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val result = Shell.cmd("ls /data/adb/modules/re-malwack").exec()
            Result.success(result.isSuccess)
        } catch (e: Exception) {
            Result.success(false)
        }
    }
    
    suspend fun executeReMalwackScript(action: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val scriptPath = "/data/adb/modules/re-malwack/rmlwck.sh"
            val result = Shell.cmd("sh $scriptPath $action").exec()
            
            if (result.isSuccess) {
                Result.success(result.out.joinToString("\n"))
            } else {
                Result.failure(Exception("Script execution failed: ${result.err.joinToString()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun executeCommand(command: String): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val result = Shell.cmd(command).exec()
            if (result.isSuccess) {
                Result.success(result.out)
            } else {
                Result.failure(Exception("Command failed: ${result.err.joinToString()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 