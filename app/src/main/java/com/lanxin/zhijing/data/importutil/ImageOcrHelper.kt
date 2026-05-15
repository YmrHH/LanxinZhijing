package com.lanxin.zhijing.data.importutil

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageOcrHelper {

    private val recognizer by lazy {
        TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
    }

    /**
     * 使用 ML Kit 中文模型做设备端 OCR；需 Google Play 服务分载模型，识别失败返回 null。
     */
    suspend fun recognizeText(context: Context, uri: Uri): String? =
        withContext(Dispatchers.IO) {
            runCatching {
                val bitmap = context.contentResolver.openInputStream(uri)?.use { inp ->
                    BitmapFactory.decodeStream(inp)
                } ?: return@withContext null
                val image = InputImage.fromBitmap(bitmap, 0)
                val result = Tasks.await(recognizer.process(image))
                result.text.trim().takeIf { it.isNotEmpty() }?.take(200_000)
            }.getOrNull()
        }
}
