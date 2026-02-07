package com.omnitool.features.security.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen
import java.text.SimpleDateFormat
import java.util.*

/**
 * Encrypted Notes Screen
 */
@Composable
fun EncryptedNotesScreen(
    onBack: () -> Unit,
    viewModel: EncryptedNotesViewModel = hiltViewModel()
) {
    // Show lock screen if locked
    if (viewModel.isLocked) {
        LockScreen(
            hasPin = viewModel.hasPin,
            errorMessage = viewModel.errorMessage,
            onUnlock = { viewModel.unlock(it) },
            onSetupPin = { viewModel.setupPin(it) },
            onBack = onBack
        )
        return
    }
    
    // Show note editor if editing
    if (viewModel.editingTitle.isNotEmpty() || viewModel.editingContent.isNotEmpty() || 
        viewModel.currentNote != null) {
        NoteEditor(
            title = viewModel.editingTitle,
            content = viewModel.editingContent,
            onTitleChange = { viewModel.updateTitle(it) },
            onContentChange = { viewModel.updateContent(it) },
            onSave = { viewModel.saveNote() },
            onCancel = { viewModel.cancelEdit() }
        )
        return
    }
    
    // Notes list
    ToolWorkspaceScreen(
        toolName = "Encrypted Notes",
        toolIcon = OmniToolIcons.EncryptedNotes,
        accentColor = OmniToolTheme.colors.lavender,
        onBack = onBack,
        inputContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(OmniToolTheme.spacing.sm)
            ) {
                // Lock button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { viewModel.lock() }) {
                        Icon(
                            imageVector = OmniToolIcons.Vault,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Lock", color = OmniToolTheme.colors.lavender)
                    }
                }
                
                // Notes list
                if (viewModel.notes.isEmpty()) {
                    EmptyNotesMessage()
                } else {
                    NotesList(
                        notes = viewModel.notes,
                        onNoteClick = { viewModel.editNote(it) },
                        onDeleteNote = { viewModel.deleteNote(it) }
                    )
                }
            }
        },
        outputContent = { },
        primaryActionLabel = "New Note",
        onPrimaryAction = { viewModel.createNote(); viewModel.updateTitle("") }
    )
}

@Composable
private fun LockScreen(
    hasPin: Boolean,
    errorMessage: String?,
    onUnlock: (String) -> Boolean,
    onSetupPin: (String) -> Unit,
    onBack: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isSettingUp by remember { mutableStateOf(!hasPin) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OmniToolTheme.colors.background)
            .padding(OmniToolTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = OmniToolIcons.Vault,
            contentDescription = null,
            tint = OmniToolTheme.colors.lavender,
            modifier = Modifier.size(64.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (isSettingUp) "Create PIN" else "Enter PIN",
            style = OmniToolTheme.typography.header,
            color = OmniToolTheme.colors.textPrimary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = pin,
            onValueChange = { if (it.length <= 6) pin = it },
            label = { Text("PIN") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OmniToolTheme.colors.lavender,
                cursorColor = OmniToolTheme.colors.lavender
            )
        )
        
        if (isSettingUp) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = confirmPin,
                onValueChange = { if (it.length <= 6) confirmPin = it },
                label = { Text("Confirm PIN") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OmniToolTheme.colors.lavender,
                    cursorColor = OmniToolTheme.colors.lavender
                )
            )
        }
        
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.softCoral
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                if (isSettingUp) {
                    if (pin == confirmPin) {
                        onSetupPin(pin)
                    }
                } else {
                    onUnlock(pin)
                }
            },
            enabled = pin.length >= 4 && (!isSettingUp || pin == confirmPin),
            colors = ButtonDefaults.buttonColors(
                containerColor = OmniToolTheme.colors.lavender
            )
        ) {
            Text(if (isSettingUp) "Set PIN" else "Unlock")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onBack) {
            Text("Cancel", color = OmniToolTheme.colors.textMuted)
        }
    }
}

@Composable
private fun EmptyNotesMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = OmniToolIcons.Notes,
                contentDescription = null,
                tint = OmniToolTheme.colors.textMuted,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No encrypted notes yet",
                style = OmniToolTheme.typography.body,
                color = OmniToolTheme.colors.textMuted
            )
        }
    }
}

@Composable
private fun NotesList(
    notes: List<EncryptedNote>,
    onNoteClick: (EncryptedNote) -> Unit,
    onDeleteNote: (EncryptedNote) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(notes) { note ->
            NoteCard(
                note = note,
                onClick = { onNoteClick(note) },
                onDelete = { onDeleteNote(note) }
            )
        }
    }
}

@Composable
private fun NoteCard(
    note: EncryptedNote,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .clickable(onClick = onClick)
            .padding(OmniToolTheme.spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = note.title.ifEmpty { "Untitled" },
                style = OmniToolTheme.typography.label.copy(fontWeight = FontWeight.Bold),
                color = OmniToolTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = note.content.take(50) + if (note.content.length > 50) "..." else "",
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = dateFormat.format(Date(note.timestamp)),
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.lavender
            )
        }
        
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = OmniToolIcons.Delete,
                contentDescription = "Delete",
                tint = OmniToolTheme.colors.softCoral
            )
        }
    }
}

@Composable
private fun NoteEditor(
    title: String,
    content: String,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OmniToolTheme.colors.background)
            .padding(OmniToolTheme.spacing.md)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancel", color = OmniToolTheme.colors.textMuted)
            }
            Text(
                text = "Edit Note",
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textPrimary
            )
            TextButton(onClick = onSave) {
                Text("Save", color = OmniToolTheme.colors.lavender)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Title
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Title") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OmniToolTheme.colors.lavender,
                cursorColor = OmniToolTheme.colors.lavender
            )
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Content
        OutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            label = { Text("Content") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OmniToolTheme.colors.lavender,
                cursorColor = OmniToolTheme.colors.lavender
            )
        )
    }
}
