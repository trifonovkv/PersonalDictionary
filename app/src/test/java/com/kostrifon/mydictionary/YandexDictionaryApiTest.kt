package com.kostrifon.mydictionary

import kotlinx.coroutines.runBlocking
import org.junit.Test


class YandexDictionaryApiTest {

    private val yandexDictionaryApi = YandexDictionaryApi(BuildConfig.YANDEX_API_KEY)

    @Test
    fun getWord() {
        val requestedWord = RequestedWord("dog", "")
        runBlocking { yandexDictionaryApi.getWord(requestedWord) }.fold(
            onFailure = { assert(false) { it.message?: "Error" } },
            onSuccess = { assert(it.def.isNotEmpty()) }
        )
    }
}
