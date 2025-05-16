package com.example.umc_closit.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
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

    fun swapImagesWithTagEffect(
        bigImageView: ImageView,
        smallImageView: ImageView,
        tagContainer: ConstraintLayout, // 태그도 같이 처리
        onSwapEnd: () -> Unit
    ) {
        val tempImage = bigImageView.drawable
        val tempTag = bigImageView.tag

        // 1. 태그 페이드 아웃
        tagContainer.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction {
                tagContainer.removeAllViews() // 태그 제거

                // 2. 이미지 축소 애니메이션
                val scaleDownBigX = ObjectAnimator.ofFloat(bigImageView, View.SCALE_X, 1f, 0.8f).setDuration(300)
                val scaleDownBigY = ObjectAnimator.ofFloat(bigImageView, View.SCALE_Y, 1f, 0.8f).setDuration(300)

                val scaleDownSmallX = ObjectAnimator.ofFloat(smallImageView, View.SCALE_X, 1f, 0.8f).setDuration(300)
                val scaleDownSmallY = ObjectAnimator.ofFloat(smallImageView, View.SCALE_Y, 1f, 0.8f).setDuration(300)

                // 3. 큰 이미지 페이드 아웃 (자연스럽게)
                val fadeOutBig = ObjectAnimator.ofFloat(bigImageView, View.ALPHA, 1f, 0f).setDuration(200)

                // 4. 축소 -> 스왑 -> 확대 -> 페이드인
                fadeOutBig.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        // 스왑
                        bigImageView.setImageDrawable(smallImageView.drawable)
                        smallImageView.setImageDrawable(tempImage)

                        bigImageView.tag = smallImageView.tag
                        smallImageView.tag = tempTag

                        // 5. 확대 애니메이션
                        val scaleUpBigX = ObjectAnimator.ofFloat(bigImageView, View.SCALE_X, 0.8f, 1f).setDuration(300)
                        val scaleUpBigY = ObjectAnimator.ofFloat(bigImageView, View.SCALE_Y, 0.8f, 1f).setDuration(300)

                        val scaleUpSmallX = ObjectAnimator.ofFloat(smallImageView, View.SCALE_X, 0.8f, 1f).setDuration(300)
                        val scaleUpSmallY = ObjectAnimator.ofFloat(smallImageView, View.SCALE_Y, 0.8f, 1f).setDuration(300)

                        // 6. 큰 이미지 페이드 인
                        val fadeInBig = ObjectAnimator.ofFloat(bigImageView, View.ALPHA, 0f, 1f).setDuration(200)

                        AnimatorSet().apply {
                            playTogether(scaleUpBigX, scaleUpBigY, scaleUpSmallX, scaleUpSmallY, fadeInBig)
                            addListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    onSwapEnd()
                                }
                            })
                            start()
                        }
                    }
                })

                // 7. 최종 실행 (축소 -> 페이드아웃 -> 스왑 -> 확대 + 페이드인)
                AnimatorSet().apply {
                    playTogether(scaleDownBigX, scaleDownBigY, scaleDownSmallX, scaleDownSmallY, fadeOutBig)
                    start()
                }
            }
            .start()
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
            layoutParams.leftMargin = (imageViewWidth * tag.x).toInt()
            layoutParams.topMargin = (imageViewHeight * tag.y).toInt()

            container.addView(tagView, layoutParams)
        }
    }

    fun dpToPx(context: Context, dp: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    fun addItemTagView(
        context: Context,
        container: ConstraintLayout,
        imageView: ImageView,
        tag: ItemTag
    ) {
        val tagView = TextView(context).apply {
            text = tag.content
            setTextColor(Color.WHITE)
            textSize = 14f
            setBackgroundResource(R.drawable.bg_hashtag)
            setPadding(dpToPx(context, 30), dpToPx(context, 8), dpToPx(context, 8), dpToPx(context, 8))
            id = View.generateViewId()
        }

        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        // 1. 이미지 뷰 위치(화면상의 절대 위치) 구하기
        val imageViewLocation = IntArray(2)
        imageView.getLocationOnScreen(imageViewLocation)

        val containerLocation = IntArray(2)
        container.getLocationOnScreen(containerLocation)

        val xOffset = imageViewLocation[0] - containerLocation[0]
        val yOffset = imageViewLocation[1] - containerLocation[1]

        // 2. 태그 위치 비율 적용
        val xPosition = (imageView.width * (tag.x)).toInt() + xOffset
        val yPosition = (imageView.height * (tag.y)).toInt() + yOffset

        layoutParams.leftMargin = xPosition
        layoutParams.topMargin = yPosition

        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID

        container.addView(tagView, layoutParams)
    }



}