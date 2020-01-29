package com.kostrifon.mydictionary

import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.features.DefaultRequest
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.util.*

class YandexDictionaryApi(private val apiKey: String):DictionaryApi<YandexDictionaryModel> {

    private val address = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup"
    private val lang = "en-ru"

    override suspend fun getWord(requestedWord: RequestedWord): Result<YandexDictionaryModel> {
        return try {
            val httpResponse = getResponse(requestedWord.word)
            if (httpResponse.status.value != 200) {
                Result.failure(Exception(httpResponse.status.description))
            } else {
                val text = httpResponse.use { it.readText() }
                val json = Json(JsonConfiguration.Stable)
                Result.success(json.parse(YandexDictionaryModel.serializer(), text))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getResponse(requestedWord: String): HttpResponse {
        val client = HttpClient {
            install(DefaultRequest) {
                headers.append("Accept", "application/json")
            }
        }
        return client.call(getUrl(requestedWord, apiKey)).response
    }

    private fun getUrl(requestedWord: String, apiKey: String): String {
        val wordId = requestedWord.toLowerCase(Locale.getDefault())
        return "$address?key=$apiKey&lang=$lang&text=$wordId"
    }
}
