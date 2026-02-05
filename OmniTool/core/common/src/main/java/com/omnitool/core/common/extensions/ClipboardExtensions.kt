package com.omnitool.core.common.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/**
 * Clipboard extensions
 */
fun Context.copyToClipboard(text: String, label: String = "OmniTool") {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}

fun Context.getClipboardText(): String? {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    return clipboard.primaryClip?.getItemAt(0)?.text?.toString()
}
