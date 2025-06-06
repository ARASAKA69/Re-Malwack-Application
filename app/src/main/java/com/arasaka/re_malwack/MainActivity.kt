package com.arasaka.re_malwack

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arasaka.re_malwack.service.HostsUpdateService
import com.arasaka.re_malwack.ui.theme.AccentRed
import com.arasaka.re_malwack.ui.theme.ReMalwackTheme
import com.arasaka.re_malwack.ui.theme.StatusSuccess
import com.arasaka.re_malwack.ui.theme.StatusWarning
import com.arasaka.re_malwack.ui.theme.ThemeMode
import com.arasaka.re_malwack.ui.viewmodel.AppState
import com.arasaka.re_malwack.ui.viewmodel.MainViewModel
import com.arasaka.re_malwack.utils.Constants
import com.arasaka.re_malwack.utils.LanguageManager
import com.arasaka.re_malwack.utils.ThemeManager
import kotlinx.coroutines.launch
import java.util.Locale

private val SunIcon: ImageVector
    get() = ImageVector.Builder(
        name = "Sun",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
            strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
        ) {
            moveTo(12f, 1f)
            lineTo(12f, 3f)
            moveTo(12f, 21f)
            lineTo(12f, 23f)
            moveTo(4.22f, 4.22f)
            lineTo(5.64f, 5.64f)
            moveTo(18.36f, 18.36f)
            lineTo(19.78f, 19.78f)
            moveTo(1f, 12f)
            lineTo(3f, 12f)
            moveTo(21f, 12f)
            lineTo(23f, 12f)
            moveTo(4.22f, 19.78f)
            lineTo(5.64f, 18.36f)
            moveTo(18.36f, 5.64f)
            lineTo(19.78f, 4.22f)
            moveTo(12f, 8f)
            arcTo(4f, 4f, 0f, true, true, 12f, 16f)
            arcTo(4f, 4f, 0f, true, true, 12f, 8f)
        }
    }.build()

private val MoonIcon: ImageVector
    get() = ImageVector.Builder(
        name = "Moon",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
            strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
        ) {
            moveTo(21f, 12.79f)
            arcTo(9f, 9f, 180f, true, true, 11.21f, 3f)
            arcTo(7f, 7f, 0f, false, false, 21f, 12.79f)
            close()
        }
    }.build()

private val PaletteIcon: ImageVector
    get() = ImageVector.Builder(
        name = "Palette",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
            strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
        ) {
            moveTo(12f, 2f)
            curveTo(17.52f, 2f, 22f, 6.48f, 22f, 12f)
            curveTo(22f, 13.66f, 21.33f, 15f, 20f, 15f)
            horizontalLineTo(16f)
            curveTo(14.9f, 15f, 14f, 15.9f, 14f, 17f)
            curveTo(14f, 17.55f, 14.22f, 18.05f, 14.59f, 18.42f)
            curveTo(14.95f, 18.78f, 15f, 19.45f, 15f, 20f)
            curveTo(15f, 21.1f, 14.1f, 22f, 13f, 22f)
            curveTo(7.48f, 22f, 3f, 17.52f, 3f, 12f)
            curveTo(3f, 6.48f, 7.48f, 2f, 12f, 2f)
            close()
        }
        path(
            fill = androidx.compose.ui.graphics.SolidColor(Color.Black),
            stroke = null
        ) {
            moveTo(6.5f, 10.5f)
            arcTo(1.5f, 1.5f, 0f, true, true, 6.5f, 13.5f)
            arcTo(1.5f, 1.5f, 0f, true, true, 6.5f, 10.5f)
            moveTo(9.5f, 7.5f)
            arcTo(1.5f, 1.5f, 0f, true, true, 9.5f, 10.5f)
            arcTo(1.5f, 1.5f, 0f, true, true, 9.5f, 7.5f)
            moveTo(14.5f, 7.5f)
            arcTo(1.5f, 1.5f, 0f, true, true, 14.5f, 10.5f)
            arcTo(1.5f, 1.5f, 0f, true, true, 14.5f, 7.5f)
            moveTo(17.5f, 10.5f)
            arcTo(1.5f, 1.5f, 0f, true, true, 17.5f, 13.5f)
            arcTo(1.5f, 1.5f, 0f, true, true, 17.5f, 10.5f)
        }
    }.build()

private val SystemIcon: ImageVector
    get() = ImageVector.Builder(
        name = "System",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
            strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
        ) {
            moveTo(2f, 3f)
            lineTo(20f, 3f)
            arcTo(2f, 2f, 0f, false, true, 22f, 5f)
            lineTo(22f, 15f)
            arcTo(2f, 2f, 0f, false, true, 20f, 17f)
            lineTo(2f, 17f)
            arcTo(2f, 2f, 0f, false, true, 0f, 15f)
            lineTo(0f, 5f)
            arcTo(2f, 2f, 0f, false, true, 2f, 3f)
            close()
            moveTo(8f, 21f)
            lineTo(16f, 21f)
            moveTo(12f, 17f)
            lineTo(12f, 21f)
        }
    }.build()

class MainActivity : ComponentActivity() {
    
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val themeManager = remember { ThemeManager(this) }
            val selectedTheme by themeManager.selectedTheme.collectAsStateWithLifecycle(initialValue = ThemeMode.LIGHT)
            
            ReMalwackTheme(themeMode = selectedTheme) {
                ReMalwackApp(viewModel = viewModel, themeManager = themeManager)
            }
        }
    }
    
    override fun attachBaseContext(newBase: Context?) {
        var updatedContext = newBase
        
        try {
            if (newBase != null) {
                val languageManager = LanguageManager(newBase)
                val savedLanguage = languageManager.getLanguageSync()
                
                if (savedLanguage != "system") {
                    val locale = Locale(savedLanguage)
                    Locale.setDefault(locale)
                    val config = Configuration(newBase.resources.configuration)
                    config.setLocale(locale)
                    updatedContext = newBase.createConfigurationContext(config)
                }
            }
        } catch (_: Exception) {
            updatedContext = newBase
        }
        
        super.attachBaseContext(updatedContext)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReMalwackApp(viewModel: MainViewModel, themeManager: ThemeManager) {
    val appState by viewModel.appState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val languageManager = remember { LanguageManager(context) }
    val selectedLanguage by languageManager.selectedLanguage.collectAsStateWithLifecycle(initialValue = "system")
    val selectedTheme by themeManager.selectedTheme.collectAsStateWithLifecycle(initialValue = ThemeMode.LIGHT)
    
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    
            LaunchedEffect(appState.isRootAvailable) {
                when {
                    appState.isRootAvailable && !appState.isMagiskModuleInstalled -> {
                    }
                }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
            
            item {
                MinimalHeaderCard(appState = appState)
            }
            
            if (appState.isRootAvailable && !appState.isMagiskModuleInstalled) {
                item {
                    MinimalWarningCard()
                }
            }
            
            item {
                MinimalStatusCard(
                    appState = appState,
                    onUpdateHosts = { viewModel.updateHosts() },
                    onRestoreHosts = { viewModel.restoreHosts() },
                    onBackupHosts = { viewModel.backupHosts() }
                )
            }
            
            item {
                MinimalCategoriesCard(
                    appState = appState,
                    onToggleCategory = { category, enabled ->
                        viewModel.toggleBlockingCategory(category, enabled)
                    }
                )
            }
            
            item {
                MinimalSettingsCard(
                    appState = appState,
                    selectedTheme = selectedTheme,
                    selectedLanguage = selectedLanguage,
                    languageManager = languageManager,
                    onToggleAutoUpdate = { viewModel.toggleAutoUpdate(it) },
                    onThemeClick = { showThemeDialog = true },
                    onLanguageClick = { showLanguageDialog = true }
                )
            }
            
            item {
                MinimalInfoCard(appState = appState)
            }
            
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp, horizontal = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFFD700),
                                        Color(0xFFFFA500),
                                        Color(0xFFFFD700)
                                    )
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW,
                                    "https://github.com/ARASAKA69".toUri())
                                context.startActivity(intent)
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Developed by ARASAKA",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
    
    if (showThemeDialog) {
        MinimalThemeDialog(
            themeManager = themeManager,
            selectedTheme = selectedTheme,
            onThemeSelected = { theme ->
                scope.launch {
                    themeManager.setTheme(theme)
                }
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }
    
    if (showLanguageDialog) {
        MinimalLanguageDialog(
            languageManager = languageManager,
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { languageCode ->
                scope.launch {
                    languageManager.setLanguage(languageCode)
                    (context as ComponentActivity).recreate()
                }
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }
}

@Composable
fun MinimalHeaderCard(appState: AppState) {
    val uriHandler = LocalUriHandler.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .clickable {
                        uriHandler.openUri("https://github.com/ZG089/Re-Malwack")
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.remalwack_logo),
                    contentDescription = "Re-Malwack Logo",
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Re-Malwack",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.advanced_protection),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun MinimalWarningCard() {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(R.string.magisk_module_required).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Text(
                text = stringResource(R.string.install_module_to_enable),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.error)
                    .clickable { 
                        val intent = Intent(Intent.ACTION_VIEW,
                            "https://github.com/ZG089/Re-Malwack/releases/latest".toUri())
                        context.startActivity(intent)
                    }
                    .padding(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.download_latest_module),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun MinimalStatusCard(
    appState: AppState,
    onUpdateHosts: () -> Unit,
    onRestoreHosts: () -> Unit,
    onBackupHosts: () -> Unit
) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(R.string.protection_status).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (appState.isBlocking) StatusSuccess else StatusWarning,
                            shape = CircleShape
                        )
                )
                
                Column {
                    Text(
                        text = if (appState.isBlocking) 
                            stringResource(R.string.active_protection) 
                        else 
                            stringResource(R.string.protection_inactive),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${appState.hostsCount} ${stringResource(R.string.blocked_domains)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (appState.isLoading) {
                LinearProgressIndicator(
                    progress = { appState.updateProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Text(
                    text = stringResource(R.string.updating_hosts),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            color = if (appState.isRootAvailable && appState.isMagiskModuleInstalled && !appState.isLoading)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable(
                            enabled = appState.isRootAvailable && appState.isMagiskModuleInstalled && !appState.isLoading
                        ) {
                            val intent = Intent(context, HostsUpdateService::class.java).apply {
                                action = Constants.ACTION_UPDATE_HOSTS
                            }
                            context.startForegroundService(intent)
                        }
                        .padding(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.update),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (appState.isRootAvailable && appState.isMagiskModuleInstalled && !appState.isLoading)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            color = if (appState.isRootAvailable && !appState.isLoading)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable(enabled = appState.isRootAvailable && !appState.isLoading) {
                            onBackupHosts()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Backup",
                        tint = if (appState.isRootAvailable && !appState.isLoading)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            color = if (appState.isRootAvailable && !appState.isLoading)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable(enabled = appState.isRootAvailable && !appState.isLoading) {
                            onRestoreHosts()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Restore",
                        tint = if (appState.isRootAvailable && !appState.isLoading)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MinimalCategoriesCard(
    appState: AppState,
    onToggleCategory: (String, Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.protection_categories).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            val categories = listOf(
                Triple("porn", stringResource(R.string.adult_content), appState.blockPorn),
                Triple("gambling", stringResource(R.string.gambling), appState.blockGambling),
                Triple("fakenews", stringResource(R.string.fake_news), appState.blockFakeNews),
                Triple("social", stringResource(R.string.social_media), appState.blockSocial)
            )
            
            categories.forEach { (key, title, enabled) ->
                MinimalToggleItem(
                    title = title,
                    enabled = enabled,
                    onToggle = { onToggleCategory(key, it) }
                )
            }
        }
    }
}

@Composable
fun MinimalToggleItem(
    title: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onToggle(!enabled) }
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            )
            
            Switch(
                checked = enabled,
                onCheckedChange = null,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AccentRed,
                    checkedTrackColor = AccentRed.copy(alpha = 0.3f),
                    checkedBorderColor = AccentRed,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    uncheckedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

@Composable
fun MinimalSettingsCard(
    appState: AppState,
    selectedTheme: ThemeMode,
    selectedLanguage: String,
    languageManager: LanguageManager,
    onToggleAutoUpdate: (Boolean) -> Unit,
    onThemeClick: () -> Unit,
    onLanguageClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.settings).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            MinimalToggleItem(
                title = stringResource(R.string.auto_update),
                enabled = appState.autoUpdate,
                onToggle = onToggleAutoUpdate
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onThemeClick() }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = selectedTheme.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onLanguageClick() }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.menu_language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = getLanguageDisplayName(selectedLanguage),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MinimalInfoCard(appState: AppState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.information).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            if (appState.lastUpdate.isNotEmpty()) {
                MinimalInfoRow(
                    title = stringResource(R.string.last_update),
                    value = appState.lastUpdate
                )
            }
            
            MinimalInfoRow(
                title = stringResource(R.string.magisk_module),
                value = if (appState.isMagiskModuleInstalled) 
                    stringResource(R.string.installed) 
                else 
                    stringResource(R.string.not_installed)
            )
            
            MinimalInfoRow(
                title = "App Version",
                value = "0.2.0"
            )
        }
    }
}

@Composable
fun MinimalInfoRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MinimalThemeDialog(
    themeManager: ThemeManager,
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    val availableThemes = remember { themeManager.getAvailableThemes() }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(300.dp)
                .clickable(enabled = false) { },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Select Theme",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    availableThemes.forEach { theme ->
                        val isSupported = if (theme == ThemeMode.MONET) themeManager.isMonetSupported() else true
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    color = if (selectedTheme == theme)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else if (isSupported)
                                        Color.Transparent
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                                .clickable(enabled = isSupported) { 
                                    if (isSupported) onThemeSelected(theme) 
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = when (theme) {
                                    ThemeMode.LIGHT -> SunIcon
                                    ThemeMode.DARK -> MoonIcon
                                    ThemeMode.MONET -> PaletteIcon
                                    ThemeMode.CUSTOM -> SystemIcon
                                },
                                contentDescription = when (theme) {
                                    ThemeMode.LIGHT -> "Light Theme"
                                    ThemeMode.DARK -> "Dark Theme"
                                    ThemeMode.MONET -> "Material You Theme"
                                    ThemeMode.CUSTOM -> "System Theme"
                                },
                                tint = if (selectedTheme == theme)
                                    MaterialTheme.colorScheme.primary
                                else if (isSupported)
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = theme.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (selectedTheme == theme)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else if (isSupported)
                                        MaterialTheme.colorScheme.onSurface
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                                if (theme == ThemeMode.MONET && !isSupported) {
                                    Text(
                                        text = "Not supported",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            
                            if (selectedTheme == theme) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onDismiss() }
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Close",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun MinimalLanguageDialog(
    languageManager: LanguageManager,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val languages = remember { languageManager.getAvailableLanguages() }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(300.dp)
                .clickable(enabled = false) { },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Select Language",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    languages.forEach { language ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    color = if (selectedLanguage == language.code)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        Color.Transparent
                                )
                                .clickable { onLanguageSelected(language.code) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = language.flag,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Column(modifier = Modifier.weight(1f)) {
    Text(
                                    text = language.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (selectedLanguage == language.code)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            if (selectedLanguage == language.code) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onDismiss() }
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Close",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun getLanguageDisplayName(languageCode: String): String {
    return when (languageCode) {
        "system" -> "System"
        "en" -> "English"
        "de" -> "Deutsch"
        "pl" -> "Polski"
        else -> "System"
    }
}