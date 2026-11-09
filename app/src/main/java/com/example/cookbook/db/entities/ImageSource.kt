package com.example.cookbook.db.entities

import android.net.Uri

sealed class ImageSource {
    data class FromUri(val uri: Uri) : ImageSource()
    data class FromString(val urlOrPath: String) : ImageSource()
    data class FromRes(val resId: Int) : ImageSource()
}