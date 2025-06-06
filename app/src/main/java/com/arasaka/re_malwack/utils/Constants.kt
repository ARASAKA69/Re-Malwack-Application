package com.arasaka.re_malwack.utils

object Constants {
    const val NOTIFICATION_CHANNEL_HOSTS_UPDATE = "hosts_update"
    const val NOTIFICATION_CHANNEL_STATUS = "status"
    const val NOTIFICATION_CHANNEL_ERROR = "error"
    
    const val NOTIFICATION_ID_HOSTS_UPDATE = 1001
    const val NOTIFICATION_ID_STATUS = 1002
    const val NOTIFICATION_ID_ERROR = 1003
    
    const val HOSTS_FILE_PATH = "/system/etc/hosts"
    const val TEMP_HOSTS_PATH = "/data/local/tmp/hosts_temp"

    const val CMD_MOUNT_SYSTEM_RO = "mount -o remount,ro /system"
    const val CMD_BACKUP_HOSTS = "cp /system/etc/hosts /system/etc/hosts.backup"
    const val CMD_RESTORE_HOSTS = "cp /system/etc/hosts.backup /system/etc/hosts"

    const val WORK_TAG_AUTO_UPDATE = "auto_update"
    
    const val ACTION_UPDATE_HOSTS = "com.arasaka.re_malwack.UPDATE_HOSTS"

}