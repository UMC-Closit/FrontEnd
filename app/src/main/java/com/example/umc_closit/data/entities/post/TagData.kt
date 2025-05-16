package com.example.umc_closit.data.entities.post

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TagData(
    val xRatio: Float,
    val yRatio: Float,
    val tagText: String
) : Parcelable
