package com.example.mydictionary

import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.features.DefaultRequest
import io.ktor.client.response.readText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.lang.Exception
import java.util.*


class OxfordDictionaryApi(private val appId: String, private val appKey: String) {
    private val address = "https://od-api.oxforddictionaries.com:443/api/v2/entries"
    private val language = "en-us"
    private val strictMatch = "false"

    suspend fun getWord(requestedWord: RequestedWord): OxfordDictionaryResponse {
        return try {
                val client = HttpClient {
                    install(DefaultRequest) {
                        headers.append("Accept", "application/json")
                        headers.append("app_id", appId)
                        headers.append("app_key", appKey)
                    }
                }

                val httpResponse = client.call(getUrl(requestedWord)).response

            if (httpResponse.status.value != 200) {
                OxfordDictionaryResponse.Failure(httpResponse.status.description)
            } else {
                val text = httpResponse.use { it.readText() }
                val json = Json(JsonConfiguration.Stable)
                OxfordDictionaryResponse.Success(
                    json.parse(
                        OxfordDictionaryModel.serializer(),
                        text
                    )
                )
            }
        } catch (e: Exception) {
            OxfordDictionaryResponse.Failure(e.message ?: "Unknown error")
        }
    }

    private fun getUrl(requestedWord: RequestedWord): String {
        val wordId = requestedWord.word.toLowerCase(Locale.getDefault())
        return "$address/$language/$wordId?fields=${requestedWord.fields}&strictMatch=$strictMatch"
    }
}

data class RequestedWord(val word: String, val fields: String)

sealed class OxfordDictionaryResponse {
    data class Success(val oxfordDictionaryModel: OxfordDictionaryModel) : OxfordDictionaryResponse()
    data class Failure(val message: String) : OxfordDictionaryResponse()
}
