package com.example.umc_closit.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.umc_closit.R

object HashtagUtils {

    fun displayHashtags(
        context: Context,
        hashtags: List<String>,
        flow: Flow,
        parentLayout: ConstraintLayout
    ) {
        Log.d("HashtagUtils", "Displaying hashtags: $hashtags")

        val ids = mutableListOf<Int>()

        for (hashtag in hashtags) {
            Log.d("HashtagUtils", "Creating hashtag view for: $hashtag")
            val textView = createHashtagTextView(context, hashtag, parentLayout)
            ids.add(textView.id)
        }

        flow.referencedIds = ids.toIntArray()
        flow.invalidate()
    }

    private fun createHashtagTextView(
        context: Context,
        text: String,
        parentLayout: ConstraintLayout
    ): TextView {

        val textView = TextView(context).apply {
            id = View.generateViewId()
            this.text = text
            textSize = 16f
            typeface = ResourcesCompat.getFont(context, R.font.noto_medium)
            includeFontPadding = false
            setTextColor(ContextCompat.getColor(context, R.color.white))
            setBackgroundResource(R.drawable.bg_detail_hashtag)
            setPadding(36, 12, 36, 12)
        }


        parentLayout.addView(textView)
        return textView
    }
}
