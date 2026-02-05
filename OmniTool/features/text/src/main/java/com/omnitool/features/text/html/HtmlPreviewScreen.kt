package com.omnitool.features.text.html

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import com.omnitool.ui.screens.WorkspaceInputField

/**
 * HTML Preview Screen - Edit HTML and see live preview
 */
@Composable
fun HtmlPreviewScreen(
    onBack: () -> Unit,
    viewModel: HtmlPreviewViewModel = hiltViewModel()
) {
    ToolWorkspaceScreen(
        toolName = "HTML Preview",
        toolIcon = OmniToolIcons.Html,
        accentColor = OmniToolTheme.colors.mint,
        onBack = onBack,
        inputContent = {
            WorkspaceInputField(
                value = viewModel.inputText,
                onValueChange = { viewModel.updateInput(it) },
                placeholder = "<!DOCTYPE html>\n<html>\n<body>\n  <h1>Hello</h1>\n</body>\n</html>",
                minLines = 6
            )
        },
        outputContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(OmniToolTheme.shapes.medium)
                    .background(OmniToolTheme.colors.textPrimary) // White background for HTML
            ) {
                if (viewModel.inputText.isNotEmpty()) {
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                webViewClient = WebViewClient()
                                settings.javaScriptEnabled = false // Security
                            }
                        },
                        update = { webView ->
                            val safeHtml = viewModel.getSafeHtml()
                            webView.loadDataWithBaseURL(
                                null,
                                safeHtml,
                                "text/html",
                                "UTF-8",
                                null
                            )
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        },
        primaryActionLabel = "Template",
        onPrimaryAction = { viewModel.insertTemplate() },
        secondaryActionLabel = "Clear",
        onSecondaryAction = { viewModel.clearAll() }
    )
}
