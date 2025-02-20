package com.example.umc_closit.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.widget.ImageView
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

    fun swapImagesWithShrinkEffect(
        bigImageView: ImageView,
        smallImageView: ImageView
    ) {
        val tempImage = bigImageView.drawable
        val tempTag = bigImageView.tag

        val scaleDownBigX = ObjectAnimator.ofFloat(bigImageView, View.SCALE_X, 1f, 0.1f).setDuration(300)
        val scaleDownBigY = ObjectAnimator.ofFloat(bigImageView, View.SCALE_Y, 1f, 0.1f).setDuration(300)
        val fadeOutBig = ObjectAnimator.ofFloat(bigImageView, View.ALPHA, 1f, 0f).setDuration(150)

        val scaleDownSmallX = ObjectAnimator.ofFloat(smallImageView, View.SCALE_X, 1f, 0.1f).setDuration(300)
        val scaleDownSmallY = ObjectAnimator.ofFloat(smallImageView, View.SCALE_Y, 1f, 0.1f).setDuration(300)
        val fadeOutSmall = ObjectAnimator.ofFloat(smallImageView, View.ALPHA, 1f, 0f).setDuration(150)

        scaleDownBigX.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                bigImageView.setImageDrawable(smallImageView.drawable)
                smallImageView.setImageDrawable(tempImage)

                bigImageView.tag = smallImageView.tag
                smallImageView.tag = tempTag

                val scaleUpBigX = ObjectAnimator.ofFloat(bigImageView, View.SCALE_X, 0.1f, 1f).setDuration(300)
                val scaleUpBigY = ObjectAnimator.ofFloat(bigImageView, View.SCALE_Y, 0.1f, 1f).setDuration(300)
                val fadeInBig = ObjectAnimator.ofFloat(bigImageView, View.ALPHA, 0f, 1f).setDuration(200)

                val scaleUpSmallX = ObjectAnimator.ofFloat(smallImageView, View.SCALE_X, 0.1f, 1f).setDuration(300)
                val scaleUpSmallY = ObjectAnimator.ofFloat(smallImageView, View.SCALE_Y, 0.1f, 1f).setDuration(300)
                val fadeInSmall = ObjectAnimator.ofFloat(smallImageView, View.ALPHA, 0f, 1f).setDuration(200)

                AnimatorSet().apply {
                    playTogether(scaleUpBigX, scaleUpBigY, fadeInBig, scaleUpSmallX, scaleUpSmallY, fadeInSmall)
                    start()
                }
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        AnimatorSet().apply {
            playTogether(scaleDownBigX, scaleDownBigY, fadeOutBig, scaleDownSmallX, scaleDownSmallY, fadeOutSmall)
            start()
        }
    }


}