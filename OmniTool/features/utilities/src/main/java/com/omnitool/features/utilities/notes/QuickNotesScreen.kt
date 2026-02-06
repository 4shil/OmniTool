package com.omnitool.features.utilities.notes

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omnitool.core.theme.OmniToolTheme
import com.omnitool.ui.icons.OmniToolIcons
import com.omnitool.ui.screens.ToolWorkspaceScreen

/**
 * Quick Notes Screen
 * 
 * Features:
 * - Note list view
 * - Editor view
 * - Search bar
 * - Delete with swipe
 */
@Composable
fun QuickNotesScreen(
    onBack: () -> Unit,
    viewModel: QuickNotesViewModel = hiltViewModel()
) {
    if (viewModel.isEditing) {
        NoteEditor(
            title = viewModel.editTitle,
            content = viewModel.editContent,
            onTitleChange = { viewModel.updateTitle(it) },
            onContentChange = { viewModel.updateContent(it) },
            onSave = { viewModel.saveNote() },
            onCancel = { viewModel.cancelEdit() },
            isNew = viewModel.currentNote == null
        )
    } else {
        ToolWorkspaceScreen(
            toolName = "Quick Notes",
            toolIcon = OmniToolIcons.Notes,
            accentColor = OmniToolTheme.colors.warmYellow,
            onBack = onBack,
            inputContent = {
                // Search bar
                SearchBar(
                    query = viewModel.searchQuery,
                    onQueryChange = { viewModel.updateSearch(it) }
                )
            },
            outputContent = {
                // Notes list
                NotesList(
                    notes = viewModel.filteredNotes,
                    onNoteClick = { viewModel.editNote(it) },
                    onDeleteNote = { viewModel.deleteNote(it) },
                    formatDate = { viewModel.formatDate(it) }
                )
            },
            primaryActionLabel = "New Note",
            onPrimaryAction = { viewModel.createNewNote() }
        )
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search notes...") },
        leadingIcon = {
            Icon(
                imageVector = OmniToolIcons.Search,
                contentDescription = null,
                tint = OmniToolTheme.colors.textMuted
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = OmniToolIcons.Clear,
                        contentDescription = "Clear",
                        tint = OmniToolTheme.colors.textMuted
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = OmniToolTheme.colors.warmYellow,
            unfocusedBorderColor = OmniToolTheme.colors.surface,
            cursorColor = OmniToolTheme.colors.warmYellow,
            focusedTextColor = OmniToolTheme.colors.textPrimary,
            unfocusedTextColor = OmniToolTheme.colors.textPrimary
        ),
        singleLine = true
    )
}

@Composable
private fun NotesList(
    notes: List<QuickNote>,
    onNoteClick: (QuickNote) -> Unit,
    onDeleteNote: (QuickNote) -> Unit,
    formatDate: (Long) -> String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.medium)
            .background(OmniToolTheme.colors.elevatedSurface)
            .padding(OmniToolTheme.spacing.sm)
    ) {
        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = OmniToolIcons.Notes,
                        contentDescription = null,
                        tint = OmniToolTheme.colors.textMuted.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No notes yet",
                        style = OmniToolTheme.typography.body,
                        color = OmniToolTheme.colors.textMuted
                    )
                    Text(
                        text = "Tap 'New Note' to create one",
                        style = OmniToolTheme.typography.caption,
                        color = OmniToolTheme.colors.textMuted
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                items(notes, key = { it.id }) { note ->
                    NoteItem(
                        note = note,
                        onClick = { onNoteClick(note) },
                        onDelete = { onDeleteNote(note) },
                        formatDate = formatDate
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteItem(
    note: QuickNote,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    formatDate: (Long) -> String
) {
    var showDelete by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(OmniToolTheme.shapes.small)
            .background(OmniToolTheme.colors.surface)
            .clickable(onClick = onClick)
            .padding(OmniToolTheme.spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = note.title,
                style = OmniToolTheme.typography.label,
                color = OmniToolTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            if (note.content != note.title) {
                Text(
                    text = note.content.replace("\n", " "),
                    style = OmniToolTheme.typography.caption,
                    color = OmniToolTheme.colors.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Text(
                text = formatDate(note.updatedAt),
                style = OmniToolTheme.typography.caption,
                color = OmniToolTheme.colors.textMuted.copy(alpha = 0.7f)
            )
        }
        
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = OmniToolIcons.Delete,
                contentDescription = "Delete",
                tint = OmniToolTheme.colors.softCoral,
                modifier = Modifier.size(18.dp)
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
    onCancel: () -> Unit,
    isNew: Boolean
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
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = OmniToolIcons.ArrowBack,
                    contentDescription = "Back",
                    tint = OmniToolTheme.colors.textPrimary
                )
            }
            
            Text(
                text = if (isNew) "New Note" else "Edit Note",
                style = OmniToolTheme.typography.header,
                color = OmniToolTheme.colors.textPrimary
            )
            
            TextButton(onClick = onSave) {
                Text(
                    text = "Save",
                    color = OmniToolTheme.colors.warmYellow
                )
            }
        }
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.md))
        
        // Title input
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = { Text("Title (optional)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OmniToolTheme.colors.warmYellow,
                unfocusedBorderColor = OmniToolTheme.colors.surface,
                cursorColor = OmniToolTheme.colors.warmYellow,
                focusedTextColor = OmniToolTheme.colors.textPrimary,
                unfocusedTextColor = OmniToolTheme.colors.textPrimary
            ),
            textStyle = OmniToolTheme.typography.header,
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.sm))
        
        // Content input
        OutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            placeholder = { Text("Start writing...") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OmniToolTheme.colors.warmYellow,
                unfocusedBorderColor = OmniToolTheme.colors.surface,
                cursorColor = OmniToolTheme.colors.warmYellow,
                focusedTextColor = OmniToolTheme.colors.textPrimary,
                unfocusedTextColor = OmniToolTheme.colors.textPrimary
            )
        )
        
        Spacer(modifier = Modifier.height(OmniToolTheme.spacing.sm))
        
        // Character count
        Text(
            text = "${content.length} characters • ${content.split("\\s+".toRegex()).filter { it.isNotBlank() }.size} words",
            style = OmniToolTheme.typography.caption,
            color = OmniToolTheme.colors.textMuted
        )
    }
}
