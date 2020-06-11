package com.kostrifon.mydictionary

import kotlinx.coroutines.runBlocking
import org.junit.Test

class Test2 {
    @Test
    fun test() {
        /*
        YM -> f1 -> LexicalCategory -> f2 -> iD
        OM ->
        */
        val requestedWord = RequestedWord("file", "pronunciations,etymologies")
        val oxfordApi =
            OxfordDictionaryApi(BuildConfig.OXFORD_APP_ID, BuildConfig.OXFORD_APP_KEY)
        val yandexApi =
            YandexDictionaryApi(BuildConfig.YANDEX_API_KEY)
        runBlocking {
            val oxfordResult = oxfordApi.getWord(requestedWord)
            val yandexResult = yandexApi.getWord(requestedWord)
            val oxfordModel = oxfordResult.getOrNull()
            val yandexModel = yandexResult.getOrNull()
            assert(oxfordModel != null) { "oxford model is null" }
            assert(yandexModel != null) { "yandex model is null" }
            val lexicalCategories = f1(oxfordModel!!, yandexModel!!)
            assert(lexicalCategories.isNotEmpty()) { "lexicalCategories is empty" }
        }
    }
}

interface LexicalCat {
    val name: String
}

interface Noun : LexicalCat

interface Verb : LexicalCat

interface Adjective : LexicalCat

data class OxfordNoun(val pronunciation: Pronunciation, val etymology: String) : Noun {
    override val name: String
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
}

data class OxfordVerb(val pronunciation: Pronunciation, val etymology: String) : Verb {
    override val name: String
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
}

data class OxfordAdjective(val pronunciation: Pronunciation, val etymology: String) : Adjective {
    override val name: String
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
}

fun f1(
    oxfordModel: OxfordDictionaryModel,
    yandexModel: YandexDictionaryModel
): List<LexicalCat> {
    val list = mutableListOf<LexicalCat>()
    val etymologies = mutableListOf<Etymology>()
    val pronunciations = mutableListOf<Etymology>()
    oxfordModel.results.forEach {
        it.lexicalEntries.forEach { lexicalEntry ->
            lexicalEntry.lexicalCategory
            etymologies += getOxfordEtymologies(lexicalEntry)
        }
    }
    list += getOxfordPronunciations()
    list += getYandexTranslates()
    return list
}

fun getOxfordEtymologies(lexicalEntries: LexicalEntries): List<Etymology> {
    val list = mutableListOf<Etymology>()
    lexicalEntries.entries?.forEach {
        it.etymologies?.forEach { etymology ->
            list += Etymology(lexicalEntries.lexicalCategory?.id?:"", etymology)
        }
    }
    return list
}

data class Etymology(val lexicalCategory: String, val text: String)

fun getOxfordPronunciations(): List<LexicalCat> {
    return emptyList()
}

fun getYandexTranslates(): List<LexicalCat> {
    return emptyList()
}
