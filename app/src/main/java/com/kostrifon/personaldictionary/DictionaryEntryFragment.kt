package com.kostrifon.personaldictionary


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.ktor.util.KtorExperimentalAPI
import kotlinx.android.synthetic.main.fragment_dictionary_entry.*
import kotlinx.android.synthetic.main.fragment_dictionary_entry.view.*
import kotlinx.android.synthetic.main.pronuncation_view.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class DictionaryEntryFragment : Fragment() {
    private val dictionaryWordSerializeKey = "dictionaryWordSerializeKey"
    private lateinit var dictionaryWord: DictionaryWord

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { dictionaryWord = it.getSerializable(dictionaryWordSerializeKey) as DictionaryWord }
    }

    @KtorExperimentalAPI
    @ExperimentalStdlibApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dictionary_entry, container, false)
        val cacheFiles = mutableListOf<File>()

        GlobalScope.launch {
            val pronunciations = getUniquePronunciations(dictionaryWord)
            val cache = pronunciations.map { pronunciation ->
                pronunciation.audioFile to "${context!!.cacheDir}/${pronunciation.audioFile.substringAfterLast(
                    "/"
                )}"
            }.toMap()
            cache.forEach {
                cacheFiles += downloadCompat(context, it.key, it.value)
            }
            GlobalScope.launch(Dispatchers.Main) { setPronunciations(view, pronunciations, cache) }
        }
        setTranslatedWord(view, dictionaryWord)
        setTranslates(view, dictionaryWord)
        setEtymologies(view, dictionaryWord)

        view.backImageView.setOnClickListener {
            GlobalScope.launch { cacheFiles.forEach { it.delete() } }
            activity?.supportFragmentManager?.popBackStack()
        }

        // Inflate the layout for this fragment
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(dictionaryWord: DictionaryWord) = DictionaryEntryFragment().apply {
            arguments = Bundle().apply { putSerializable(dictionaryWordSerializeKey, dictionaryWord) }
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
}