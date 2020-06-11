package com.kostrifon.mydictionary

import kotlinx.coroutines.runBlocking
import org.junit.Test


class OxfordDictionaryApiTest {

    private val oxfordDictionaryApi =
        OxfordDictionaryApi(
            BuildConfig.OXFORD_APP_ID,
            BuildConfig.OXFORD_APP_KEY
        )

    @Test
    fun oxfordDictionaryApi() {
        val requestedWord = RequestedWord(
            "enter",
            "pronunciations,etymologies"
        )
        runBlocking { oxfordDictionaryApi.getWord(requestedWord) }.fold(
            onSuccess = {
                assert(it.results.isNotEmpty())
                println(it.results)
            },
            onFailure = { assert(false) { it.localizedMessage?: "Error" } }
        )
    }
}