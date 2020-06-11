package com.kostrifon.mydictionary

import kotlinx.coroutines.runBlocking
import org.junit.Test

class Test {
    @Test
    fun test() {
        val requestedWord = RequestedWord("file", "pronunciations,etymologies")
        val dictionaryApi =
            OxfordDictionaryApi(BuildConfig.OXFORD_APP_ID, BuildConfig.OXFORD_APP_KEY)
        runBlocking {
            val result = dictionaryApi.getWord(requestedWord)
            val model = result.getOrNull()
            assert(model != null) { "model is null" }
            val words = getOxfordWords(model!!)
            assert(words.isNotEmpty()) { "words is empty" }
            printOxfordWords(words)
        }
    }

    private fun printOxfordWords(oxfordWords: List<OxfordWord>) {
        oxfordWords.forEach {
            println(it.lexicalCategory)
            it.pronunciations.forEach { pronunciation ->
                if (pronunciation.audioFile != "" || pronunciation.phoneticSpelling != "") {
                    println("${pronunciation.phoneticSpelling}  ${pronunciation.audioFile}")
                }
            }
            if (it.etymologies != "") {
                println(it.etymologies)
            }
            println("--------------------------------------------------------------------")
        }
    }

    private fun getOxfordWords(model: OxfordDictionaryModel): List<OxfordWord> {
        val words = mutableListOf<OxfordWord>()
        model.results.forEach {
            it.lexicalEntries.forEach { lexicalEntry ->
                words += oxfordWord(lexicalEntry)
            }
        }
        return words
    }

    private fun oxfordWord(lexicalEntries: LexicalEntries): OxfordWord {
        return OxfordWord(
            lexicalEntries.lexicalCategory?.text ?: "",
            getPronunciations(lexicalEntries),
            getEtymology(lexicalEntries.entries)
        )
    }

    private fun getEtymology(entries: List<Entries>?): String {
        var returnString = ""
        entries?.forEach {
            it.etymologies?.forEach { etymology ->
                if (etymology != "") returnString = etymology
            }
        }
        return returnString
    }

    private fun getPronunciations(lexicalEntries: LexicalEntries): List<Pronunciation> {
        val pronunciations = mutableListOf<Pronunciation>()
        lexicalEntries.pronunciations?.let { f(it) }?.let { pronunciations.addAll(it) }
        lexicalEntries.entries?.get(0)?.pronunciations?.let { f(it) }?.let {
            pronunciations.addAll(it)
        }
        return pronunciations
    }

    // TODO rename (converter from pronunciations to my pronunciations)
    private fun f(pronunciations: List<Pronunciations>): List<Pronunciation> {
        val returnPronunciations = mutableListOf<Pronunciation>()
        pronunciations.forEach {
            if (it.phoneticNotation == "IPA") {
                returnPronunciations += Pronunciation(
                    it.phoneticSpelling ?: "", it.audioFile ?: ""
                )
            }
        }
        return returnPronunciations
    }
}

data class OxfordWord(
    val lexicalCategory: String,
    val pronunciations: List<Pronunciation>,
    val etymologies: String
)