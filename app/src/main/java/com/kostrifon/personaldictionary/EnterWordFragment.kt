package com.kostrifon.personaldictionary

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
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

        // delete old fragment when recreate this fragment
        if (savedInstanceState != null) {
            activity!!.supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, newInstance())
                addToBackStack(null)
                commit()
            }
        }

        view.check_invert_colors.isChecked =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        view.check_invert_colors.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_NO -> AppCompatDelegate.MODE_NIGHT_YES
                    Configuration.UI_MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_NO
                    else -> AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }

        view.image_dictionary.setOnClickListener {
            hideSoftKeyboard(activity!!, view)
            activity?.let {
                it.supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container, ItemFragment.newInstance())
                    addToBackStack(null)
                    commit()
                }
            }
        }

        view.image_clear_text.setOnClickListener {
            view.edit_translated_word.setText("")
        }

        view.image_search.isEnabled = false

        val objectAnimator = ObjectAnimator.ofFloat(view.image_spinner, "rotation", 360f).apply {
            interpolator = LinearInterpolator()
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
        }
        view.image_search.setOnClickListener {
            hideSoftKeyboard(activity!!, view)

            if (!isConnected(context!!)) {
                Toast.makeText(context, R.string.msg_offline, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            view.image_spinner.visibility = VISIBLE
            objectAnimator.start()

            val translatedWord = view.edit_translated_word.text.toString()
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
                            showErrorDialog(activity!!, getString(R.string.error), message)
                            objectAnimator.cancel()
                        })
                    } catch (e: IOException) {
                        showErrorDialog(
                            activity!!,
                            getString(R.string.error),
                            e.localizedMessage ?: getString(R.string.unknown)
                        )
                        objectAnimator.cancel()
                    } catch (e: Exception) {
                        showErrorDialog(
                            activity!!,
                            getString(R.string.error_connection),
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

        view.edit_translated_word.addTextChangedListener(object : TextValidator(view.edit_translated_word) {
            override fun validate(editText: EditText, text: String) {
                editText.error = null
                if (editText.text.isBlank()) {
                    view.image_search.isEnabled = false
                    image_clear_text.visibility = GONE
                } else {
                    if (text.trim().split("\\s+".toRegex()).size > 1) {
                        editText.error = getString(R.string.error_only_one_word_is_allowed)
                    }
                    view.image_search.isEnabled = true
                    image_clear_text.visibility = VISIBLE
                }
            }
        })

        val db = DbHelper(context!!).readableDatabase
        view.image_dictionary.isEnabled = getAllWordsFromDb(db).isNotEmpty()
        db.close()

        view.image_search.isEnabled = false

        // Inflate the layout for this fragment
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = EnterWordFragment()
    }

    private fun showErrorDialog(activity: FragmentActivity, title: String, message: String) {
        GlobalScope.launch(Dispatchers.Main) {
            AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.cancel) { _, _ ->
                    activity.image_spinner?.visibility = GONE
                    activity.supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragment_container, newInstance())
                        addToBackStack(null)
                        commit()
                    }
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }
}

