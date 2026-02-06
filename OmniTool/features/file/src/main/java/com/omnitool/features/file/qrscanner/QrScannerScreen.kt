package com.omnitool.features.file.qrscanner

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

/**
 * QR Scanner Screen
 * 
 * Features:
 * - Camera preview
 * - Real-time scanning
 * - Result actions
 * - Scan history
 */
@Composable
fun QrScannerScreen(
    onBack: () -> Unit,
    viewModel: QrScannerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.setPermissionGranted(granted)
        if (granted) {
            viewModel.startScanning()
        }
    }
    
    ToolWorkspaceScreen(
        toolName = "QR Scanner",
        toolIcon = OmniToolIcons.QrScanner,
        accentColor = OmniToolTheme.colors.skyBlue,
        onBack = onBack,
        inputContent = {
            if (viewModel.needsPermission) {
                PermissionRequest(
                    onRequest = { permissionLauncher.launch(Manifest.permission.CAMERA) }
                )
            } else if (viewModel.scannedResult != null) {
                ScanResultDisplay(
                    result = viewModel.scannedResult!!,
                    onClear = { viewModel.clearResult() },
                    onCopy = { copyToClipboard(context, viewModel.scannedResult!!) },
                    onOpen = { openResult(context, viewModel.scannedResult!!) }
                )
            } else {
                CameraPreview(
                    isScanning = viewModel.isScanning,
                    onImageAnalysis = { viewModel.processImage(it) }
                )
            }
        },
        outputContent = {
            ScanHistory(
                history = viewModel.scanHistory,
                onClear = { viewModel.clearHistory() }
            )
        },
        primaryActionLabel = when {
            viewModel.needsPermission -> "Grant Permission"
            viewModel.scannedResult != null -> "Scan Again"
            viewModel.isScanning -> "Stop"
            else -> "Start Scan"
        },
        onPrimaryAction = {
            when {
                viewModel.needsPermission -> permissionLauncher.launch(Manifest.permission.CAMERA)
                viewModel.scannedResult != null -> viewModel.clearResult()
                viewModel.isScanning -> viewModel.stopScanning()
                else -> viewModel.startScanning()
            }
        }
    )
}

@Composable
private fun PermissionRequest(onRequest: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.elevatedSurface)
            .clickable(onClick = onRequest),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = OmniToolIcons.Camera,
                contentDescription = null,
                tint = OmniToolTheme.colors.skyBlue,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Camera permission required",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textPrimary
            )
            Text(
                text = "Tap to grant permission",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
        }
    }
}

@Composable
private fun CameraPreview(
    isScanning: Boolean,
    onImageAnalysis: (androidx.camera.core.ImageProxy) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val previewView = remember { PreviewView(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    DisposableEffect(isScanning) {
        if (isScanning) {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor) { imageProxy ->
                            onImageAnalysis(imageProxy)
                        }
                    }
                
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalyzer
                    )
                } catch (e: Exception) {
                    // Camera binding failed
                }
            }, ContextCompat.getMainExecutor(context))
        }
        
        onDispose {
            try {
                val cameraProvider = ProcessCameraProvider.getInstance(context).get()
                cameraProvider.unbindAll()
            } catch (_: Exception) {}
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(OmniToolTheme.shapes.large)
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        
        // Scan overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            // Scanner frame indicator could go here
        }
        
        if (!isScanning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(OmniToolTheme.colors.background.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tap 'Start Scan' to begin",
                    style = OmniToolTheme.typography.body,
                    color = OmniToolTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun ScanResultDisplay(
    result: String,
    onClear: () -> Unit,
    onCopy: () -> Unit,
    onOpen: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.primaryLime.copy(alpha = 0.1f))
            .padding(OmniToolTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = OmniToolIcons.Check,
                contentDescription = null,
                tint = OmniToolTheme.colors.primaryLime
            )
            Text(
                text = "Scanned!",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.primaryLime
            )
        }
        
        Text(
            text = result,
            style = OmniToolTheme.typography.body,
            color = OmniToolTheme.colors.textPrimary,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCopy,
                modifier = Modifier.weight(1f)
            ) {
                Text("Copy")
            }
            
            if (result.startsWith("http") || result.startsWith("tel:") || result.startsWith("mailto:")) {
                Button(
                    onClick = onOpen,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OmniToolTheme.colors.skyBlue
                    )
                ) {
                    Text("Open")
                }
            }
        }
    }
}

@Composable
private fun ScanHistory(
    history: List<ScanResult>,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Scan History",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textSecondary
            )
            if (history.isNotEmpty()) {
                TextButton(onClick = onClear) {
                    Text(
                        text = "Clear",
                        color = OmniToolTheme.colors.softCoral
                    )
                }
            }
        }
        
        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No scans yet",
                    style = OmniToolTheme.typography.caption,
                    color = OmniToolTheme.colors.textMuted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.heightIn(max = 150.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(history) { scan ->
                    HistoryItem(scan)
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(scan: ScanResult) {
    val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.small)
            .background(OmniToolTheme.colors.surface)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = scan.content,
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${scan.type.displayName} • ${dateFormat.format(Date(scan.timestamp))}",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted
            )
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Scanned QR", text))
}

private fun openResult(context: Context, result: String) {
    try {
        val intent = when {
            result.startsWith("http") -> Intent(Intent.ACTION_VIEW, Uri.parse(result))
            result.startsWith("tel:") -> Intent(Intent.ACTION_DIAL, Uri.parse(result))
            result.startsWith("mailto:") -> Intent(Intent.ACTION_SENDTO, Uri.parse(result))
            else -> null
        }
        intent?.let { context.startActivity(it) }
    } catch (_: Exception) {}
}
