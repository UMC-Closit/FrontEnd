package com.example.umc_closit.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.post.ItemTag
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

object FileUtils {
    fun createImagePart(partName: String, filePath: String): MultipartBody.Part {
        val file = File(filePath)
        if (!file.exists()) {
            throw IllegalArgumentException("파일 경로가 잘못되었습니다: $filePath")
        }
        val requestFile = RequestBody.create("image/*".toMediaType(), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    fun swapImagesWithTags(
        bigImageView: ImageView,
        smallImageView: ImageView
    ) {
        val tempImage = bigImageView.drawable
        val tempTag = bigImageView.tag

        val scaleDownBig = ObjectAnimator.ofFloat(bigImageView, View.SCALE_X, 1f, 0.9f).setDuration(300)
        val scaleDownSmall = ObjectAnimator.ofFloat(smallImageView, View.SCALE_X, 1f, 0.9f).setDuration(300)
        val fadeOutBig = ObjectAnimator.ofFloat(bigImageView, View.ALPHA, 1f, 0f).setDuration(150)
        val fadeOutSmall = ObjectAnimator.ofFloat(smallImageView, View.ALPHA, 1f, 0f).setDuration(150)

        scaleDownBig.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                bigImageView.setImageDrawable(smallImageView.drawable)
                smallImageView.setImageDrawable(tempImage)

                bigImageView.tag = smallImageView.tag
                smallImageView.tag = tempTag

                val scaleUpBig = ObjectAnimator.ofFloat(bigImageView, View.SCALE_X, 0.9f, 1f).setDuration(300)
                val scaleUpSmall = ObjectAnimator.ofFloat(smallImageView, View.SCALE_X, 0.9f, 1f).setDuration(300)
                val fadeInBig = ObjectAnimator.ofFloat(bigImageView, View.ALPHA, 0f, 1f).setDuration(200)
                val fadeInSmall = ObjectAnimator.ofFloat(smallImageView, View.ALPHA, 0f, 1f).setDuration(200)

                AnimatorSet().apply {
                    playTogether(scaleUpBig, scaleUpSmall, fadeInBig, fadeInSmall)
                    start()
                }
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        AnimatorSet().apply {
            playTogether(scaleDownBig, scaleDownSmall, fadeOutBig, fadeOutSmall)
            start()
        }
    }

    fun addItemTags(
        context: Context,
        container: ConstraintLayout,
        imageView: View,
        tags: List<ItemTag>
    ) {
        container.removeAllViews()

        val imageViewWidth = imageView.width.toFloat()
        val imageViewHeight = imageView.height.toFloat()

        for (tag in tags) {
            val tagView = TextView(context).apply {
                text = tag.content
                setTextColor(context.getColor(R.color.white))
                textSize = 14f
                setBackgroundResource(R.drawable.bg_hashtag)
                setPadding(dpToPx(context, 30), dpToPx(context, 8), dpToPx(context, 8), dpToPx(context, 8))
                id = View.generateViewId()
            }

            val layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )

            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.leftMargin = (imageViewWidth * tag.x / 100).toInt()
            layoutParams.topMargin = (imageViewHeight * tag.y / 100).toInt()

            container.addView(tagView, layoutParams)
        }
    }

    fun dpToPx(context: Context, dp: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }


}