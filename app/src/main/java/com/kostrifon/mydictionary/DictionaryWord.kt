package com.kostrifon.mydictionary

import java.util.*


data class Pronunciation(val phoneticSpelling: String, val audioFile: String)

data class DictionaryEntry(
    val translates: List<String>,
    val pronunciations: List<Pronunciation>,
    val etymologies: List<String>
)

data class DictionaryWord(
    val word: String,
    val noun: DictionaryEntry,
    val verb: DictionaryEntry,
    val adjective: DictionaryEntry
)

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
            fun getPronunciation(
                pronunciations: Pronunciations
            ): Pronunciation {
                return Pronunciation(
                    pronunciations.phoneticSpelling ?: "",
                    pronunciations.audioFile ?: ""
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
