package ru.alexpronin.simpleintroductiontofrontendandbackend

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    private lateinit var tvOutput: TextView
    private val apiUrl = "https://helper.alexpronin.ru/api-fruits-simple.php" // ← можно заменить на свой сервер

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        tvOutput = findViewById(R.id.tvOutput)
        val btnLoad = findViewById<Button>(R.id.btnLoad)

        btnLoad.setOnClickListener {
            loadFruits()
        }
    }
    private fun loadFruits() {
        // Загружаем данные с сервера в фоновом потоке
        thread {
            try {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val response = connection.inputStream.bufferedReader().readText()
                val jsonObject = JSONObject(response)
                val result = StringBuilder()

                for (fruitName in jsonObject.keys()) {
                    val fruitColor = jsonObject.getString(fruitName)
                    result.append("$fruitName — $fruitColor\n")
                }

                runOnUiThread {
                    tvOutput.text = result.toString()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    tvOutput.text = "Ошибка: ${e.message}"
                }
            }
        }
    }
}