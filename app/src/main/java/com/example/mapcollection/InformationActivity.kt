package com.example.mapcollection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class InformationActivity : AppCompatActivity() {

    private lateinit var tvLocName: TextView
    private lateinit var btnBack: Button
    private lateinit var btnAskAI: Button
    private lateinit var btnNearbySpots: Button
    private lateinit var btnAddToTrip: Button

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_information)

        tvLocName = findViewById(R.id.tvLocName)
        btnBack = findViewById(R.id.btnBack)
        btnAskAI = findViewById(R.id.button)
        btnNearbySpots = findViewById(R.id.button2)
        btnAddToTrip = findViewById(R.id.button3)

        latitude = intent.getDoubleExtra("latitude", 0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)

        // 建議的寫法，避免 'Do not concatenate' 警告
        tvLocName.text = "座標: $latitude, $longitude"

        btnBack.setOnClickListener {
            finish()
        }

        btnAskAI.setOnClickListener {
            showAskAIDialog()
        }

        btnNearbySpots.setOnClickListener {
            findNearbyAttractions()
        }

        btnAddToTrip.setOnClickListener {
            // TODO: 實現加入行程功能
        }
    }

    private fun showAskAIDialog() {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("詢問AI")
            .setMessage("請輸入您想詢問關於這個地點的問題：")
            .setView(editText)
            .setPositiveButton("送出") { dialog, _ ->
                val question = editText.text.toString()
                if (question.isNotEmpty()) {
                    askGemini(question)
                }
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun askGemini(question: String) {
        val generativeModel = GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = "AIzaSyDJlVFbRgLbjVkj-LL7XJIRCP57td6OjNE" // <--- 請替換成您自己的金鑰
        )

        val prompt = "關於地點座標 ($latitude, $longitude)，我想知道：$question"

        lifecycleScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)
                showResultDialog("AI 的回答", response.text ?: "無法取得回應")
            } catch (e: Exception) {
                showResultDialog("錯誤", "發生錯誤：${e.localizedMessage}")
            }
        }
    }

    private fun findNearbyAttractions() {1
        val generativeModel = GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = "AIzaSyDJlVFbRgLbjVkj-LL7XJIRCP57td6OjNE" // <--- 請替換成您自己的金鑰
        )

        val prompt = "請推薦在座標 ($latitude, $longitude) 附近的5個景點，並簡單介紹每個景點。"

        lifecycleScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)
                showResultDialog("附近景點推薦", response.text ?: "無法取得回應")
            } catch (e: Exception) {
                showResultDialog("錯誤", "發生錯誤：${e.localizedMessage}")
            }
        }
    }

    private fun showResultDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("關閉", null)
            .show()
    }
}