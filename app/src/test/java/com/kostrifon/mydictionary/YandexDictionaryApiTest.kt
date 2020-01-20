package com.kostrifon.mydictionary

import YandexDictionaryApi
import YandexDictionaryResponse
import kotlinx.coroutines.runBlocking
import org.junit.Test


class YandexDictionaryApiTest {

    private val yandexDictionaryApi = YandexDictionaryApi(BuildConfig.YANDEX_API_KEY)

    @Test
    fun getWord() {
        val requestedWord = "dog"
        when(val yandexDictionaryResponse = runBlocking { yandexDictionaryApi.getWord(requestedWord) }) {
            is YandexDictionaryResponse.Success -> {
                assert(yandexDictionaryResponse.yandexDictionaryModel.def.isNotEmpty())
            }
            is YandexDictionaryResponse.Failure -> {
                assert(false) { yandexDictionaryResponse.message }
            }
        }
    }
}
