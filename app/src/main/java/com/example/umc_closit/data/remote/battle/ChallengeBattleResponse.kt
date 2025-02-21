package com.example.umc_closit.data.remote.battle

import android.os.Parcel
import android.os.Parcelable

data class ChallengeBattleResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ChallengeBattleResult?
)

data class ChallengeBattleResult(
    val challengeBattlePreviewList: List<ChallengeBattlePreview>,
    val listSize: Int,
    val hasNext: Boolean,
    val first: Boolean,
    val last: Boolean
)

data class ChallengeBattlePreview(
    val battleId: Long,
    val firstClositId: String,
    val firstProfileImage: String,
    val firstPostId: Int,
    val firstPostFrontImage: String,
    val firstPostBackImage: String,
    val title: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(battleId)
        parcel.writeString(firstClositId)
        parcel.writeString(firstProfileImage)
        parcel.writeInt(firstPostId)
        parcel.writeString(firstPostFrontImage)
        parcel.writeString(firstPostBackImage)
        parcel.writeString(title)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ChallengeBattlePreview> {
        override fun createFromParcel(parcel: Parcel): ChallengeBattlePreview {
            return ChallengeBattlePreview(parcel)
        }

        override fun newArray(size: Int): Array<ChallengeBattlePreview?> {
            return arrayOfNulls(size)
        }
    }
}

