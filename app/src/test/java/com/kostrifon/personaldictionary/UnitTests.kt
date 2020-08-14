package com.kostrifon.personaldictionary

import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun printOxfordWord(oxfordDictionaryWord: OxfordDictionaryWord) {

    fun printEntry(oxfordEntry: OxfordEntry) {

        fun printPronunciations(pronunciations: List<Pronunciations>) {
            pronunciations.forEach {
                print("\t ${it.phoneticSpelling}")
                println("\t ${it.audioFile}")
            }
        }

        fun printEtymologies(etymologies: List<String>) {
            etymologies.forEach { println("\t $it") }
        }

        println("lexicalCategory: ${oxfordEntry.lexicalCategory}")
        println("pronunciations:")
        oxfordEntry.pronunciations?.let { printPronunciations(it) }
        println("etymologies:")
        oxfordEntry.etymologies?.let { printEtymologies(it) }
    }

    println(oxfordDictionaryWord.word)
    oxfordDictionaryWord.entries.forEach {
        printEntry(it)
        println("---------------------------------------------------------------------------------")
        println()
    }
}


fun compareOxfordDictionary(model: OxfordDictionaryModel, word: OxfordDictionaryWord): Boolean {

    fun getLexicalCategoryCount(oxfordDictionaryModel: OxfordDictionaryModel): Int {
        var lexicalCategoryCount = 0
        oxfordDictionaryModel.results.forEach {
            it.lexicalEntries.forEach { it1 ->
                if (it1.lexicalCategory != null) lexicalCategoryCount++
            }
        }
        return lexicalCategoryCount
    }

    fun getLexicalCategoryCount(oxfordDictionaryWord: OxfordDictionaryWord): Int {
        var lexicalCategoryCount = 0
        oxfordDictionaryWord.entries.forEach {
            if (it.lexicalCategory != null) lexicalCategoryCount++
        }
        return lexicalCategoryCount
    }

    fun getPronunciationsCount(oxfordDictionaryModel: OxfordDictionaryModel): Int {
        var pronunciationsCount = 0
        oxfordDictionaryModel.results.forEach {
            it.lexicalEntries.forEach { it1 ->
                it1.entries?.forEach { it2 ->
                    if (it2.pronunciations != null) pronunciationsCount++
                }
            }
        }
        return pronunciationsCount
    }

    fun getPronunciationsCount(oxfordDictionaryWord: OxfordDictionaryWord): Int {
        var pronunciationsCount = 0
        oxfordDictionaryWord.entries.forEach {
            if (it.pronunciations != null) pronunciationsCount++
        }
        return pronunciationsCount
    }

    fun getEtymologiesCount(oxfordDictionaryModel: OxfordDictionaryModel): Int {
        var etymologiesCount = 0
        oxfordDictionaryModel.results.forEach {
            it.lexicalEntries.forEach { it1 ->
                it1.entries?.forEach { _ -> etymologiesCount++ }
            }
        }
        return etymologiesCount
    }

    fun getEtymologiesCount(oxfordDictionaryWord: OxfordDictionaryWord): Int {
        var etymologiesCount = 0
        repeat(oxfordDictionaryWord.entries.size) { etymologiesCount++ }
        return etymologiesCount
    }

    return (getLexicalCategoryCount(model) == getLexicalCategoryCount(word)) == (
            getPronunciationsCount(model) == getPronunciationsCount(word)) == (
            getEtymologiesCount(model) == getEtymologiesCount(word))
}


fun printAssertError(json: String, oxfordDictionaryWord: OxfordDictionaryWord) {
    println(json)
    println()
    printOxfordWord(oxfordDictionaryWord)
}


fun isEmpty(yandexDictionaryModel: YandexDictionaryModel): Boolean {
    return yandexDictionaryModel.def.isEmpty()
}


fun printYandexWord(yandexDictionaryWord: YandexDictionaryWord) {

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
    printYandexWord(yandexDictionaryWord)
}


class UnitTests {
    private val testDirectory = "/tmp"
    private var testWord = "fire"
    private val words = listOf("neglect", "gain", "issue")

    @KtorExperimentalAPI
    @ExperimentalStdlibApi
    @Test
    fun testDictionaryWord() {
        getTranslatedWord(testWord, { dictionaryWord: DictionaryWord ->
            assert(true)
            printDictionaryWord(dictionaryWord)
            getUniquePronunciations(dictionaryWord).let {
                assert(it.size == it.toSet().size) {
                    println("List of pronunciations contain duplicates")
                }
                println(it.joinToString(separator = "\n"))
                it.map { pronunciation ->
                    val path = testDirectory + "/" + pronunciation.audioFile.substringAfterLast("/")
                    download(pronunciation.audioFile, path)
                }.forEach { file -> assert(file.delete()) { println("${file.path} is't exist") } }
            }
        }, { message: String -> assert(false) { println(message) } }
        )

        // await for coroutines ends
        CountDownLatch(1).await(7, TimeUnit.SECONDS)
    }

    @Test
    fun testOxfordDictionary() {
        val success = { json: String ->
            assert(true)

            val oxfordDictionaryModel = parseOxfordDictionaryModel(json)
            val oxfordDictionaryWord = getOxfordDictionaryWord(oxfordDictionaryModel)

            assert(
                compareOxfordDictionary(
                    oxfordDictionaryModel,
                    oxfordDictionaryWord
                )
            )
            { printAssertError(json, oxfordDictionaryWord) }
        }

        val error = { json: String -> assert(false) { println(json) } }

        runBlocking {
            makeRequest(createOxfordClient(), "neglect", success, error)
        }
    }

    @Test
    fun testYandexDictionary() {
        suspend fun testWord(word: String) {
            val json = makeRequest(createYandexClient(), word)
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