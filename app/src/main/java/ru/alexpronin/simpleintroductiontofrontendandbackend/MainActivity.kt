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
    /*
    Если вы запускаете приложение в эмуляторе и хотите обратиться к локальному серверу на хост-машине, то 127.0.0.1 в эмуляторе указывает на сам эмулятор, а не на ваш компьютер. В этом случае используйте IP-адрес 10.0.2.2 — это специальный адрес, который эмулятор перенаправляет на хост-машину.
    Использование HTTP (незащищённого трафика) небезопасно — данные могут быть перехвачены или изменены злоумышленниками. Рекомендуется использовать HTTPS, если это возможно.
    (При использовании только HTTPS, рекомендую удалить android:usesCleartextTraffic="true" из файла AndroidManifest.xml)
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Находим элементы по ID из activity_main.xml
            // напиши здесь свой код tvOutput = _____
            // напиши здесь свой код val btnLoad = _____
        // Устанавливаем обработчик клика по кнопке "Загрузить"
            // напиши здесь свой код btnLoad._____
    }
    private fun loadFruits() {
        /*
        Загружаем данные с сервера в фоновом потоке
        (запускаем сетевой запрос в отдельном потоке, чтобы не тормозить интерфейс)
        */
        thread {
            try {
                // Создаём URL-объект по строке адреса сервера
                val url = URL(apiUrl)

                // Открываем HTTP-соединение с сервером
                val connection = url.openConnection() as HttpURLConnection

                // Устанавливаем метод запроса GET
                connection.requestMethod = "GET"

                // Выполняем подключение (отправка запроса)
                connection.connect()

                // Читаем весь ответ от сервера как одну строку
                val response = connection.inputStream.bufferedReader().readText()

                // Преобразуем строку-ответ в JSON-объект
                val jsonObject = JSONObject(response)

                // Создаём StringBuilder для накопления строк результата
                val result = StringBuilder()

                // Перебираем все ключи в JSON (например, "apple", "banana" и т.п.)
                for (fruitName in jsonObject.keys()) {
                    // Получаем значение по ключу (например, цвет фрукта)
                    val fruitColor = jsonObject.getString(fruitName)

                    // Добавляем строку вида "apple — red" в результат
                    result.append("$fruitName — $fruitColor\n")
                }

                // Обновляем интерфейс из главного потока
                runOnUiThread {
                    // Отображаем результат в TextView
                    tvOutput.text = result.toString()
                }

            } catch (e: Exception) {
                // Если произошла ошибка (например, нет сети или некорректный JSON)
                runOnUiThread {
                    // Показываем сообщение об ошибке в TextView
                    tvOutput.text = "Ошибка: ${e.message}"
                }
            }
        }
    }
}