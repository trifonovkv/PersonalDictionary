import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.features.DefaultRequest
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.util.*

class YandexDictionaryApi(private val apiKey: String) {

    private val address = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup"
    private val lang = "en-ru"

    suspend fun getWord(requestedWord: String): YandexDictionaryResponse {
        return try {
            val httpResponse = getResponse(requestedWord)
            if (httpResponse.status.value != 200) {
                YandexDictionaryResponse.Failure(httpResponse.status.description)
            } else {
                val text = httpResponse.readText()
                val json = Json(JsonConfiguration.Stable)
                YandexDictionaryResponse.Success(json.parse(YandexDictionaryModel.serializer(), text))
            }
        } catch (e: Exception) {
            YandexDictionaryResponse.Failure(e.message ?: "Unknown error")
        }
    }

    private suspend fun getResponse(requestedWord: String): HttpResponse {
        val client = HttpClient {
            install(DefaultRequest) {
                headers.append("Accept", "application/json")
            }
        }
        val response = client.call(getUrl(requestedWord, apiKey)).response
        client.close()
        return response
    }

    private fun getUrl(requestedWord: String, apiKey: String): String {
        val wordId = requestedWord.toLowerCase(Locale.getDefault())
        return "$address?key=$apiKey&lang=$lang&text=$wordId"
    }
}

sealed class YandexDictionaryResponse {
    data class Success(val yandexDictionaryModel: YandexDictionaryModel) : YandexDictionaryResponse()
    data class Failure(val message: String) : YandexDictionaryResponse()
}

