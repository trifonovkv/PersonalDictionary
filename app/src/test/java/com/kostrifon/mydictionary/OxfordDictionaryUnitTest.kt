package com.kostrifon.mydictionary

import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.features.DefaultRequest
import io.ktor.client.response.readText
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.junit.Test

import java.util.*

fun makeRequest(
    requestedWord: String,
    success: (json: String) -> Unit,
    error: (json: String) -> Unit
) {
    val client = HttpClient {
        install(DefaultRequest) {
            headers.append("Accept", "application/json")
            headers.append("app_id", BuildConfig.OXFORD_APP_ID)
            headers.append("app_key", BuildConfig.OXFORD_APP_KEY)
        }
    }

    val wordId = requestedWord.toLowerCase(Locale.getDefault())

    runBlocking {
        val httpResponse = client.call(
            "https://od-api.oxforddictionaries.com:443/api/v2/entries/en-us/" +
                    "$wordId?fields=pronunciations,etymologies&strictMatch=false"
        ).response

        val text = httpResponse.readText()
        when (httpResponse.status.value) {
            200, 201 -> success(text)
            else -> error(text)
        }
    }
}

fun parseOxfordDictionaryModel(json: String): OxfordDictionaryModel {
    return Json(JsonConfiguration.Stable).parse(OxfordDictionaryModel.serializer(), json)
}

class OxfordDictionaryUnitTest {

    @Test
    fun test() {
        val success = { json: String ->
            assert(true)
            println(parseOxfordDictionaryModel(json))
        }
        val error = { json: String ->
            assert(false)
            println(json)
        }

        makeRequest("cum", success, error)
    }
}
