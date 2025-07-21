package com.example.umc_closit.ui.timeline

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.umc_closit.R
import com.example.umc_closit.data.remote.RetrofitClient
import com.example.umc_closit.data.remote.timeline.BookmarkCreateResponse
import com.example.umc_closit.data.remote.timeline.BookmarkDeleteResponse
import com.example.umc_closit.data.remote.timeline.BookmarkRequest
import com.example.umc_closit.data.remote.timeline.LikeResponse
import com.example.umc_closit.data.remote.timeline.PostPreview
import com.example.umc_closit.databinding.DialogBlockRepBinding
import com.example.umc_closit.databinding.DialogDelModBinding
import com.example.umc_closit.databinding.ItemTimelineBinding
import com.example.umc_closit.ui.profile.ProfileFragment
import com.example.umc_closit.ui.timeline.comment.CommentBottomSheetFragment
import com.example.umc_closit.ui.timeline.detail.DetailActivity
import com.example.umc_closit.data.remote.post.PostDeleteResponse
import com.example.umc_closit.utils.FileUtils
import com.example.umc_closit.utils.TokenUtils

class   TimelineAdapter(
    private val context: Context,
    var timelineItems: MutableList<PostPreview>,
) : RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    private val timelineService = RetrofitClient.timelineService

    class TimelineViewHolder(val binding: ItemTimelineBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val binding = ItemTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimelineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = timelineItems[position] ?: return

        Log.d("TIMELINE_ADAPTER", "🎯 onBindViewHolder 호출 - position: $position")
        Log.d("TIMELINE_ADAPTER", "📊 바인딩할 아이템: $item")

        with(holder.binding) {
            Glide.with(context).load(item.frontImage).into(ivImageBig)
            Glide.with(context).load(item.backImage).into(ivImageSmall)
            Glide.with(context).load(item.profileImage).transform(CircleCrop()).into(ivUserProfile)

            ivLike.setImageResource(if (item.isLiked) R.drawable.ic_like_on else R.drawable.ic_like_off)
            ivSave.setImageResource(if (item.isSaved) R.drawable.ic_save_on else R.drawable.ic_save_off)

            // 좋아요 수 binding
            likeCountNum.text = item.likeCount.toString()

            ivImageBig.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("postId", item.postId)
                Log.d("POST","send postId: ${item.postId}, ${item.isLiked} ${item.isSaved}")
                intent.putExtra("position", position)
                context.startActivity(intent)
            }


            var isFrontImageBig = true


            val fakeTagContainer = ConstraintLayout(context)

            ivImageSmall.setOnClickListener {
                FileUtils.swapImagesWithTagEffect(
                    bigImageView = ivImageBig,
                    smallImageView = ivImageSmall,
                    tagContainer = fakeTagContainer
                ) {
                    isFrontImageBig = !isFrontImageBig
                }
            }



            // TimelineAdapter.kt
            ivComment.setOnClickListener {
                val commentFragment = CommentBottomSheetFragment.newInstance(item.postId)
                commentFragment.show((context as androidx.fragment.app.FragmentActivity).supportFragmentManager, commentFragment.tag)
            }

            ivLike.setOnClickListener {
                if (item.isLiked) {
                    val apiCall = { timelineService.removeLike(item.postId) }
                    TokenUtils.handleTokenRefresh(
                        call = apiCall(),
                        onSuccess = { result: LikeResponse ->
                            if (result.isSuccess) {
                                timelineItems[position] = item.copy(isLiked = false)
                                notifyItemChanged(position)
                            }
                        },
                        onFailure = { t ->
                            Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                        },
                        context = context
                    )
                } else {
                    val apiCall = { timelineService.addLike(item.postId) }
                    TokenUtils.handleTokenRefresh(
                        call = apiCall(),
                        onSuccess = { result: LikeResponse ->
                            if (result.isSuccess) {
                                timelineItems[position] = item.copy(isLiked = true)
                                notifyItemChanged(position)
                            }
                        },
                        onFailure = { t ->
                            Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                        },
                        context = context
                    )
                }
            }

            ivSave.setOnClickListener {
                val newSaveState = !item.isSaved

                if (newSaveState) {
                    val apiCall = {
                        timelineService.addBookmark(BookmarkRequest(item.postId))
                    }

                    TokenUtils.handleTokenRefresh(
                        call = apiCall(),
                        onSuccess = { response: BookmarkCreateResponse ->
                            if (response.isSuccess) {
                                timelineItems[position] = item.copy(isSaved = true)
                                notifyItemChanged(position)
                            } else {
                                Toast.makeText(context, "저장 실패", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onFailure = { t ->
                            Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                        },
                        context = context
                    )

                } else {
                    val apiCall = {
                        timelineService.removeBookmark(item.postId)
                    }

                    TokenUtils.handleTokenRefresh(
                        call = apiCall(),
                        onSuccess = { response: BookmarkDeleteResponse ->
                            if (response.isSuccess) {
                                timelineItems[position] = item.copy(isSaved = false)
                                notifyItemChanged(position)
                            } else {
                                Toast.makeText(context, "저장 취소 실패", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onFailure = { t ->
                            Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT)
                                .show()
                        },
                        context = context
                    )
                }
            }

//            ivOption.setOnClickListener {
//                val myClositId = TokenUtils.getClositId(context)
//                val isMyPost = (item.clositId == myClositId)
//                if (isMyPost) {
//                    showDeleteModifyDialog(context, item.postId) {
//                        timelineItems.removeAt(position)
//                        notifyItemRemoved(position)
//                    }
//            }

            ivUserProfile.setOnClickListener {
                val activity = context as? androidx.fragment.app.FragmentActivity
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container, ProfileFragment().apply {
                        arguments = Bundle().apply {
                            putString("profileUserClositId", item.clositId)
                        }
                    })
                    ?.addToBackStack(null)
                    ?.commit()
            }

//            ivOption.setOnClickListener {
//                val myClositId = TokenUtils.getClositId(context) //나의 Id
//                val isMyPost = (item.clositId == myClositId)
//
//                if (isMyPost) {
//                    showDeleteModifyDialog(context)
//                }
//                else {
//                    showBlockReportDialog(context)
//                }
//            }

        }
    }

    fun updateTimelineItems(updatedItems: List<PostPreview>) {
        Log.d("TIMELINE_ADAPTER", "🔄 어댑터 데이터 업데이트")
        Log.d("TIMELINE_ADAPTER", "📊 업데이트할 아이템 수: ${updatedItems.size}")
        Log.d("TIMELINE_ADAPTER", "📊 기존 아이템 수: ${this.timelineItems.size}")
        
        this.timelineItems.clear()
        this.timelineItems.addAll(updatedItems)
        notifyDataSetChanged()
        
        Log.d("TIMELINE_ADAPTER", "✅ 어댑터 업데이트 완료 - 총 아이템 수: ${this.timelineItems.size}")
    }

    //삭제 수정 dialog 표시
    fun showDeleteModifyDialog(context: Context) {
        val dialog = Dialog(context)
        val binding = DialogDelModBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명
        dialog.show()

        // 예시: 삭제 버튼 클릭 리스너
        binding.deleteButton.setOnClickListener {
            // 삭제 로직
            dialog.dismiss()
        }
        // 예시: 수정 버튼 클릭 리스너
        binding.modifyButton.setOnClickListener {
            // 수정 로직
            dialog.dismiss()
        }
    }


    //차단, 신고 dialog 표시
    fun showBlockReportDialog(context: Context) {
        val dialog = Dialog(context)
        val binding = DialogBlockRepBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        // 예시: 차단 버튼 클릭 리스너
        binding.blockButton.setOnClickListener {
            // 차단 보내기 로직
            dialog.dismiss()
        }
        // 예시: 신고 버튼 클릭 리스너
        binding.reportButton.setOnClickListener {
            // 신고 로직
            dialog.dismiss()
        }
    }
//    fun deletePost(context: Context, postId: Int, onSuccess: () -> Unit) {
//        val apiCall = { RetrofitClient.postService.deletePost(postId) }
//        TokenUtils.handleTokenRefresh(
//            call = apiCall(),
//            onSuccess = { response ->
//                if (response.isSuccess) {
//                    Toast.makeText(context, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
//                    onSuccess()
//                } else {
//                    Toast.makeText(context, "삭제 실패: ${response.message}", Toast.LENGTH_SHORT).show()
//                }
//            },
//            onFailure = { t ->
//                Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
//            },
//            retryCall = apiCall,
//            context = context
//        )
//    }





    override fun getItemCount(): Int = timelineItems.size
}
