package com.kostrifon.mydictionary

import org.junit.Test


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
            val oxfordDictionaryWord = getDictionaryWord(oxfordDictionaryModel)

            assert(compareOxfordDictionary(oxfordDictionaryModel, oxfordDictionaryWord))
            { printAssertError(json, oxfordDictionaryWord) }
        }

        val error = { json: String -> assert(false) { println(json) } }

        makeRequest(
            createClient(BuildConfig.OXFORD_APP_ID, BuildConfig.OXFORD_APP_KEY),
            "neglect",
            success,
            error
        )
    }
}
