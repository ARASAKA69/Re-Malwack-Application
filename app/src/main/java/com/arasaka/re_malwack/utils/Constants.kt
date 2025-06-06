package com.arasaka.re_malwack.utils

object Constants {
    const val NOTIFICATION_CHANNEL_HOSTS_UPDATE = "hosts_update"
    const val NOTIFICATION_CHANNEL_STATUS = "status"
    const val NOTIFICATION_CHANNEL_ERROR = "error"
    
    const val NOTIFICATION_ID_HOSTS_UPDATE = 1001
    const val NOTIFICATION_ID_STATUS = 1002
    const val NOTIFICATION_ID_ERROR = 1003
    
    const val HOSTS_FILE_PATH = "/system/etc/hosts"
    const val HOSTS_BACKUP_PATH = "/system/etc/hosts.backup"
    const val TEMP_HOSTS_PATH = "/data/local/tmp/hosts_temp"
    
    val DEFAULT_HOSTS_SOURCES = listOf(
        "https://raw.githubusercontent.com/StevenBlack/hosts/master/hosts",
        "https://cdn.jsdelivr.net/gh/hagezi/dns-blocklists@release/hosts/pro.plus.txt",
        "https://raw.githubusercontent.com/badmojr/1Hosts/master/Pro/hosts.txt",
        "https://raw.githubusercontent.com/AdguardTeam/AdguardFilters/master/MobileFilter/sections/adservers.txt"
    )
    
    const val CMD_MOUNT_SYSTEM_RW = "mount -o remount,rw /system"
    const val CMD_MOUNT_SYSTEM_RO = "mount -o remount,ro /system"
    const val CMD_BACKUP_HOSTS = "cp /system/etc/hosts /system/etc/hosts.backup"
    const val CMD_RESTORE_HOSTS = "cp /system/etc/hosts.backup /system/etc/hosts"

    const val PREF_AUTO_UPDATE = "auto_update"
    const val PREF_UPDATE_INTERVAL = "update_interval"
    const val PREF_BLOCK_PORN = "block_porn"
    const val PREF_BLOCK_GAMBLING = "block_gambling"
    const val PREF_BLOCK_FAKE_NEWS = "block_fake_news"
    const val PREF_BLOCK_SOCIAL = "block_social"
    const val PREF_CUSTOM_SOURCES = "custom_sources"
    const val PREF_WHITELIST = "whitelist"
    const val PREF_BLACKLIST = "blacklist"
    const val PREF_LAST_UPDATE = "last_update"
    const val PREF_HOSTS_COUNT = "hosts_count"

    const val UPDATE_INTERVAL_6H = 6
    const val UPDATE_INTERVAL_12H = 12
    const val UPDATE_INTERVAL_24H = 24
    const val UPDATE_INTERVAL_48H = 48
    const val UPDATE_INTERVAL_WEEKLY = 168
    
    const val WORK_TAG_HOSTS_UPDATE = "hosts_update"
    const val WORK_TAG_AUTO_UPDATE = "auto_update"
    
    const val ACTION_UPDATE_HOSTS = "com.arasaka.re_malwack.UPDATE_HOSTS"
    const val ACTION_TOGGLE_BLOCKING = "com.arasaka.re_malwack.TOGGLE_BLOCKING"

    const val BRAND_COLOR_PRIMARY = 0xFF6750A4
    const val BRAND_COLOR_SECONDARY = 0xFF625B71
    const val BRAND_COLOR_TERTIARY = 0xFF7D5260
    const val BRAND_COLOR_ERROR = 0xFFBA1A1A
    const val BRAND_COLOR_SUCCESS = 0xFF006D3B
    const val BRAND_COLOR_WARNING = 0xFFE65100
} 