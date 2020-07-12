package com.kostrifon.mydictionary

import org.junit.Test


fun printDictionaryWord(dictionaryWord: DictionaryWord) {

    fun dictionaryEntryToString(dictionaryEntry: DictionaryEntry): String {
        var string = ""

        fun etymologiesToString(etymologies: List<String>): String {
            if (etymologies.isEmpty()) return ""
            var s = "\t\tEtymologies:\n"
            etymologies.forEach { s = s.plus("\t\t\t${it}\n") }
            return s
        }

        fun pronunciationsToString(
            pronunciations: List<Pronunciation>
        ): String {
            if (pronunciations.isEmpty()) return ""
            var s = "\t\tPronunciations:\n"
            pronunciations.forEach {
                s = s.plus(
                    "\t\t\t${it.phoneticSpelling} ${it.audioFile}\n"
                )
            }
            return s
        }

        fun translatesToString(translates: List<String>) =
            if (translates.isEmpty()) {
                ""
            } else {
                "\t\tTranslates:\n".plus(
                    "\t\t\t${translates.joinToString()}\n"
                )
            }

        string = string.plus(translatesToString(dictionaryEntry.translates))
        string = string.plus(
            pronunciationsToString(
                dictionaryEntry.pronunciations
            )
        )
        string = string.plus(etymologiesToString(dictionaryEntry.etymologies))

        return string
    }

    println("Word: ${dictionaryWord.word}")
    dictionaryEntryToString(dictionaryWord.noun).let {
        if (it.isNotBlank()) println("\tNoun:\n $it")
    }
    dictionaryEntryToString(dictionaryWord.verb).let {
        if (it.isNotBlank()) println("\tVerb:\n $it")
    }
    dictionaryEntryToString(dictionaryWord.adjective).let {
        if (it.isNotBlank()) println("\tAdjective:\n $it")
    }
    println()
}


class DictionaryWordUnitTest {

    private var testWord = "abandon"

    private fun getOxfordWord(
        word: String,
        success: (word: OxfordDictionaryWord) -> Unit,
        error: (json: String) -> Unit
    ) {

        makeRequest(
            createClient(BuildConfig.OXFORD_APP_ID, BuildConfig.OXFORD_APP_KEY),
            word,
            { json: String ->
                success(
                    getOxfordDictionaryWord(parseOxfordDictionaryModel(json))
                )
            },
            error
        )
    }

    private fun getYandexWord(
        word: String,
        success: (word: YandexDictionaryWord) -> Unit,
        error: (json: String) -> Unit
    ) {
        val json = makeRequest(createClient(), word)
        val yandexDictionaryModel = parseYandexDictionaryModel(json)
        if (yandexDictionaryModel.def.isEmpty()) {
            error(json)
        } else {
            success(getYandexDictionaryWord(yandexDictionaryModel))
        }
    }

    @ExperimentalStdlibApi
    @Test
    fun isNotEmpty() {
        val oxfordSuccess = { oxfordWord: OxfordDictionaryWord ->
            val yandexSuccess = { yandexWord: YandexDictionaryWord ->
                assert(true)
                val dictionaryWord = getDictionaryWord(oxfordWord, yandexWord)
                printDictionaryWord(dictionaryWord)
                println()
            }
            val yandexError = { json: String ->
                assert(false) {
                    println(json)
                }
            }
            assert(true)
            getYandexWord(testWord, yandexSuccess, yandexError)
        }

        val oxfordError = { json: String ->
            assert(false) { println(json) }
        }
        getOxfordWord(testWord, oxfordSuccess, oxfordError)
    }
}