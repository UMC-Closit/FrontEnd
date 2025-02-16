package com.example.umc_closit.ui.mission

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.mission.utils.RotateBitmap.rotateBitmapIfNeeded
import com.example.umc_closit.data.entities.post.TagData
import com.example.umc_closit.databinding.ActivityBackOnlyBinding
import com.example.umc_closit.ui.mission.FrontOnlyActivity.Companion
import com.example.umc_closit.ui.timeline.TimelineActivity

class BackOnlyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBackOnlyBinding
    private var originalBitmapPath: String? = null
    private var originalBitmap: Bitmap? = null

    private var tvPrivacyStatus: TextView? = null  // 공개범위 TextView

    private var hashtags: ArrayList<String> = arrayListOf()
    private var pointColor: Int = -1
    private var frontTagList: ArrayList<TagData>? = null



    companion object {
        private const val TAGGING_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hashtags = intent.getStringArrayListExtra("hashtags") ?: arrayListOf()
        pointColor = intent.getIntExtra("pointColor", -1)
        frontTagList = intent.getParcelableArrayListExtra("frontTagList") ?: arrayListOf()

        binding = ActivityBackOnlyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            finish()
        }

        val backPhotoPath = intent.getStringExtra("backPhotoPath")
        backPhotoPath?.let { path ->
            originalBitmap = rotateBitmapIfNeeded(path)
            originalBitmap?.let { bmp ->
                binding.imageViewBackOnly.setImageBitmap(bmp)
            }
        }

        // Upload 보낼 frontPhotoPath
        val frontPhotoPath = intent.getStringExtra("frontPhotoPath")

        var isColorExtractMode = false

        if (pointColor != -1) {
            setIconColor(binding.viewColorIcon, pointColor)
        }

        if (hashtags.isNotEmpty()) {
            hashtags.forEach { hashtag ->
                addHashtagToContainer(hashtag)
            }
        }

        binding.viewColorIcon.setOnClickListener {
            isColorExtractMode = !isColorExtractMode
        }

        binding.imageViewBackOnly.setOnTouchListener { _, event ->
            if (isColorExtractMode) {
                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    originalBitmap?.let { bmp ->
                        val color = getTouchedColor(bmp, event.x, event.y)
                        setIconColor(binding.viewColorIcon, color)
                    }
                    isColorExtractMode = false
                }
            } else {
                if (event.action == MotionEvent.ACTION_UP) {
                    val intent = Intent(this, TaggingActivity::class.java).apply {
                        putExtra("photoPath", backPhotoPath)
                    }
                    startActivityForResult(intent, TAGGING_REQUEST_CODE)
                }
            }
            true
        }

        // 해시태그 버튼 클릭
        binding.btnHashtag.setOnClickListener {
            showHashtagDialog(
                currentHashtag = null,
                onHashtagSaved = { newHashtag ->
                    addHashtagToContainer(newHashtag)
                }
            )
        }

        binding.btnPrivacy.setOnClickListener {
            showPrivacyOptions(it)
        }

        binding.btnUpload.setOnClickListener {
            Toast.makeText(this, "미션 완료!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, TimelineActivity::class.java)
            intent.putExtra("showUploadFragment", true)
            startActivity(intent)
        }
    }

    // 드롭다운 메뉴를 표시하는 함수
    private fun showPrivacyOptions(view: View) {
        // PopupMenu 객체 생성
        val popupMenu = PopupMenu(this, view)

        // 메뉴 항목을 추가 (전체공개, 친구공개, 나만보기)
        popupMenu.menuInflater.inflate(com.example.umc_closit.R.menu.privacy_menu, popupMenu.menu)

        // 메뉴 아이템 클릭 시 동작 설정
        popupMenu.setOnMenuItemClickListener { item ->
            handlePrivacyOptionSelection(item)
            true
        }

        // 드롭다운 메뉴 표시
        popupMenu.show()
    }

    // 선택된 공개범위 옵션 처리
    private fun handlePrivacyOptionSelection(item: MenuItem) {
        when (item.itemId) {
            com.example.umc_closit.R.id.menu_public -> {
                tvPrivacyStatus?.text = "전체공개"
                binding.btnPrivacy.setImageResource(com.example.umc_closit.R.drawable.ic_public) // 전체공개 아이콘으로 변경
            }
            com.example.umc_closit.R.id.menu_friends -> {
                tvPrivacyStatus?.text = "친구공개"
                binding.btnPrivacy.setImageResource(com.example.umc_closit.R.drawable.ic_friends) // 친구공개 아이콘으로 변경
            }
            com.example.umc_closit.R.id.menu_private -> {
                tvPrivacyStatus?.text = "나만보기"
                binding.btnPrivacy.setImageResource(com.example.umc_closit.R.drawable.ic_private) // 나만보기 아이콘으로 변경
            }
        }
    }

    private fun addHashtagToContainer(hashtag: String) {
        val hashtagView = android.widget.TextView(this).apply {
            text = "#$hashtag"
            setBackgroundResource(com.example.umc_closit.R.drawable.bg_detail_hashtag)
            setTextColor(resources.getColor(android.R.color.white))
            setPadding(10, 10, 10, 10)
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 10
                marginEnd = 10
            }
        }
        binding.hashtagContainer.addView(hashtagView)
    }

    private fun showHashtagDialog(
        currentHashtag: String?,
        onHashtagSaved: (String) -> Unit
    ) {
        val editText = android.widget.EditText(this).apply {
            hint = "#해시태그 입력"
            currentHashtag?.let { setText(it) }
        }

        AlertDialog.Builder(this)
            .setTitle("해시태그 ${if (currentHashtag == null) "입력" else "수정"}")
            .setView(editText)
            .setPositiveButton("확인") { _: DialogInterface, _: Int ->
                val input = editText.text.toString()
                if (input.isNotBlank()) {
                    onHashtagSaved(input)
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    // 아이콘 색상 변경
    private fun setIconColor(view: android.view.View, color: Int) {
        val bg = view.background
        if (bg is GradientDrawable) {
            bg.setColor(color)
        } else {
            view.setBackgroundColor(color)
        }
    }

    // 이미지에서 색상 추출
    private fun getTouchedColor(bitmap: Bitmap, touchX: Float, touchY: Float): Int {
        val ivWidth = binding.imageViewBackOnly.width
        val ivHeight = binding.imageViewBackOnly.height

        val bmpWidth = bitmap.width
        val bmpHeight = bitmap.height

        val xRatio = touchX / ivWidth
        val yRatio = touchY / ivHeight

        val pixelX = (xRatio * bmpWidth).toInt().coerceIn(0, bmpWidth - 1)
        val pixelY = (yRatio * bmpHeight).toInt().coerceIn(0, bmpHeight - 1)

        return bitmap.getPixel(pixelX, pixelY)
    }

    private fun addTagView(tagText: String, xRatio: Float, yRatio: Float) {
        val tagView = android.widget.TextView(this).apply {
            text = tagText
            setTextColor(Color.WHITE)
            textSize = 14f
            setBackgroundResource(com.example.umc_closit.R.drawable.bg_hashtag)
            val leftPad = dpToPx(30)
            val pad = dpToPx(8)
            setPadding(leftPad, pad, pad, pad)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BackOnlyActivity.TAGGING_REQUEST_CODE && resultCode == RESULT_OK) {
            val tagList = data?.getParcelableArrayListExtra<TagData>("tagList")
            if (tagList != null) {
                for (tag in tagList) {
                    addTagView(tag.tagText, tag.xRatio, tag.yRatio)
                }
            }

        }
    }
}