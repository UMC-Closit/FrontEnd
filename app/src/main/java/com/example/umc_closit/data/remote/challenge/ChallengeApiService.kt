import com.example.umc_closit.data.remote.challenge.ChallengeRequest
import com.example.umc_closit.data.remote.challenge.ChallengeResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

// 챌린지 API 인터페이스
interface ChallengeApiService {
    @POST("/api/auth/communities/battle/challenge/upload/{battle_id}")
    fun uploadChallenge(
        @Header("Authorization") token: String,  // 인증 헤더 추가
        @Path("battle_id") battleId: Int,        // 배틀 ID 경로 변수
        @Body requestBody: ChallengeRequest      // 요청 바디
    ): Call<ChallengeResponse>
}
