package com.example.umc_closit.ui.mission

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.mission.utils.RotateBitmap.rotateBitmapIfNeeded
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.post.TagData
import com.example.umc_closit.databinding.ActivityFrontOnlyBinding
import com.example.umc_closit.databinding.CustomTagDialogBinding


class FrontOnlyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFrontOnlyBinding

    private var frontPhotoPath: String? = null
    private var backPhotoPath: String? = null

    private val hashtags = mutableListOf<String>()

    private var originalBitmap: Bitmap? = null

    var pointColor: Int? = null

    var ifTagged = false

    // 새 태그 데이터를 저장할 멤버 변수 추가
    private var receivedTagList: ArrayList<TagData>? = null

    companion object {
        private const val TAGGING_REQUEST_CODE = 1001
    }

    private fun getDisplayTag(fullTag: String): String {
        return if (fullTag.length > 7) fullTag.substring(0, 7) + "..." else fullTag
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFrontOnlyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바 뒤로가기 버튼 설정
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        frontPhotoPath = intent.getStringExtra("frontPhotoPath")
        frontPhotoPath?.let { path ->
            originalBitmap = rotateBitmapIfNeeded(path)
            originalBitmap?.let { bmp ->
                binding.imageViewFrontOnly.setImageBitmap(bmp)
            }
        }

        backPhotoPath = intent.getStringExtra("backPhotoPath")

        var isColorExtractMode = false

        binding.viewColorIcon.setOnClickListener {
            isColorExtractMode = !isColorExtractMode
        }

        binding.addItem.setOnClickListener{
            val intent = Intent(this, TaggingActivity::class.java).apply {
                putExtra("photoPath", frontPhotoPath)
            }
            startActivityForResult(intent, TAGGING_REQUEST_CODE)
        }

        binding.imageViewFrontOnly.setOnTouchListener { view, event ->
            if (isColorExtractMode) {
                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    originalBitmap?.let { bmp ->
                        val color = getTouchedColor(bmp, event.x, event.y)
                        setIconColor(binding.viewColorIcon, color)
                    }
                }
            }
            true
        }


        binding.btnHashtag.setOnClickListener {
            showHashtagDialog { newHashtag ->
                hashtags.add(newHashtag)
                createHashtagTextView(newHashtag, binding.clHashtag, binding.flowHashtagContainer)
            }
        }

        // BackOnlyActivity로 이동
        binding.btnContinue.setOnClickListener {
            val intent = Intent(this, BackOnlyActivity::class.java).apply {
                putExtra("frontPhotoPath", frontPhotoPath)
                putExtra("backPhotoPath", backPhotoPath)

                if (ifTagged) {
                    putParcelableArrayListExtra("frontTagList", receivedTagList ?: arrayListOf())
                }
                putStringArrayListExtra("hashtags", ArrayList(hashtags))

                //  포인트 색상 전달 (없으면 기본값 -1)
                putExtra("pointColor", pointColor ?: -1)

            }
            startActivity(intent)
        }
    }

    // 해시태그 입력 다이얼로그
    private fun showHashtagDialog(onHashtagSaved: (String) -> Unit) {
        // 다이얼로그 생성
        val dialog = Dialog(this)
        val binding = CustomTagDialogBinding.inflate(layoutInflater)

        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명화

        // 기본값으로 '#' 추가
        binding.etHashtag.setText("#")
        binding.etHashtag.setSelection(1) // 커서를 # 뒤로 이동

        // 취소 버튼
        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        // 확인 버튼
        binding.btnConfirm.setOnClickListener {
            val input = binding.etHashtag.text.toString().trim()

            // 입력 검증
            if (input.length > 1 && input.startsWith("#")) {
                onHashtagSaved(input)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "올바른 해시태그를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    // 아이콘 색상 변경
    private fun setIconColor(view: android.view.View, color: Int) {
        val bg = view.background
        if (bg is GradientDrawable) {
            bg.setColor(color)
            pointColor = color
        } else {
            view.setBackgroundColor(color)
        }
    }

    // 이미지에서 색상 추출
    private fun getTouchedColor(bitmap: Bitmap, touchX: Float, touchY: Float): Int {
        val ivWidth = binding.imageViewFrontOnly.width
        val ivHeight = binding.imageViewFrontOnly.height

        val bmpWidth = bitmap.width
        val bmpHeight = bitmap.height

        val xRatio = touchX / ivWidth
        val yRatio = touchY / ivHeight

        val pixelX = (xRatio * bmpWidth).toInt().coerceIn(0, bmpWidth - 1)
        val pixelY = (yRatio * bmpHeight).toInt().coerceIn(0, bmpHeight - 1)

        return bitmap.getPixel(pixelX, pixelY)
    }

    private fun addTagView(tagText: String, xRatio: Float, yRatio: Float) {
        val displayTag = getDisplayTag(tagText) // ✅ display tag 사용

        val tagView = android.widget.TextView(this).apply {
            text = displayTag
            setTextColor(Color.WHITE)
            textSize = 14f
            setBackgroundResource(com.example.umc_closit.R.drawable.bg_hashtag)
            val leftPad = dpToPx(30)
            val pad = dpToPx(8)
            setPadding(leftPad, pad, pad, pad)

            // 전체 태그를 클릭하면 Toast로 보여주기 (선택 사항)
            setOnClickListener {
                Toast.makeText(context, "전체 태그: $tagText", Toast.LENGTH_SHORT).show()
            }
        }

        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        val parentWidth = binding.imageAndTag.width.toFloat()
        val parentHeight = binding.imageAndTag.height.toFloat()

        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.leftMargin = (parentWidth * xRatio).toInt()
        layoutParams.topMargin = (parentHeight * yRatio).toInt()

        binding.imageAndTag.addView(tagView, layoutParams)
    }

    private fun dpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    private fun createHashtagTextView(text: String, parentLayout: ConstraintLayout, flow: Flow) {

        val font: Typeface? = ResourcesCompat.getFont(this, R.font.pretendard_regular)
        val textView = TextView(this).apply {
            id = View.generateViewId()
            this.text = text
            textSize = 16f
            typeface = ResourcesCompat.getFont(context, R.font.noto_medium)
            includeFontPadding = false
            setTextColor(ContextCompat.getColor(context, R.color.white))
            setBackgroundResource(R.drawable.bg_detail_hashtag)
            setPadding(36, 12, 36, 12)

            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )

            // TODO: 해시태그 클릭 시 삭제 기능 추가
            // setOnClickListener {}
        }

        parentLayout.addView(textView)
        flow.referencedIds += textView.id
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAGGING_REQUEST_CODE && resultCode == RESULT_OK) {

            val tagList = data?.getParcelableArrayListExtra<TagData>("tagList")
            if (tagList != null) {
                ifTagged = true
                receivedTagList = tagList
                for (tag in tagList) {
                    addTagView(tag.tagText, tag.xRatio, tag.yRatio)
                }
            }

        }
    }
}