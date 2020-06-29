package com.kostrifon.mydictionary

import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.features.DefaultRequest
import io.ktor.client.response.readText
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.util.*

@Serializable
data class YandexDictionaryModel(val head: Head, val def: List<Def>)

@Serializable
data class Head(val notUsed: String? = null)

@Serializable
data class Def(
    val text: String,
    val pos: String,
    val ts: String? = null,
    val fl: String? = null,
    val tr: List<Tr>
)

@Serializable
data class Tr(
    val text: String,
    val pos: String? = null,
    val num: String? = null,
    val syn: List<Syn>? = null,
    val gen: String? = null,
    val mean: List<Mean>? = null,
    val ex: List<Ex>? = null,
    val asp: String? = null
)

@Serializable
data class Syn(
    val text: String,
    val pos: String? = null,
    val asp: String? = null,
    val gen: String? = null
)

@Serializable
data class Mean(val text: String)

@Serializable
data class Ex(val text: String, val tr: List<Tr>)

data class YandexEntry(val pos: String, val translations: List<String>)
data class YandexDictionaryWord(val word: String, val entries: List<YandexEntry>)


fun getYandexDictionaryWord(yandexDictionaryModel: YandexDictionaryModel): YandexDictionaryWord {
    fun getWord(yandexDictionaryModel: YandexDictionaryModel): String {
        return yandexDictionaryModel.def[0].text
    }

    fun getEntries(yandexDictionaryModel: YandexDictionaryModel): List<YandexEntry> {
        fun getTranslations(tr: List<Tr>): List<String> {
            val translations = mutableListOf<String>()
            tr.forEach {
                translations.add(it.text)
            }
            return translations
        }

        val entries = mutableListOf<YandexEntry>()
        yandexDictionaryModel.def.forEach { def ->
            val translations = getTranslations(def.tr)
            val entry = YandexEntry(def.pos, translations)
            entries.add(entry)
        }
        return entries
    }

    return YandexDictionaryWord(getWord(yandexDictionaryModel), getEntries(yandexDictionaryModel))
}


fun createClient(): HttpClient {
    return HttpClient {
        install(DefaultRequest) {
            headers.append("Accept", "application/json")
        }
    }
}

// not need error and success because it always return json
fun makeRequest(client: HttpClient, requestedWord: String): String {
    return runBlocking {
        val url = ("https://dictionary.yandex.net/api/v1/dicservice.json/lookup?" +
                "key=${BuildConfig.YANDEX_API_KEY}&lang=en-ru&" +
                "text=${requestedWord.toLowerCase(Locale.getDefault())}")
        client.call(url).response.readText()
    }
}


fun parseYandexDictionaryModel(json: String): YandexDictionaryModel {
    return Json(JsonConfiguration.Stable).parse(YandexDictionaryModel.serializer(), json)
}