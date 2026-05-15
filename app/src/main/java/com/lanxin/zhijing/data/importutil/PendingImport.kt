package com.lanxin.zhijing.data.importutil

import com.lanxin.zhijing.data.ai.ImportSource

data class PendingImport(
    val title: String,
    val body: String,
    val source: ImportSource,
    val fileUri: String? = null,
    val imageUri: String? = null
)
