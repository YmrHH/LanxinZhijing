package com.lanxin.zhijing.data.importutil

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlin.text.Charsets

object TextImportHelper {
    private const val MAX_BYTES = 512 * 1024

    fun readUri(context: Context, uri: Uri): String? =
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val bytes = input.readBytes()
                if (bytes.size > MAX_BYTES) return@use null
                String(bytes, Charsets.UTF_8)
            }
        } catch (_: Exception) {
            null
        }

    fun queryDisplayName(context: Context, uri: Uri): String? =
        try {
            context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { c ->
                if (c.moveToFirst()) c.getString(0) else null
            }
        } catch (_: Exception) {
            null
        }
}
