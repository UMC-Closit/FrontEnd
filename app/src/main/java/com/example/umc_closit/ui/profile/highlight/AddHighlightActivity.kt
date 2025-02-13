package com.example.umc_closit.ui.profile.highlight

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.umc_closit.R
import com.example.umc_closit.data.entities.TimelineItem
import com.example.umc_closit.databinding.ActivityAddHighlightBinding
import com.example.umc_closit.model.TimelineViewModel

class AddHighlightActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddHighlightBinding
    private val timelineViewModel: TimelineViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHighlightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("AddHighlightActivity", "Activity started")

        // ✅ 공개범위 드롭다운 설정
        setupDropdown()

        // ✅ 클릭한 timelineItemId 가져오기
        val timelineItemId = intent.getIntExtra("timeline_item_id", -1)
        if (timelineItemId == -1) {
            Log.e("AddHighlightActivity", "Invalid timelineItemId received")
            finish()
            return
        }
        Log.d("AddHighlightActivity", "Received timelineItemId: $timelineItemId")

        // ✅ LiveData를 observe하여 해당 ID의 TimelineItem 찾기
        timelineViewModel.timelineItems.observe(this) { timelineItems ->
            val timelineItem = timelineItems.find { it.id == timelineItemId }
            timelineItem?.let { updateUI(it) } ?: Log.e("AddHighlightActivity", "No TimelineItem found for ID: $timelineItemId")
        }

        // ✅ 뒤로가기 버튼 클릭 리스너 설정
        binding.ivBack.setOnClickListener {
            Log.d("AddHighlightActivity", "Back button clicked")
            onBackPressed()  // 뒤로 가기
        }
    }

    private fun setupDropdown() {
        val options = listOf("전체 공개", "친구 공개", "비공개")

        val adapter = ArrayAdapter(this, R.layout.item_dropdown, options)

        val autoCompleteTextView = binding.exposedDropdown
        autoCompleteTextView.setAdapter(adapter)

        // ✅ 클릭 시 드롭다운이 뜨도록 설정
        autoCompleteTextView.setOnClickListener {
            autoCompleteTextView.showDropDown()
        }

        // ✅ 아이템 선택 시 동작
        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            autoCompleteTextView.setText(selectedItem, false)
            Log.d("AddHighlightActivity", "Selected Privacy: $selectedItem")
        }
    }


    private fun updateUI(timelineItem: TimelineItem) {
        Log.d("AddHighlightActivity", "Updating UI with TimelineItem: $timelineItem")

        with(binding) {
            ivImageBig.setImageResource(timelineItem.mainImageResId) // ✅ mainImageResId 적용
            ivImageSmall.setImageResource(timelineItem.overlayImageResId) // ✅ overlayImageResId 적용

            // ✅ 원형 배경 적용 (pointColor 반영)
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor(timelineItem.pointColor))
                setStroke(2, Color.BLACK) // ✅ 테두리 추가
            }
            viewColorCircle.background = drawable

            // ✅ 해시태그 표시
            displayHashtags(timelineItem.hashtags)
        }
    }

    private fun displayHashtags(hashtags: List<String>) {
        Log.d("AddHighlightActivity", "Displaying hashtags: $hashtags")

        val flow = binding.flowHashtagContainer
        val parentLayout = binding.clHashtag
        val ids = mutableListOf<Int>()

        for (hashtag in hashtags) {
            Log.d("AddHighlightActivity", "Creating hashtag view for: $hashtag")
            val textView = createHashtagTextView(hashtag, parentLayout)
            ids.add(textView.id)
        }

        // ✅ Flow에 ID 적용 후 invalidate() 호출
        flow.referencedIds = ids.toIntArray()
        flow.invalidate()
        Log.d("AddHighlightActivity", "Flow updated with new hashtags")
    }

    private fun createHashtagTextView(text: String, parentLayout: ConstraintLayout): TextView {
        Log.d("AddHighlightActivity", "Creating TextView for hashtag: $text")

        val textView = TextView(this).apply {
            id = View.generateViewId()
            this.text = text
            textSize = 16f
            setTextColor(ContextCompat.getColor(context, R.color.white))
            setBackgroundResource(R.drawable.bg_detail_hashtag)
            setPadding(30, 8, 30, 8)
        }

        parentLayout.addView(textView)
        return textView
    }
}
