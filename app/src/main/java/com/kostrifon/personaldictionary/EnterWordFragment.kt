package com.kostrifon.personaldictionary

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import io.ktor.util.KtorExperimentalAPI
import kotlinx.android.synthetic.main.fragment_enter_word.*
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

        view.invert_colors_button.setImageResource(
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO -> R.drawable.ic_invert_colors_off_24px
                else -> R.drawable.ic_invert_colors_24px
            }
        )

        view.invert_colors_button.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_NO -> AppCompatDelegate.MODE_NIGHT_YES
                    Configuration.UI_MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_NO
                    else -> AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }

        view.dictionaryImageView.setOnClickListener {
            hideSoftKeyboard(activity!!, view)
            activity?.let {
                it.supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container, ItemFragment.newInstance())
                    addToBackStack(null)
                    commit()
                }
            }
        }

        view.clearTextButton.setOnClickListener {
            view.translatedWordEditText.setText("")
        }

        view.findIconImageView.isEnabled = false

        val objectAnimator = ObjectAnimator.ofFloat(view.spinnerImageView, "rotation", 360f).apply {
            interpolator = LinearInterpolator()
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
        }
        view.findIconImageView.setOnClickListener {
            hideSoftKeyboard(activity!!, view)

            if (!isConnected(context!!)) {
                Toast.makeText(context, R.string.offline, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            view.spinnerImageView.visibility = VISIBLE
            objectAnimator.start()

            val translatedWord = view.translatedWordEditText.text.toString()
            if (translatedWord.isNotBlank()) {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        getTranslatedWord(translatedWord, { dictionaryWord: DictionaryWord ->
                            objectAnimator.cancel()
                            activity!!.supportFragmentManager.beginTransaction().apply {
                                replace(R.id.fragment_container, DictionaryEntryFragment.newInstance(dictionaryWord))
                                addToBackStack(null)
                                commit()
                            }
                        }, { message: String ->
                            showErrorDialog(getString(R.string.error), message)
                            objectAnimator.cancel()
                        })
                    } catch (e: IOException) {
                        showErrorDialog(getString(R.string.error), e.localizedMessage ?: getString(R.string.unknown))
                        objectAnimator.cancel()
                    } catch (e: Exception) {
                        showErrorDialog(
                            getString(R.string.connection_error),
                            e.localizedMessage ?: getString(R.string.unknown)
                        )
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
                editText.error = null
                if (editText.text.isBlank()) {
                    isFindIconEnabled(false)
                    clearTextButton.visibility = GONE
                } else {
                    if (text.trim().split("\\s+".toRegex()).size > 1) {
                        editText.error = getString(R.string.only_one_word_is_allowed)
                    }
                    isFindIconEnabled(true)
                    clearTextButton.visibility = VISIBLE
                }
            }
        })

        val db = DbHelper(context!!).readableDatabase
        if (getAllWordsFromDb(db).isNotEmpty()) {
            view.dictionaryImageView.isEnabled = true
            view.dictionaryImageView.setColorFilter(ContextCompat.getColor(context!!, R.color.colorAccent))
        } else {
            view.dictionaryImageView.isEnabled = false
            view.dictionaryImageView.setColorFilter(ContextCompat.getColor(context!!, android.R.color.darker_gray))
        }
        db.close()

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
                        view?.spinnerImageView?.visibility = GONE
                        activity?.supportFragmentManager?.popBackStack()
                    }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }
        }
    }

    fun isFindIconEnabled(enabled: Boolean) {
        view?.findIconImageView?.isEnabled = enabled
        view?.findIconImageView?.setColorFilter(
            ContextCompat.getColor(
                context!!, if (enabled) {
                    R.color.colorAccent
                } else {
                    android.R.color.darker_gray
                }
            )
        )
    }
}

