package com.kostrifon.mydictionary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


@ExperimentalStdlibApi
fun getDictionaryWord(
    word: String,
    success: (word: DictionaryWord) -> Unit,
    error: (message: String) -> Unit
) {

    fun getOxfordWord(
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

    fun getYandexWord(
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

    val oxfordSuccess = { oxfordWord: OxfordDictionaryWord ->
        val yandexSuccess = { yandexWord: YandexDictionaryWord ->
            val dictionaryWord = getDictionaryWord(oxfordWord, yandexWord)
//            printDictionaryWord(dictionaryWord)
//            println()
            success(dictionaryWord)
        }
        val yandexError = { json: String ->
            // TODO
            error("error")
        }
        getYandexWord(word, yandexSuccess, yandexError)
    }

    val oxfordError = { json: String ->
        // TODO
        error("error")
    }
    getOxfordWord(word, oxfordSuccess, oxfordError)
}


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitvity_main)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = EnterWordFragment()
        fragmentTransaction.add(R.id.fragment_container, fragment)
        fragmentTransaction.commit()








    }
}





