package com.example.umc_closit.model

import androidx.lifecycle.*
import com.example.umc_closit.data.remote.post.ItemTag
import com.example.umc_closit.data.remote.post.PostUploadResponse
import com.example.umc_closit.data.remote.post.PostService
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.utils.FileUtils
import com.example.umc_closit.utils.JsonUtils
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostViewModel : ViewModel() {

    private val postService: PostService = RetrofitClient.postService

    // 업로드 결과를 위한 LiveData
    private val _uploadResult = MutableLiveData<Result<PostUploadResponse>>()
    val uploadResult: LiveData<Result<PostUploadResponse>> = _uploadResult

    fun uploadPost(
        requestBody: RequestBody,
        frontImagePart: MultipartBody.Part,
        backImagePart: MultipartBody.Part
    ) {
        viewModelScope.launch {
            try {
                // API 호출
                val response = postService.uploadPost(
                    request = requestBody,
                    frontImage = frontImagePart,
                    backImage = backImagePart
                )

                // 응답 처리
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _uploadResult.postValue(Result.success(response.body()!!))
                    println("업로드 성공: ${response.body()?.result?.postId}")
                } else {
                    _uploadResult.postValue(
                        Result.failure(Exception("업로드 실패: ${response.body()?.message ?: response.message()}"))
                    )
                }
            } catch (e: Exception) {
                _uploadResult.postValue(Result.failure(e))
                println("에러 발생: ${e.message}")
            }
        }
    }
}

    /*
    // Multipart 업로드 함수
    fun uploadPost(
        frontImage: MultipartBody.Part,
        backImage: MultipartBody.Part,
        hashtags: List<String>,
        frontItemtags: List<ItemTag>,
        backItemtags: List<ItemTag>,
        pointColor: String,
        visibility: String,
        mission: Boolean
    ) {
        viewModelScope.launch {
            try {
                val response = postService.uploadPost(
                    frontImage = frontImage,
                    backImage = backImage,
                    hashtags = hashtags,
                    frontItemtags = frontItemtags,
                    backItemtags = backItemtags,
                    pointColor = pointColor,
                    visibility = visibility,
                    mission = mission
                )
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _uploadResult.postValue(Result.success(response.body()!!))
                } else {
                    _uploadResult.postValue(
                        Result.failure(
                            Exception("업로드 실패: ${response.body()?.message ?: response.message()}")
                        )
                    )
                }
            } catch (e: Exception) {
                _uploadResult.postValue(Result.failure(e))
            }
        }
    }
}

     */
