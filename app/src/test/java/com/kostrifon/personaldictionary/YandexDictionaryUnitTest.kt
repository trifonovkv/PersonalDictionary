package com.kostrifon.personaldictionary

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test


fun isEmpty(yandexDictionaryModel: YandexDictionaryModel): Boolean {
    return yandexDictionaryModel.def.isEmpty()
}


fun printDictionaryWord(yandexDictionaryWord: YandexDictionaryWord) {

    fun printEntry(yandexEntry: YandexEntry) {
        println("pos: ${yandexEntry.pos}")
        println("translations:")
        yandexEntry.translations.forEach { println("\t$it") }
    }

    println("word: ${yandexDictionaryWord.word}")
    yandexDictionaryWord.entries.forEach {
        printEntry(it)
        println("-------------------------------------------------------------")
        println()
    }
}


fun compareYandexDictionary(model: YandexDictionaryModel, word: YandexDictionaryWord): Boolean {

    fun getTranslationsCount(yandexDictionaryModel: YandexDictionaryModel): Int {
        var count = 0
        yandexDictionaryModel.def.forEach { count += it.tr.size }
        return count
    }

    fun getTranslationsCount(yandexDictionaryWord: YandexDictionaryWord): Int {
        var count = 0
        yandexDictionaryWord.entries.forEach { count += it.translations.size }
        return count
    }

    return (getTranslationsCount(model) == getTranslationsCount(word)) == (model.def.size == word.entries.size)
}


fun printAssertError(json: String, yandexDictionaryWord: YandexDictionaryWord) {
    println(json)
    println()
    printDictionaryWord(yandexDictionaryWord)
}


class YandexDictionaryUnitTest {
    private val words = listOf("neglect", "gain", "issue")

    @Test
    fun test() {
        suspend fun testWord(word: String) {
            val json = makeRequest(createClient(), word)
            val yandexDictionaryModel = parseYandexDictionaryModel(json)
            assert(!isEmpty(yandexDictionaryModel)) {
                println("yandexDictionaryModel is empty")
            }
            val yandexDictionaryWord = getYandexDictionaryWord(yandexDictionaryModel)
            assert(compareYandexDictionary(yandexDictionaryModel, yandexDictionaryWord)) {
                printAssertError(json, yandexDictionaryWord)
            }
        }

        words.forEach {
            runBlocking {
                testWord(it)
                delay(3000)
            }
        }
    }
}