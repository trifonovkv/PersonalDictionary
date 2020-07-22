package com.kostrifon.mydictionary

import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
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


class DictionaryWordUnitTest {

    private val testDirectory = "/tmp"
    private var testWord = "water"

    @KtorExperimentalAPI
    @ExperimentalStdlibApi
    @Test
    fun testAll() {
        runBlocking {
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
                    }.forEach { file ->
                        assert(file.delete()) { println("${file.path} is't exist") }
                    }
                }
            }, { message: String ->
                assert(false) { println(message) }
            })
        }
    }
}