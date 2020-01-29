package com.kostrifon.mydictionary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            val requestedWord = RequestedWord(editText.text.toString(), "pronunciations,etymologies")
            GlobalScope.launch {
                f(requestedWord)
            }
        }
    }

    // TODO rename
    private suspend fun f(requestedWord: RequestedWord) {
        OxfordDictionaryApi(BuildConfig.OXFORD_APP_ID, BuildConfig.OXFORD_APP_KEY).getWord(
            requestedWord
        ).fold(
            onFailure = { showError(it.message ?: "Error") },
            onSuccess = { updateOxfordFields(it) }
        )

        YandexDictionaryApi(BuildConfig.YANDEX_API_KEY).getWord(requestedWord).fold(
            onFailure = { showError(it.message ?: "Error") },
            onSuccess = { updateYandexFields(it) }
        )
    }

    private fun updateOxfordFields(oxfordDictionaryModel: OxfordDictionaryModel) {
        val oxfordDictionaryModelFacade = OxfordDictionaryModelFacade(oxfordDictionaryModel)
        val phoneticSpellings = mutableListOf<String>()
        oxfordDictionaryModelFacade.getPronunciations().forEach {
            phoneticSpellings += it.phoneticSpelling
        }
        runOnUiThread {
            pronounces.text = phoneticSpellings.toString()
            etymologies.text = oxfordDictionaryModelFacade.getEtymologies().toString()
        }
    }

    private fun updateYandexFields(yandexDictionaryModel: YandexDictionaryModel) {
        val yandexDictionaryModelFacade = YandexDictionaryModelFacade(yandexDictionaryModel)
        runOnUiThread {
            word.text = yandexDictionaryModelFacade.getWord()
            translates.text = yandexDictionaryModelFacade.getTranslates().toString()
        }
    }

    private fun showError(message: String) {
        Log.e("MyDictionary", message)
    }
}


