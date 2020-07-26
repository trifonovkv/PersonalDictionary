package com.kostrifon.mydictionary

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import io.ktor.util.KtorExperimentalAPI
import kotlinx.android.synthetic.main.fragment_dictionary_entry.*
import kotlinx.android.synthetic.main.fragment_dictionary_entry.view.*
import kotlinx.android.synthetic.main.pronuncation_view.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException


private const val ARG_WORD = "word"


/**
 * A simple [Fragment] subclass.
 * Use the [DictionaryEntryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@KtorExperimentalAPI
class DictionaryEntryFragment : Fragment() {
    private var word: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { word = it.getString(ARG_WORD) }
    }

    @ExperimentalStdlibApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dictionary_entry, container, false)
        val cacheFiles = mutableListOf<File>()

        // hide keyboard
        val imm: InputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)

        val objectAnimator = ObjectAnimator.ofFloat(view.imageView5, "rotation", 360f).apply {
            interpolator = LinearInterpolator()
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
        }
        objectAnimator.start()

        GlobalScope.launch(Dispatchers.IO) {
            word?.let { word ->
                try {
                    getTranslatedWord(word, { dictionaryWord: DictionaryWord ->
                        GlobalScope.launch {
                            val pronunciations = getUniquePronunciations(dictionaryWord)
                            val cache = pronunciations.map { pronunciation ->
                                pronunciation.audioFile to "${context!!.cacheDir}/${pronunciation.audioFile.substringAfterLast(
                                    "/"
                                )}"
                            }.toMap()
                            cache.map {
                                cacheFiles += downloadCompat(context, it.key, it.value)
                            }
                            GlobalScope.launch(Dispatchers.Main) { setPronunciations(view, pronunciations, cache) }

                        }
                        GlobalScope.launch(Dispatchers.Main) {
                            setTranslatedWord(view, dictionaryWord)
                            setTranslates(view, dictionaryWord)
                            setEtymologies(view, dictionaryWord)
                            objectAnimator.cancel()
                            view.imageView5.visibility = View.GONE
                        }
                    }, { message: String ->
                        showErrorDialog("Error", message)
                        objectAnimator.cancel()
                    })
                } catch (e: IOException) {
                    showErrorDialog(
                        "Error",
                        e.localizedMessage ?: "Unknown"
                    )
                    objectAnimator.cancel()
                } catch (e: Exception) {
                    showErrorDialog(
                        "Connection error",
                        e.localizedMessage ?: "Unknown"
                    )
                    objectAnimator.cancel()
                }
            }
        }

        view.backImageView.setOnClickListener {
            GlobalScope.launch { cacheFiles.forEach { it.delete() } }
            activity?.supportFragmentManager?.popBackStack()
        }

        // Inflate the layout for this fragment
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(word: String) = DictionaryEntryFragment().apply {
            arguments = Bundle().apply { putString(ARG_WORD, word) }
        }
    }

    private fun setTranslatedWord(view: View, dictionaryWord: DictionaryWord) {
        view.translatedWordTextView.text = dictionaryWord.word
    }

    private fun setPronunciations(view: View, pronunciations: List<Pronunciation>, cache: Map<String, String>) {
        pronunciations.forEach { pronunciation ->
            layoutInflater.inflate(R.layout.pronuncation_view, pronunciationsLinearLayout, false).let {
                it.pronunciationTextView.text = pronunciation.phoneticSpelling
                it.pronunciationTextView.setOnClickListener {
                    context?.let { ctx ->
                        cache[pronunciation.audioFile]?.let { path ->
                            playSound(ctx, path)
                        }
                    }
                }
                view.pronunciationsLinearLayout.addView(it)
            }
        }
    }

    private fun setTranslates(view: View, dictionaryWord: DictionaryWord) {
        listOf<Triple<List<String>, LinearLayout, TextView>>(
            Triple(dictionaryWord.noun.translates, view.nounLinearLayout, view.nounTextView),
            Triple(dictionaryWord.verb.translates, view.verbLinearLayout, view.verbTextView),
            Triple(dictionaryWord.adjective.translates, view.adjectiveLinearLayout, view.adjectiveTextView)
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

        view.etymologyTextView.text = etymologies.joinToString(prefix = "\t", separator = "\n\t")
    }

    private fun showErrorDialog(title: String, message: String) {
        activity?.let {
            GlobalScope.launch(Dispatchers.Main) {
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
}