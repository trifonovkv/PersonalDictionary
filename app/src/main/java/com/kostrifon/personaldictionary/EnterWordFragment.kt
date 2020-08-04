package com.kostrifon.personaldictionary

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import io.ktor.util.KtorExperimentalAPI
import kotlinx.android.synthetic.main.fragment_enter_word.view.*


/**
 * A simple [Fragment] subclass.
 * Use the [EnterWordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EnterWordFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    @KtorExperimentalAPI
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_enter_word, container, false)

        view.dictionaryImageView.setOnClickListener {
            activity?.let {
                it.supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container, ItemFragment.newInstance())
                    addToBackStack(null)
                    commit()
                }
            }
        }

        view.findIconImageView.setOnClickListener {
            activity?.let {
                val translatedWord = view.translatedWordEditText.text.toString()
                if (translatedWord.isNotBlank()) {
                    it.supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragment_container, DictionaryEntryFragment.newInstance(translatedWord))
                        addToBackStack(null)
                        commit()
                    }
                }
            }
        }

        abstract class TextValidator(private val editText: EditText) : TextWatcher {
            abstract fun validate(editText: EditText, text: String)
            override fun afterTextChanged(s: Editable) {
                val text = editText.text.toString()
                validate(editText, text)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { /* Don't care */
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { /* Don't care */
            }
        }

        view.translatedWordEditText.addTextChangedListener(object : TextValidator(view.translatedWordEditText) {
            override fun validate(editText: EditText, text: String) {
                val ctx = context ?: return
                if (text.trim().split("\\s+".toRegex()).size > 1) {
                    editText.error = getString(R.string.only_one_word_is_allowed)
                    view.findIconImageView.isEnabled = false
                    view.findIconImageView.setColorFilter(ContextCompat.getColor(ctx, android.R.color.darker_gray))
                } else {
                    view.findIconImageView.isEnabled = true
                    view.findIconImageView.setColorFilter(ContextCompat.getColor(ctx, R.color.colorAccent))
                }
            }
        })

        // Inflate the layout for this fragment
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = EnterWordFragment().apply { arguments = Bundle().apply {} }
    }
}