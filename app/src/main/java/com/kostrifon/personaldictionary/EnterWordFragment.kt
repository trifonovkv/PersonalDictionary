package com.kostrifon.personaldictionary

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import io.ktor.util.KtorExperimentalAPI
import kotlinx.android.synthetic.main.fragment_enter_word.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException


class EnterWordFragment : Fragment() {

    @ExperimentalStdlibApi
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
            // hide keyboard
            val imm: InputMethodManager =
                activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view!!.windowToken, 0)

            val objectAnimator = ObjectAnimator.ofFloat(view.spinnerImageView, "rotation", 360f).apply {
                interpolator = LinearInterpolator()
                duration = 1500
                repeatCount = ValueAnimator.INFINITE
            }

            view.spinnerImageView.visibility = View.VISIBLE
            objectAnimator.start()

            val translatedWord = view.translatedWordEditText.text.toString()
            if (translatedWord.isNotBlank()) {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        getTranslatedWord(translatedWord, { dictionaryWord: DictionaryWord ->
                            activity!!.supportFragmentManager.beginTransaction().apply {
                                replace(R.id.fragment_container, DictionaryEntryFragment.newInstance(dictionaryWord))
                                addToBackStack(null)
                                commit()
                            }
                        }, { message: String ->
                            showErrorDialog("Error", message)
                            objectAnimator.cancel()
                        })
                    } catch (e: IOException) {
                        showErrorDialog("Error", e.localizedMessage ?: "Unknown")
                        objectAnimator.cancel()

                    } catch (e: Exception) {
                        showErrorDialog("Connection error", e.localizedMessage ?: "Unknown")
                        objectAnimator.cancel()
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
        fun newInstance() = EnterWordFragment()
    }

    private fun showErrorDialog(title: String, message: String) {
        activity?.let {
            GlobalScope.launch(Dispatchers.Main) {
                AlertDialog.Builder(it)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.cancel) { _, _ ->
                        view?.spinnerImageView?.visibility = View.GONE
                        activity?.supportFragmentManager?.popBackStack()
                    }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }
        }
    }
}

