package com.kostrifon.mydictionary

import kotlinx.coroutines.runBlocking
import org.junit.Test


fun printDictionaryWord(oxfordDictionaryWord: OxfordDictionaryWord) {

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
    printDictionaryWord(oxfordDictionaryWord)
}


class OxfordDictionaryUnitTest {

    @Test
    fun test() {
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
            makeRequest(
                createClient(
                    BuildConfig.OXFORD_APP_ID, BuildConfig.OXFORD_APP_KEY
                ), "neglect", success, error
            )
        }
    }
}
