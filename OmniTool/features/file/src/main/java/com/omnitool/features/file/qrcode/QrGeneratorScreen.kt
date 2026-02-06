package com.omnitool.features.file.qrcode

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.components.ErrorCard
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * QR Code Generator Screen
 * 
 * Features:
 * - Content type selector
 * - Input fields based on type
 * - QR code preview
 */
@Composable
fun QrGeneratorScreen(
    onBack: () -> Unit,
    viewModel: QrGeneratorViewModel = hiltViewModel()
) {
    ToolWorkspaceScreen(
        toolName = "QR Generator",
        toolIcon = OmniToolIcons.QrCode,
        accentColor = OmniToolTheme.colors.primaryLime,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Content type selector
                ContentTypeSelector(
                    selected = viewModel.contentType,
                    onSelect = { viewModel.setContentType(it) }
                )
                
                // Dynamic input fields based on type
                ContentInputFields(
                    contentType = viewModel.contentType,
                    textInput = viewModel.inputText,
                    onTextChange = { viewModel.updateInput(it) },
                    urlInput = viewModel.urlInput,
                    onUrlChange = { viewModel.updateUrl(it) },
                    phoneInput = viewModel.phoneInput,
                    onPhoneChange = { viewModel.updatePhone(it) },
                    emailInput = viewModel.emailInput,
                    onEmailChange = { viewModel.updateEmail(it) },
                    emailSubject = viewModel.emailSubject,
                    onSubjectChange = { viewModel.updateEmailSubject(it) },
                    wifiSsid = viewModel.wifiSsid,
                    onSsidChange = { viewModel.updateWifiSsid(it) },
                    wifiPassword = viewModel.wifiPassword,
                    onPasswordChange = { viewModel.updateWifiPassword(it) },
                    wifiType = viewModel.wifiType,
                    onWifiTypeChange = { viewModel.setWifiType(it) }
                )
            }
        },
        outputContent = {
            Column {
                if (viewModel.errorMessage != null) {
                    ErrorCard(
                        title = "Error",
                        explanation = viewModel.errorMessage!!,
                        suggestion = "Please check your input"
                    )
                    Spacer(modifier = Modifier.height(OmniToolTheme.spacing.xs))
                }
                
                QrCodeDisplay(
                    bitmap = viewModel.qrBitmap,
                    isGenerating = viewModel.isGenerating
                )
            }
        },
        primaryActionLabel = if (viewModel.isGenerating) "Generating..." else "Generate",
        onPrimaryAction = { viewModel.generate() },
        secondaryActionLabel = if (viewModel.qrBitmap != null) "Clear" else null,
        onSecondaryAction = if (viewModel.qrBitmap != null) {
            { viewModel.clear() }
        } else null
    )
}

@Composable
private fun ContentTypeSelector(
    selected: QrContentType,
    onSelect: (QrContentType) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(QrContentType.values().toList()) { type ->
            val isSelected = type == selected
            Box(
                modifier = Modifier
                    .clip(OmniToolTheme.shapes.small)
                    .background(
                        if (isSelected) OmniToolTheme.colors.primaryLime.copy(alpha = 0.2f)
                        else OmniToolTheme.colors.surface
                    )
                    .clickable { onSelect(type) }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = type.displayName,
                    style = OmniToolTheme.typography.label,
                    color = if (isSelected) OmniToolTheme.colors.primaryLime 
                            else OmniToolTheme.colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun ContentInputFields(
    contentType: QrContentType,
    textInput: String,
    onTextChange: (String) -> Unit,
    urlInput: String,
    onUrlChange: (String) -> Unit,
    phoneInput: String,
    onPhoneChange: (String) -> Unit,
    emailInput: String,
    onEmailChange: (String) -> Unit,
    emailSubject: String,
    onSubjectChange: (String) -> Unit,
    wifiSsid: String,
    onSsidChange: (String) -> Unit,
    wifiPassword: String,
    onPasswordChange: (String) -> Unit,
    wifiType: WifiType,
    onWifiTypeChange: (WifiType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (contentType) {
            QrContentType.TEXT -> {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = onTextChange,
                    label = { Text("Enter text") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    colors = textFieldColors()
                )
            }
            QrContentType.URL -> {
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = onUrlChange,
                    label = { Text("Website URL") },
                    placeholder = { Text("example.com") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors()
                )
            }
            QrContentType.PHONE -> {
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = onPhoneChange,
                    label = { Text("Phone number") },
                    placeholder = { Text("+1 234 567 8900") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors()
                )
            }
            QrContentType.EMAIL -> {
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = onEmailChange,
                    label = { Text("Email address") },
                    placeholder = { Text("name@example.com") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors()
                )
                OutlinedTextField(
                    value = emailSubject,
                    onValueChange = onSubjectChange,
                    label = { Text("Subject (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors()
                )
            }
            QrContentType.WIFI -> {
                OutlinedTextField(
                    value = wifiSsid,
                    onValueChange = onSsidChange,
                    label = { Text("Network name (SSID)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors()
                )
                OutlinedTextField(
                    value = wifiPassword,
                    onValueChange = onPasswordChange,
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors()
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WifiType.values().forEach { type ->
                        val isSelected = type == wifiType
                        FilterChip(
                            selected = isSelected,
                            onClick = { onWifiTypeChange(type) },
                            label = { Text(type.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OmniToolTheme.colors.primaryLime.copy(alpha = 0.2f),
                                selectedLabelColor = OmniToolTheme.colors.primaryLime
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = OmniToolTheme.colors.primaryLime,
    unfocusedBorderColor = OmniToolTheme.colors.surface,
    focusedLabelColor = OmniToolTheme.colors.primaryLime,
    unfocusedLabelColor = OmniToolTheme.colors.textMuted,
    cursorColor = OmniToolTheme.colors.primaryLime,
    focusedTextColor = OmniToolTheme.colors.textPrimary,
    unfocusedTextColor = OmniToolTheme.colors.textPrimary
)

@Composable
private fun QrCodeDisplay(
    bitmap: androidx.compose.ui.graphics.ImageBitmap?,
    isGenerating: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.large)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "QR Code",
            style = OmniToolTheme.typography.label,
            color = OmniToolTheme.colors.textSecondary
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.sm))
        
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(OmniToolTheme.shapes.medium)
                .background(androidx.compose.ui.graphics.Color.White),
            contentAlignment = Alignment.Center
        ) {
            when {
                isGenerating -> {
                    CircularProgressIndicator(
                        color = OmniToolTheme.colors.primaryLime
                    )
                }
                bitmap != null -> {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Generated QR Code",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = OmniToolIcons.QrCode,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = OmniToolTheme.colors.textMuted.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "QR code will appear here",
                            style = OmniToolTheme.typography.caption,
                            color = OmniToolTheme.colors.textMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
