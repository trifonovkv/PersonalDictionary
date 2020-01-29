package com.kostrifon.mydictionary

import kotlinx.coroutines.runBlocking
import org.junit.Test

class DictionariesModelFacadeTest {

    private val testWord = "volume"
    private val requestedWord = RequestedWord(testWord, "pronunciations,etymologies")
    private val oxfordDictionaryApi =
        OxfordDictionaryApi(BuildConfig.OXFORD_APP_ID, BuildConfig.OXFORD_APP_KEY)
    private val yandexDictionaryApi = YandexDictionaryApi(BuildConfig.YANDEX_API_KEY)

    @Test
    fun test() = runBlocking {

        val oxfordDictionaryModel = f(oxfordDictionaryApi)
        val yandexDictionaryModel = f(yandexDictionaryApi)

        assert(oxfordDictionaryModel != null)
        assert(yandexDictionaryModel != null)
        val oxfordDictionaryModelFacade = OxfordDictionaryModelFacade(oxfordDictionaryModel!!)
        val yandexDictionaryModelFacade = YandexDictionaryModelFacade(yandexDictionaryModel!!)
        assert(yandexDictionaryModelFacade.getWord() == testWord)
        assert(oxfordDictionaryModelFacade.getPronunciations().isNotEmpty())
        assert(yandexDictionaryModelFacade.getTranslates().isNotEmpty())
        assert(oxfordDictionaryModelFacade.getEtymologies().isNotEmpty())
    }

    // TODO need name
    private suspend fun <T> f(dictionaryApi: DictionaryApi<T>): T? {
        dictionaryApi.getWord(requestedWord).fold(
            onFailure = {
                assert(false) { it.localizedMessage ?: "Error" }
                return null
            },
            onSuccess = { return it }
        )
    }
}


