package com.kostrifon.personaldictionary

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


data class Pronunciation(val phoneticSpelling: String, val audioFile: String) : java.io.Serializable

data class DictionaryEntry(
    val translates: List<String>,
    val pronunciations: List<Pronunciation>,
    val etymologies: List<String>
) : java.io.Serializable

data class DictionaryWord(
    val word: String,
    val noun: DictionaryEntry,
    val verb: DictionaryEntry,
    val adjective: DictionaryEntry
) : java.io.Serializable

@ExperimentalStdlibApi
fun getDictionaryWord(
    oxfordDictionaryWord: OxfordDictionaryWord,
    yandexDictionaryWord: YandexDictionaryWord
): DictionaryWord {

    fun getDictionaryEntry(lexicalCategory: String): DictionaryEntry {
        fun getTranslations(lexicalCategory: String): List<String> {
            yandexDictionaryWord.entries.forEach {
                if (it.pos == lexicalCategory) {
                    return it.translations
                }
            }
            return emptyList()
        }

        fun getPronunciations(lexicalCategory: String): List<Pronunciation> {
            fun getPronunciation(pronunciations: Pronunciations): Pronunciation {
                return Pronunciation(
                    pronunciations.phoneticSpelling ?: "", pronunciations.audioFile ?: ""
                )
            }

            val pronunciations = mutableListOf<Pronunciation>()
            oxfordDictionaryWord.entries.forEach { oxfordEntry ->
                if (oxfordEntry.lexicalCategory == lexicalCategory) {
                    oxfordEntry.pronunciations?.forEach {
                        pronunciations.add(getPronunciation(it))
                    }
                }
            }
            return pronunciations
        }

        fun getEtymologies(lexicalCategory: String): List<String> {
            oxfordDictionaryWord.entries.forEach {
                if (it.lexicalCategory == lexicalCategory) {
                    return it.etymologies ?: emptyList()
                }
            }
            return emptyList()
        }

        return DictionaryEntry(
            getTranslations(lexicalCategory),
            getPronunciations(lexicalCategory.capitalize(Locale.getDefault())),
            getEtymologies(lexicalCategory.capitalize(Locale.getDefault()))
        )
    }

    return DictionaryWord(
        oxfordDictionaryWord.word ?: yandexDictionaryWord.word,
        getDictionaryEntry("noun"),
        getDictionaryEntry("verb"),
        getDictionaryEntry("adjective")
    )
}

@ExperimentalStdlibApi
fun getTranslatedWord(word: String, success: (word: DictionaryWord) -> Unit, error: (message: String) -> Unit) {

    suspend fun getOxfordWord(
        word: String, success: (word: OxfordDictionaryWord) -> Unit, error: (json: String) -> Unit
    ) {
        makeRequest(
            createOxfordClient(),
            word,
            { json: String -> success(getOxfordDictionaryWord(parseOxfordDictionaryModel(json))) },
            error
        )
    }

    suspend fun getYandexWord(
        word: String,
        success: (word: YandexDictionaryWord) -> Unit,
        error: (json: String) -> Unit
    ) {
        val json = makeRequest(createYandexClient(), word)
        val yandexDictionaryModel = parseYandexDictionaryModel(json)
        if (yandexDictionaryModel.def.isEmpty()) {
            error(json)
        } else {
            success(getYandexDictionaryWord(yandexDictionaryModel))
        }
    }

    var oxfordDictionaryWord: OxfordDictionaryWord? = null
    var yandexDictionaryWord: YandexDictionaryWord? = null
    var oxfordErrorJson: String? = null
    var yandexErrorJson: String? = null

    val endCallback = {
        if (oxfordDictionaryWord != null && yandexDictionaryWord != null)
            success(getDictionaryWord(oxfordDictionaryWord!!, yandexDictionaryWord!!))
        // return only oxford error message because yandex doesn't send error message
        if (oxfordErrorJson != null && yandexErrorJson != null) error(oxfordErrorJson ?: "null")
        if (oxfordErrorJson != null && yandexDictionaryWord != null) error(oxfordErrorJson ?: "null")
        if (oxfordDictionaryWord != null && yandexErrorJson != null) error("Yandex error")
    }

    GlobalScope.launch {
        getOxfordWord(word,
            { oxfordWord ->
                oxfordDictionaryWord = oxfordWord
                endCallback()
            }, { json ->
                oxfordErrorJson = json
                endCallback()
            })
    }

    GlobalScope.launch {
        getYandexWord(word,
            { yandexWord ->
                yandexDictionaryWord = yandexWord
                endCallback()
            }, { json ->
                yandexErrorJson = json
                endCallback()
            })
    }
}

fun getUniquePronunciations(dictionaryWord: DictionaryWord) = listOf(
    dictionaryWord.noun.pronunciations, dictionaryWord.verb.pronunciations, dictionaryWord.adjective.pronunciations
).flatten().filter { it.audioFile.isNotBlank() }.toSet().toList()

fun printDictionaryWord(dictionaryWord: DictionaryWord) {

    fun dictionaryEntryToString(dictionaryEntry: DictionaryEntry): String {
        var string = ""

        fun etymologiesToString(etymologies: List<String>): String {
            if (etymologies.isEmpty()) return ""
            var s = "\t\tEtymologies:\n"
            etymologies.forEach { s = s.plus("\t\t\t${it}\n") }
            return s
        }

        fun pronunciationsToString(pronunciations: List<Pronunciation>): String {
            if (pronunciations.isEmpty()) return ""
            var s = "\t\tPronunciations:\n"
            pronunciations.forEach { s = s.plus("\t\t\t${it.phoneticSpelling} ${it.audioFile}\n") }
            return s
        }

        fun translatesToString(translates: List<String>) =
            if (translates.isEmpty()) {
                ""
            } else {
                "\t\tTranslates:\n".plus("\t\t\t${translates.joinToString()}\n")
            }

        string = string.plus(translatesToString(dictionaryEntry.translates))
        string = string.plus(pronunciationsToString(dictionaryEntry.pronunciations))
        string = string.plus(etymologiesToString(dictionaryEntry.etymologies))

        return string
    }

    println("Word: ${dictionaryWord.word}")
    dictionaryEntryToString(dictionaryWord.noun).let { if (it.isNotBlank()) println("\tNoun:\n $it") }
    dictionaryEntryToString(dictionaryWord.verb).let { if (it.isNotBlank()) println("\tVerb:\n $it") }
    dictionaryEntryToString(dictionaryWord.adjective).let { if (it.isNotBlank()) println("\tAdjective:\n $it") }
    println()
}