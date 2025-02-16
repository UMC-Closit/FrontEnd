package com.example.umc_closit.data.entities.post

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class DeletePostResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: @RawValue Map<String, Any> = emptyMap()
) : Parcelable
