package com.example.huddle.utility

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

fun getTimeAgo(timeInMillis: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timeInMillis

    return when {
        diff < 60 * 1000 -> "Active Recently"
        diff < 60 * 60 * 1000 -> "Last seen ${diff / (60 * 1000)}m ago"
        diff < 24 * 60 * 60 * 1000 -> "Last seen ${diff / (60 * 60 * 1000)}h ago"
        diff < 7 * 24 * 60 * 60 * 1000 -> "Last seen ${diff / (24 * 60 * 60 * 1000)}d ago"
        else -> {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            "Last seen on ${dateFormat.format(timeInMillis)}"
        }
    }
}

fun decodeBase64ToBitmap(base64Image: String): Bitmap {
    val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}