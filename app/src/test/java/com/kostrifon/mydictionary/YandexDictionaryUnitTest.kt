package com.kostrifon.mydictionary

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test


fun printAssertError(json: String, yandexDictionaryWord: YandexDictionaryWord) {
    println(json)
    println()
    printDictionaryWord(yandexDictionaryWord)
}


class YandexDictionaryUnitTest {
    private val words = listOf(
        "neglect",
        "gain",
        "issue"
    )

    @Test
    fun test() {
        fun testWord(word: String) {
            val json = makeRequest(createClient(), word)
            val yandexDictionaryModel = parseYandexDictionaryModel(json)
            assert(!isEmpty(yandexDictionaryModel)) { println("yandexDictionaryModel is empty") }
            val yandexDictionaryWord = getYandexDictionaryWord(yandexDictionaryModel)
            assert(compareYandexDictionary(yandexDictionaryModel, yandexDictionaryWord)) {
                printAssertError(json, yandexDictionaryWord)
            }
        }

        words.forEach {
            testWord(it)
            runBlocking {
                delay(3000)
            }
        }
    }
}

