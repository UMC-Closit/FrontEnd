package com.example.umc_closit.ui.mission

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
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
import com.example.umc_closit.data.remote.post.TagData
import com.example.umc_closit.databinding.ActivityBackOnlyBinding
import com.example.umc_closit.ui.mission.FrontOnlyActivity.Companion
import com.example.umc_closit.ui.timeline.TimelineActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.umc_closit.data.remote.post.PostRequest
import com.example.umc_closit.data.remote.post.ItemTag
import com.example.umc_closit.data.remote.post.PostService
import com.example.umc_closit.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.umc_closit.model.PostViewModel
import com.example.umc_closit.utils.FileUtils
import com.example.umc_closit.utils.JsonUtils

class BackOnlyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBackOnlyBinding
    private var originalBitmapPath: String? = null
    private var originalBitmap: Bitmap? = null

    private var tvPrivacyStatus: TextView? = null  // 공개범위 TextView

    private var hashtags: ArrayList<String> = arrayListOf()
    private var pointColor: Int = -1
    private var frontTagList: ArrayList<TagData>? = null

    private var backTagList: ArrayList<TagData>? = null

    private lateinit var postService: PostService

    private val viewModel: PostViewModel by viewModels()

    companion object {
        private const val TAGGING_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackOnlyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postService = RetrofitClient.postService

        hashtags = intent.getStringArrayListExtra("hashtags") ?: arrayListOf()
        pointColor = intent.getIntExtra("pointColor", -1)
        frontTagList = intent.getParcelableArrayListExtra("frontTagList") ?: arrayListOf()

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

        // ViewModel의 업로드 결과 관찰
        viewModel.uploadResult.observe(this, Observer { result ->
            result.onSuccess { response ->
                Toast.makeText(this, "게시글 업로드 성공! ID: ${response.result.postId}", Toast.LENGTH_SHORT).show()
                // 업로드 성공 시 타임라인으로 이동
                val intent = Intent(this, TimelineActivity::class.java)
                intent.putExtra("showUploadFragment", true)
                startActivity(intent)
                finish()
            }.onFailure { error ->
                Toast.makeText(this, "업로드 실패: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })

        binding.btnUpload.setOnClickListener {
            val frontImagePart = FileUtils.createImagePart("frontImage", frontPhotoPath ?: "")
            val backImagePart = FileUtils.createImagePart("backImage", backPhotoPath ?: "")

            val frontItemtags = frontTagList?.map { tag ->
                ItemTag(
                    x = (tag.xRatio * 100).toInt(),
                    y = (tag.yRatio * 100).toInt(),
                    content = tag.tagText
                )
            } ?: emptyList()

            val backItemtags = backTagList?.map { tag ->
                ItemTag(
                    x = (tag.xRatio * 100).toInt(),
                    y = (tag.yRatio * 100).toInt(),
                    content = tag.tagText
                )
            } ?: emptyList()

            val visibility = when (tvPrivacyStatus?.text?.toString()) {
                "전체공개" -> "PUBLIC"
                "친구공개" -> "FRIENDS"
                "나만보기" -> "PRIVATE"
                else -> "PUBLIC"
            }

            val requestObject = mapOf(
                "hashtags" to hashtags,
                "frontItemtags" to frontItemtags,
                "backItemtags" to backItemtags,
                "pointColor" to "#${Integer.toHexString(pointColor)}",
                "visibility" to visibility,
                "mission" to true
            )

            val requestBody = JsonUtils.createRequestBody(requestObject)

            viewModel.uploadPost(
                requestBody = requestBody,
                frontImagePart = frontImagePart,
                backImagePart = backImagePart
            )
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

        /*
        binding.btnUpload.setOnClickListener {
            uploadPost()
            Toast.makeText(this, "미션 완료!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, TimelineActivity::class.java)
            intent.putExtra("showUploadFragment", true)
            startActivity(intent)
        }

         */
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

    private fun createMultipart(partName: String, filePath: String): MultipartBody.Part {
        val file = File(filePath)
        val requestFile = RequestBody.create("image/*".toMediaType(), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    /*
    private fun uploadPost() {
        // visibility 가져오기
        val visibility = when (tvPrivacyStatus?.text?.toString()) {
            "전체공개" -> "PUBLIC"
            "친구공개" -> "FRIENDS"
            "나만보기" -> "PRIVATE"
            else -> "PUBLIC"
        }


        val frontPhotoPath = intent.getStringExtra("frontPhotoPath")
        val backPhotoPath = intent.getStringExtra("backPhotoPath")
        Log.d("Upload", "Front Image Path: $frontPhotoPath")
        Log.d("Upload", "Back Image Path: $backPhotoPath")

        // Multipart 요청을 위한 Part 생성
        val frontImagePart = createMultipart("frontImage", frontPhotoPath ?: "")
        val backImagePart = createMultipart("backImage", backPhotoPath ?: "")

        val hashtagsParts = hashtags
        val pointColorString = "#${Integer.toHexString(pointColor)}"

        // frontTagList와 backTagList를 ItemTag 리스트로 변환
        val frontItemtags = frontTagList?.map { tag ->
            ItemTag(x = (tag.xRatio * 100).toInt(), y = (tag.yRatio * 100).toInt(), content = tag.tagText)
        } ?: emptyList()

        val backItemtags = backTagList?.map { tag ->
            ItemTag(x = (tag.xRatio * 100).toInt(), y = (tag.yRatio * 100).toInt(), content = tag.tagText)
        } ?: emptyList()

        // CoroutineScope로 API 호출
        lifecycleScope.launch {
            try {
                val response = postService.uploadPost(
                    frontImage = frontImagePart,
                    backImage = backImagePart,
                    hashtags = hashtagsParts,
                    frontItemtags = frontItemtags,
                    backItemtags = backItemtags,
                    pointColor = pointColorString,
                    visibility = visibility,
                    mission = true
                )
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    Toast.makeText(this@BackOnlyActivity, "게시글 업로드 성공!", Toast.LENGTH_SHORT).show()

                    // 타임라인 화면으로 이동
                    val intent = Intent(this@BackOnlyActivity, TimelineActivity::class.java)
                    intent.putExtra("showUploadFragment", true)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@BackOnlyActivity, "업로드 실패: ${response.body()?.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BackOnlyActivity, "네트워크 오류: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        /*
        // API 요청 데이터 생성
        val postRequest = PostRequest(
            frontImage = frontPhotoPath ?: "",
            backImage = backPhotoPath ?: "",
            hashtags = hashtags,
            frontItemtags = frontItemtags,
            backItemtags = backItemtags,
            pointColor = "#${Integer.toHexString(pointColor)}",
            visibility = visibility,
            mission = true
        )

        // API 호출
        postService.uploadPost(postRequest).enqueue(object : Callback<PostUploadResponse> {
            override fun onResponse(
                call: Call<PostUploadResponse>,
                response: Response<PostUploadResponse>
            ) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    Toast.makeText(this@BackOnlyActivity, "게시글 업로드 성공!", Toast.LENGTH_SHORT).show()

                    // 타임라인 화면으로 이동
                    val intent = Intent(this@BackOnlyActivity, TimelineActivity::class.java)
                    intent.putExtra("showUploadFragment", true)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@BackOnlyActivity, "업로드 실패: ${response.body()?.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<PostUploadResponse>, t: Throwable) {
                Toast.makeText(this@BackOnlyActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })

         */
    }

     */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BackOnlyActivity.TAGGING_REQUEST_CODE && resultCode == RESULT_OK) {
            val tagList = data?.getParcelableArrayListExtra<TagData>("tagList")
            if (tagList != null) {
                backTagList = tagList
                for (tag in tagList) {
                    addTagView(tag.tagText, tag.xRatio, tag.yRatio)
                }
            }

        }
    }
}