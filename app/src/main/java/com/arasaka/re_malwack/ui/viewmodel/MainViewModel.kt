package com.arasaka.re_malwack.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arasaka.re_malwack.ReMalwackApplication
import com.arasaka.re_malwack.data.repository.RootRepository
import com.arasaka.re_malwack.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class AppState(
    val isRootAvailable: Boolean = false,
    val isLoading: Boolean = false,
    val hostsCount: Long = 0,
    val lastUpdate: String = "",
    val isBlocking: Boolean = false,
    val autoUpdate: Boolean = false,
    val blockPorn: Boolean = false,
    val blockGambling: Boolean = false,
    val blockFakeNews: Boolean = false,
    val blockSocial: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val updateProgress: Float = 0f,
    val isMagiskModuleInstalled: Boolean = false,
    val magiskModuleVersion: String = "",
    val magiskModuleEnabled: Boolean = true
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val rootRepository: RootRepository = 
        (application as ReMalwackApplication).rootRepository
    
    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()
    
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    
    init {
        checkRootAccess()
        checkMagiskModule()
        loadHostsInfo()
    }
    
    fun checkRootAccess() {
        viewModelScope.launch {
            _appState.value = _appState.value.copy(isLoading = true)
            
            try {
                val hasRoot = rootRepository.requestRootAccess()
                _appState.value = _appState.value.copy(
                    isRootAvailable = hasRoot,
                    isLoading = false,
                    errorMessage = if (!hasRoot) "Root access required" else null
                )
            } catch (e: Exception) {
                _appState.value = _appState.value.copy(
                    isRootAvailable = false,
                    isLoading = false,
                    errorMessage = "Root access error: ${e.message}"
                )
            }
        }
    }
    
    private fun checkMagiskModule() {
        viewModelScope.launch {
            rootRepository.isMagiskModuleInstalled().fold(
                onSuccess = { isInstalled ->
                    _appState.value = _appState.value.copy(
                        isMagiskModuleInstalled = isInstalled
                    )
                    
                    if (isInstalled) {
                        rootRepository.getMagiskModuleVersion().fold(
                            onSuccess = { version ->
                                _appState.value = _appState.value.copy(
                                    magiskModuleVersion = version
                                )
                            },
                            onFailure = { }
                        )
                    }
                },
                onFailure = {
                    _appState.value = _appState.value.copy(
                        isMagiskModuleInstalled = false
                    )
                }
            )
        }
    }
    
    private fun loadHostsInfo() {
        viewModelScope.launch {
            rootRepository.getHostsFileSize().fold(
                onSuccess = { count ->
                    _appState.value = _appState.value.copy(
                        hostsCount = count,
                        isBlocking = count > 100
                    )
                },
                onFailure = {
                    _appState.value = _appState.value.copy(
                        hostsCount = 0,
                        isBlocking = false
                    )
                }
            )
        }
    }
    
    fun updateHosts() {
        viewModelScope.launch {
            _appState.value = _appState.value.copy(
                isLoading = true,
                updateProgress = 0f,
                errorMessage = null
            )
            
            try {
                if (!_appState.value.isMagiskModuleInstalled) {
                    _appState.value = _appState.value.copy(
                        isLoading = false,
                        errorMessage = "Magisk module is required for updates"
                    )
                    return@launch
                }
                
                _appState.value = _appState.value.copy(updateProgress = 0.2f)
                
                rootRepository.executeReMalwackScript("update").fold(
                    onSuccess = { output ->
                        _appState.value = _appState.value.copy(updateProgress = 0.8f)
                        
                        val hostsContent = downloadAndCombineHosts()
                        
                        rootRepository.updateHostsFile(hostsContent).fold(
                            onSuccess = { message ->
                                _appState.value = _appState.value.copy(
                                    isLoading = false,
                                    updateProgress = 1f,
                                    successMessage = "Protection lists updated successfully",
                                    lastUpdate = dateFormatter.format(Date()),
                                    isBlocking = true
                                )
                                
                                rootRepository.flushDnsCache()
                                
                                loadHostsInfo()
                            },
                            onFailure = { error ->
                                _appState.value = _appState.value.copy(
                                    isLoading = false,
                                    updateProgress = 0f,
                                    errorMessage = "Update failed: ${error.message}"
                                )
                            }
                        )
                    },
                    onFailure = { error ->
                        _appState.value = _appState.value.copy(updateProgress = 0.5f)
                        
                        val hostsContent = downloadAndCombineHosts()
                        
                        rootRepository.updateHostsFile(hostsContent).fold(
                            onSuccess = { message ->
                                _appState.value = _appState.value.copy(
                                    isLoading = false,
                                    updateProgress = 1f,
                                    successMessage = "Protection lists updated successfully",
                                    lastUpdate = dateFormatter.format(Date()),
                                    isBlocking = true
                                )
                                
                                rootRepository.flushDnsCache()
                                loadHostsInfo()
                            },
                            onFailure = { updateError ->
                                _appState.value = _appState.value.copy(
                                    isLoading = false,
                                    updateProgress = 0f,
                                    errorMessage = "Update failed: ${updateError.message}"
                                )
                            }
                        )
                    }
                )
            } catch (e: Exception) {
                _appState.value = _appState.value.copy(
                    isLoading = false,
                    updateProgress = 0f,
                    errorMessage = "Unexpected error: ${e.message}"
                )
            }
        }
    }
    
    fun restoreHosts() {
        viewModelScope.launch {
            _appState.value = _appState.value.copy(isLoading = true)
            
            rootRepository.restoreHostsFile().fold(
                onSuccess = { message ->
                    _appState.value = _appState.value.copy(
                        isLoading = false,
                        successMessage = "Hosts file restored successfully",
                        isBlocking = false
                    )
                    loadHostsInfo()
                },
                onFailure = { error ->
                    _appState.value = _appState.value.copy(
                        isLoading = false,
                        errorMessage = "Restore failed: ${error.message}"
                    )
                }
            )
        }
    }
    
    fun backupHosts() {
        viewModelScope.launch {
            _appState.value = _appState.value.copy(isLoading = true)
            
            rootRepository.backupHostsFile().fold(
                onSuccess = { message ->
                    _appState.value = _appState.value.copy(
                        isLoading = false,
                        successMessage = "Hosts file backed up successfully"
                    )
                },
                onFailure = { error ->
                    _appState.value = _appState.value.copy(
                        isLoading = false,
                        errorMessage = "Backup failed: ${error.message}"
                    )
                }
            )
        }
    }
    
    fun enableMagiskModule() {
        viewModelScope.launch {
            rootRepository.enableMagiskModule().fold(
                onSuccess = { message ->
                    _appState.value = _appState.value.copy(
                        magiskModuleEnabled = true,
                        successMessage = message
                    )
                },
                onFailure = { error ->
                    _appState.value = _appState.value.copy(
                        errorMessage = "Failed to enable module: ${error.message}"
                    )
                }
            )
        }
    }
    
    fun disableMagiskModule() {
        viewModelScope.launch {
            rootRepository.disableMagiskModule().fold(
                onSuccess = { message ->
                    _appState.value = _appState.value.copy(
                        magiskModuleEnabled = false,
                        successMessage = message
                    )
                },
                onFailure = { error ->
                    _appState.value = _appState.value.copy(
                        errorMessage = "Failed to disable module: ${error.message}"
                    )
                }
            )
        }
    }
    
    fun toggleBlockingCategory(category: String, enabled: Boolean) {
        _appState.value = when (category) {
            "porn" -> _appState.value.copy(blockPorn = enabled)
            "gambling" -> _appState.value.copy(blockGambling = enabled)
            "fakenews" -> _appState.value.copy(blockFakeNews = enabled)
            "social" -> _appState.value.copy(blockSocial = enabled)
            else -> _appState.value
        }
        
        if (_appState.value.isMagiskModuleInstalled) {
            updateHosts()
        }
    }
    
    fun toggleAutoUpdate(enabled: Boolean) {
        _appState.value = _appState.value.copy(autoUpdate = enabled)
    }
    
    fun clearMessages() {
        _appState.value = _appState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
    
    private suspend fun downloadAndCombineHosts(): String {
        val hostsBuilder = StringBuilder()
        
        hostsBuilder.appendLine("# Re-Malwack Hosts File")
        hostsBuilder.appendLine("# Generated on ${dateFormatter.format(Date())}")
        hostsBuilder.appendLine("# https://github.com/ZG089/Re-Malwack")
        hostsBuilder.appendLine()
        hostsBuilder.appendLine("127.0.0.1 localhost")
        hostsBuilder.appendLine("::1 localhost")
        hostsBuilder.appendLine()
        
        val state = _appState.value
        
        if (state.blockPorn) {
            hostsBuilder.appendLine("# Adult Content Blocking")
            val adultDomains = listOf(
                "pornhub.com", "xvideos.com", "xnxx.com", "redtube.com"
            )
            adultDomains.forEach { domain ->
                hostsBuilder.appendLine("0.0.0.0 $domain")
                hostsBuilder.appendLine("0.0.0.0 www.$domain")
            }
            hostsBuilder.appendLine()
        }
        
        if (state.blockGambling) {
            hostsBuilder.appendLine("# Gambling Blocking")
            val gamblingDomains = listOf(
                "bet365.com", "pokerstars.com", "888casino.com"
            )
            gamblingDomains.forEach { domain ->
                hostsBuilder.appendLine("0.0.0.0 $domain")
                hostsBuilder.appendLine("0.0.0.0 www.$domain")
            }
            hostsBuilder.appendLine()
        }
        
        if (state.blockFakeNews) {
            hostsBuilder.appendLine("# Fake News Blocking")
            val fakeNewsDomains = listOf(
                "infowars.com", "breitbart.com"
            )
            fakeNewsDomains.forEach { domain ->
                hostsBuilder.appendLine("0.0.0.0 $domain")
                hostsBuilder.appendLine("0.0.0.0 www.$domain")
            }
            hostsBuilder.appendLine()
        }
        
        if (state.blockSocial) {
            hostsBuilder.appendLine("# Social Media Blocking")
            val socialDomains = listOf(
                "facebook.com", "instagram.com", "twitter.com", "tiktok.com"
            )
            socialDomains.forEach { domain ->
                hostsBuilder.appendLine("0.0.0.0 $domain")
                hostsBuilder.appendLine("0.0.0.0 www.$domain")
            }
            hostsBuilder.appendLine()
        }
        
        hostsBuilder.appendLine("# Ad Blocking")
        val adDomains = listOf(
            "doubleclick.net", "googleadservices.com", "googlesyndication.com",
            "googletagmanager.com", "google-analytics.com", "facebook.com"
        )
        adDomains.forEach { domain ->
            hostsBuilder.appendLine("0.0.0.0 $domain")
            hostsBuilder.appendLine("0.0.0.0 www.$domain")
        }
        
        return hostsBuilder.toString()
    }
} 