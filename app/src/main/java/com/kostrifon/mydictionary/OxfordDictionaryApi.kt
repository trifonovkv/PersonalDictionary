package com.kostrifon.mydictionary

import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.features.DefaultRequest
import io.ktor.client.response.readText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.lang.Exception
import java.util.*


class OxfordDictionaryApi(private val appId: String, private val appKey: String) :
    DictionaryApi<OxfordDictionaryModel> {
    private val address = "https://od-api.oxforddictionaries.com:443/api/v2/entries"
    private val language = "en-us"
    private val strictMatch = "false"

    override suspend fun getWord(requestedWord: RequestedWord): Result<OxfordDictionaryModel> {
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
                Result.failure(Exception(httpResponse.status.description))
            } else {
                val text = httpResponse.use { it.readText() }
                val json = Json(JsonConfiguration.Stable)
                Result.success(json.parse(OxfordDictionaryModel.serializer(), text))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getUrl(requestedWord: RequestedWord): String {
        val wordId = requestedWord.word.toLowerCase(Locale.getDefault())
        return "$address/$language/$wordId?fields=${requestedWord.fields}&strictMatch=$strictMatch"
    }
}