package com.omnitool.features.text.markdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import com.omnitool.ui.screens.WorkspaceInputField

/**
 * Markdown Preview Screen - Live preview as you type
 */
@Composable
fun MarkdownPreviewScreen(
    onBack: () -> Unit,
    viewModel: MarkdownPreviewViewModel = hiltViewModel()
) {
    ToolWorkspaceScreen(
        toolName = "Markdown Preview",
        toolIcon = OmniToolIcons.Markdown,
        accentColor = OmniToolTheme.colors.mint,
        onBack = onBack,
        inputContent = {
            WorkspaceInputField(
                value = viewModel.inputText,
                onValueChange = { viewModel.updateInput(it) },
                placeholder = "# Type markdown here...\n\n**Bold**, *italic*, `code`",
                minLines = 6
            )
        },
        outputContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp)
                    .clip(OmniToolTheme.shapes.medium)
                    .background(OmniToolTheme.colors.elevatedSurface)
                    .padding(OmniToolTheme.spacing.sm)
                    .verticalScroll(rememberScrollState())
            ) {
                if (viewModel.inputText.isEmpty()) {
                    Text(
                        text = "Preview will appear here...",
                        style = OmniToolTheme.typography.body,
                        color = OmniToolTheme.colors.textMuted
                    )
                } else {
                    RenderedMarkdown(markdown = viewModel.inputText)
                }
            }
        },
        primaryActionLabel = "Clear",
        onPrimaryAction = { viewModel.clearAll() }
    )
}

@Composable
private fun RenderedMarkdown(markdown: String) {
    val annotatedText = buildAnnotatedString {
        var remaining = markdown
        
        // Process line by line
        remaining.lines().forEach { line ->
            when {
                line.startsWith("### ") -> {
                    withStyle(SpanStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OmniToolTheme.colors.textPrimary
                    )) {
                        append(line.removePrefix("### "))
                    }
                }
                line.startsWith("## ") -> {
                    withStyle(SpanStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = OmniToolTheme.colors.textPrimary
                    )) {
                        append(line.removePrefix("## "))
                    }
                }
                line.startsWith("# ") -> {
                    withStyle(SpanStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = OmniToolTheme.colors.textPrimary
                    )) {
                        append(line.removePrefix("# "))
                    }
                }
                line.startsWith("> ") -> {
                    withStyle(SpanStyle(
                        fontStyle = FontStyle.Italic,
                        color = OmniToolTheme.colors.textSecondary
                    )) {
                        append("│ ${line.removePrefix("> ")}")
                    }
                }
                line.startsWith("- ") || line.startsWith("* ") -> {
                    withStyle(SpanStyle(color = OmniToolTheme.colors.textPrimary)) {
                        append("• ${line.drop(2)}")
                    }
                }
                line == "---" || line == "***" -> {
                    withStyle(SpanStyle(color = OmniToolTheme.colors.textMuted)) {
                        append("────────────────")
                    }
                }
                else -> {
                    appendFormattedLine(line)
                }
            }
            append("\n")
        }
    }
    
    Text(
        text = annotatedText,
        style = OmniToolTheme.typography.body
    )
}

private fun AnnotatedString.Builder.appendFormattedLine(line: String) {
    var text = line
    var i = 0
    
    while (i < text.length) {
        when {
            text.substring(i).startsWith("**") -> {
                val end = text.indexOf("**", i + 2)
                if (end != -1) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(text.substring(i + 2, end))
                    }
                    i = end + 2
                } else {
                    append(text[i])
                    i++
                }
            }
            text.substring(i).startsWith("*") -> {
                val end = text.indexOf("*", i + 1)
                if (end != -1) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(text.substring(i + 1, end))
                    }
                    i = end + 1
                } else {
                    append(text[i])
                    i++
                }
            }
            text.substring(i).startsWith("`") -> {
                val end = text.indexOf("`", i + 1)
                if (end != -1) {
                    withStyle(SpanStyle(
                        fontWeight = FontWeight.Medium,
                        background = OmniToolTheme.colors.surface
                    )) {
                        append(text.substring(i + 1, end))
                    }
                    i = end + 1
                } else {
                    append(text[i])
                    i++
                }
            }
            else -> {
                append(text[i])
                i++
            }
        }
    }
}
