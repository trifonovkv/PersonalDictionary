package com.kostrifon.mydictionary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    val myDataset = mutableListOf("test")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            val requestedWord = RequestedWord(editText.text.toString(), "pronunciations,etymologies")
            GlobalScope.launch {
                f(requestedWord)
            }
        }


        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapter(myDataset.toTypedArray())

        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
    }

    // TODO rename
    private suspend fun f(requestedWord: RequestedWord) {
        /*OxfordDictionaryApi(BuildConfig.OXFORD_APP_ID, BuildConfig.OXFORD_APP_KEY).getWord(
            requestedWord
        ).fold(
            onFailure = { showError(it.message ?: "Error") },
            onSuccess = { updateOxfordFields(it) }
        )*/

        YandexDictionaryApi(BuildConfig.YANDEX_API_KEY).getWord(requestedWord).fold(
            onFailure = { showError(it.message ?: "Error") },
            onSuccess = { updateYandexFields(it) }
        )
    }

    private fun updateOxfordFields(oxfordDictionaryModel: OxfordDictionaryModel) {
        val oxfordDictionaryModelFacade = OxfordDictionaryModelFacade(oxfordDictionaryModel)
        val phoneticSpellings = mutableListOf<String>()
        oxfordDictionaryModelFacade.getPronunciations().forEach {
//            phoneticSpellings += it.phoneticSpelling
            phoneticSpellings += it.phoneticSpelling
            phoneticSpellings += it.audioFile
            myDataset.clear()
            myDataset.add(it.phoneticSpelling)
            myDataset.add(it.audioFile)
        }
        runOnUiThread {
//            pronounces.text = phoneticSpellings.toString()
            Log.d("sf", myDataset.toString())
//            viewAdapter.notifyDataSetChanged()
            recyclerView.adapter = MyAdapter(myDataset.toTypedArray())
            recyclerView.invalidate()
            etymologies.text = oxfordDictionaryModelFacade.getEtymologies().toString()
        }
    }

    private fun updateYandexFields(yandexDictionaryModel: YandexDictionaryModel) {
        val yandexDictionaryModelFacade = YandexDictionaryModelFacade(yandexDictionaryModel)
        runOnUiThread {
//            word_list_view.text = yandexDictionaryModelFacade.getWord()
            translates.text = yandexDictionaryModelFacade.getTranslates().toString()
        }
    }

    private fun showError(message: String) {
        Log.e("MyDictionary", message)
    }
}



