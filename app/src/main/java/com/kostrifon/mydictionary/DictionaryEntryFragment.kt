package com.kostrifon.mydictionary

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_dictionary_entry.*
import kotlinx.android.synthetic.main.fragment_dictionary_entry.view.*
import kotlinx.android.synthetic.main.pronuncation_view.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


private const val ARG_WORD = "word"


@ExperimentalStdlibApi
suspend fun getTranslatedWord(
    word: String,
    success: (word: DictionaryWord) -> Unit,
    error: (message: String) -> Unit
) {

    suspend fun getOxfordWord(
        word: String,
        success: (word: OxfordDictionaryWord) -> Unit,
        error: (json: String) -> Unit
    ) {
        makeRequest(
            createClient(
                BuildConfig.OXFORD_APP_ID,
                BuildConfig.OXFORD_APP_KEY
            ),
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
        runBlocking {
            val json = makeRequest(createClient(), word)
            val yandexDictionaryModel = parseYandexDictionaryModel(json)
            if (yandexDictionaryModel.def.isEmpty()) {
                error(json)
            } else {
                success(getYandexDictionaryWord(yandexDictionaryModel))
            }
        }
    }

    val oxfordSuccess = { oxfordWord: OxfordDictionaryWord ->
        val yandexSuccess = { yandexWord: YandexDictionaryWord ->
            val dictionaryWord = getDictionaryWord(oxfordWord, yandexWord)
            success(dictionaryWord)
        }
        val yandexError = { json: String -> error(json) }
        getYandexWord(word, yandexSuccess, yandexError)
    }

    val oxfordError = { json: String -> error(json) }

    getOxfordWord(word, oxfordSuccess, oxfordError)
}


/**
 * A simple [Fragment] subclass.
 * Use the [DictionaryEntryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DictionaryEntryFragment : Fragment() {
    private var word: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            word = it.getString(ARG_WORD)
        }
    }

    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_dictionary_entry,
            container,
            false
        )

        val objectAnimator =
            ObjectAnimator.ofFloat(view.imageView5, "rotation", 360f).apply {
                interpolator = LinearInterpolator()
                duration = 1500
                repeatCount = ValueAnimator.INFINITE
            }
        objectAnimator.start()

        GlobalScope.launch(Dispatchers.IO) {
            word?.let {
                try {
                    getTranslatedWord(it, { dictionaryWord: DictionaryWord ->
                        GlobalScope.launch(Dispatchers.Main) {
                            setTranslatedWord(view, dictionaryWord)
                            setPronunciations(view, dictionaryWord)
                            setTranslates(view, dictionaryWord)
                            setEtymologies(view, dictionaryWord)
                            objectAnimator.cancel()
                            view.imageView5.visibility = View.GONE
                        }
                        Unit
                    }, { message: String ->
                        showErrorDialog("Error", message)
                    })
                } catch (e: Exception) {
                    GlobalScope.launch(Dispatchers.Main) {
                        showErrorDialog(
                            "Connection error",
                            e.localizedMessage ?: "Unknown"
                        )
                    }
                    objectAnimator.cancel()
                }
            }
        }
        // Inflate the layout for this fragment
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(word: String) =
            DictionaryEntryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_WORD, word)
                }
            }
    }

    private fun setTranslatedWord(view: View, dictionaryWord: DictionaryWord) {
        view.translatedWordTextView.text = dictionaryWord.word
    }

    private fun setPronunciations(view: View, dictionaryWord: DictionaryWord) {

        fun createPronunciationView(text: String, audioUrl: String): View {
            val pronunciationView =
                layoutInflater.inflate(
                    R.layout.pronuncation_view,
                    pronunciationsLinearLayout,
                    false
                )
            pronunciationView.pronunciationTextView.text = text

            return pronunciationView
        }

        val pronunciations = mutableListOf<Pronunciation>()
        pronunciations.addAll(dictionaryWord.noun.pronunciations)
        pronunciations.addAll(dictionaryWord.verb.pronunciations)
        pronunciations.addAll(dictionaryWord.adjective.pronunciations)

        pronunciations.filter { it.audioFile.isNotBlank() }.forEach {
            view.pronunciationsLinearLayout.addView(
                createPronunciationView(it.phoneticSpelling, it.audioFile)
            )
        }
    }

    private fun setTranslates(view: View, dictionaryWord: DictionaryWord) {
        listOf<Triple<List<String>, LinearLayout, TextView>>(
            Triple(
                dictionaryWord.noun.translates,
                view.nounLinearLayout,
                view.nounTextView
            ),
            Triple(
                dictionaryWord.verb.translates,
                view.verbLinearLayout,
                view.verbTextView
            ),
            Triple(
                dictionaryWord.adjective.translates,
                view.adjectiveLinearLayout,
                view.adjectiveTextView
            )
        ).forEach {
            if (it.first.isNotEmpty()) {
                it.second.visibility = VISIBLE
                it.third.text = it.first.joinToString(separator = ", ")
            }
        }
    }

    private fun setEtymologies(view: View, dictionaryWord: DictionaryWord) {
        val etymologies = mutableListOf<String>()
        etymologies.addAll(dictionaryWord.noun.etymologies)
        etymologies.addAll(dictionaryWord.verb.etymologies)
        etymologies.addAll(dictionaryWord.adjective.etymologies)

        view.etymologyTextView.text = etymologies.joinToString(
            prefix = "\t",
            separator = "\n\t"
        )
    }

    private fun showErrorDialog(title: String, message: String) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.cancel) { _, _ ->
                    activity?.supportFragmentManager?.popBackStack()
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }
}