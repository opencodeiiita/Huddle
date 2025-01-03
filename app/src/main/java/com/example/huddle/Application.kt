package com.example.huddle

import android.app.Application
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.googlecompat.GoogleCompatEmojiProvider

class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        EmojiManager.install(
            GoogleCompatEmojiProvider(
                EmojiCompat.init(
            FontRequestEmojiCompatConfig(
                this,
                FontRequest(
                    "com.google.android.gms.fonts",
                    "com.google.android.gms",
                    "Noto Color Emoji Compat",
                    R.array.com_google_android_gms_fonts_certs,
                )
            ).setReplaceAll(true)
        )
        )
        )
    }
}