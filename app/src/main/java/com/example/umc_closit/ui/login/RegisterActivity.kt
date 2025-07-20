package com.example.umc_closit.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.umc_closit.R
import com.example.umc_closit.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 약관 내용 불러오기
        loadTerms()

        // 다음 버튼 클릭 이벤트
        binding.btnNext.setOnClickListener {
            val intent = Intent(this, MakeIdActivity::class.java)
            startActivity(intent)
        }

        // 뒤로가기 버튼 클릭 이벤트
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        // 초기 버튼 비활성화 & 회색 설정
        binding.btnNext.isEnabled = false
        binding.btnNext.backgroundTintList = ContextCompat.getColorStateList(this, R.color.gray_dark)

        setupCheckListeners()
    }

    private lateinit var allCheckListener: CompoundButton.OnCheckedChangeListener

    private fun setupCheckListeners() {
        val checkBoxes = listOf(binding.chkRegister1, binding.chkRegister2, binding.chkRegister3)

        val checkListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            validateChecks()
        }

        allCheckListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            checkBoxes.forEach { it.isChecked = isChecked }
        }

        checkBoxes.forEach { it.setOnCheckedChangeListener(checkListener) }

        binding.chkRegisterAll.setOnCheckedChangeListener(allCheckListener)
    }

    private fun validateChecks() {
        val allRequiredChecked = binding.chkRegister1.isChecked && binding.chkRegister2.isChecked
        val allChecked = allRequiredChecked && binding.chkRegister3.isChecked

        binding.btnNext.isEnabled = allRequiredChecked

        val colorRes = if (allRequiredChecked) R.color.black else R.color.gray_dark
        binding.btnNext.backgroundTintList = ContextCompat.getColorStateList(this, colorRes)

        // 모든 약관 동의 체크박스 상태 변경
        binding.chkRegisterAll.setOnCheckedChangeListener(null) // 무한루프 방지
        binding.chkRegisterAll.isChecked = allChecked
        binding.chkRegisterAll.setOnCheckedChangeListener(allCheckListener)
    }



    // assets 폴더에서 약관 텍스트 읽어와서 표시하는 함수
    private fun loadTerms() {
        binding.txtRegister1.text = loadTextFromAssets("terms_of_service.txt")
        binding.txtRegister2.text = loadTextFromAssets("privacy_policy.txt")
        binding.txtRegister3.text = loadTextFromAssets("marketing_agreement.txt")
    }

    private fun loadTextFromAssets(fileName: String): String {
        return assets.open(fileName).bufferedReader().use { it.readText() }
    }
}
