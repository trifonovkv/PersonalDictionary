@file:Suppress("DEPRECATION")

package com.kostrifon.personaldictionary


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
    private var db: SQLiteDatabase? = null
    private val cachedFiles = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { dictionaryWord = it.getSerializable(dictionaryWordSerializeKey) as DictionaryWord }
    }

    @KtorExperimentalAPI
    @ExperimentalStdlibApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dictionary_entry, container, false)
        val pronunciations = getUniquePronunciations(dictionaryWord)

        fun downloadPronunciationToCacheDir(context: Context, pronunciations: List<Pronunciation>) =
            pronunciations.map {
                downloadCompat(
                    context,
                    it.audioFile,
                    getFilePathFromCacheDir(context, it.audioFile.getFileName())
                )
            }

        GlobalScope.launch {
            pronunciations.let {
                if (!isExistPronunciationsInExternalFilesDir(context!!, it)) {
                    if (isConnected()) {
                        cachedFiles.addAll(downloadPronunciationToCacheDir(context!!, it))
                    } else {
                        GlobalScope.launch(Dispatchers.Main) {
                            Toast.makeText(context, "Offline", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                GlobalScope.launch(Dispatchers.Main) { setPronunciations(view, it) }
            }
        }

        setTranslatedWord(view, dictionaryWord)
        setTranslates(view, dictionaryWord)
        setEtymologies(view, dictionaryWord)

        view.backImageView.setOnClickListener {
            GlobalScope.launch {
                cachedFiles.forEach {
                    if (it.exists()) {
                        it.delete()
                    }
                }
            }
            activity?.supportFragmentManager?.popBackStack()
        }

        view.saveImage.setOnClickListener {
            db = DictionaryWordDbHelper(context!!).writableDatabase
            if (saveDictionaryWord(db!!, dictionaryWord)) {
                Toast.makeText(context, "Saved", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Error save", Toast.LENGTH_LONG).show()
            }
        }

        // Inflate the layout for this fragment
        return view
    }

    override fun onDestroy() {
        db?.close()
        super.onDestroy()
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

    @KtorExperimentalAPI
    private fun setPronunciations(view: View, pronunciations: List<Pronunciation>) {
        pronunciations.forEach { pronunciation ->
            layoutInflater.inflate(R.layout.pronuncation_view, pronunciationsLinearLayout, false).let {
                it.pronunciationTextView.text = pronunciation.phoneticSpelling
                it.pronunciationTextView.setOnClickListener { playPronunciation(context!!, pronunciation) }
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

    private fun isExistFileInExternalFilesDir(context: Context, fileName: String) =
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            File(context.getExternalFilesDir(null)!!.path + File.separator + fileName).exists()
        } else {
            false
        }

    private fun isExistFileInCacheDir(context: Context, fileName: String) =
        File(context.cacheDir.path + File.separator + fileName).exists()

    private fun getFilePathFromCacheDir(context: Context, fileName: String) =
        context.cacheDir.path + File.separator + fileName

    private fun getFilePathFromExternalFilesDir(context: Context, fileName: String) =
        context.getExternalFilesDir(null)!!.path + File.separator + fileName

    private fun isConnected() =
        (context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo?.isConnectedOrConnecting == true

    private fun isExistPronunciationsInExternalFilesDir(
        context: Context,
        pronunciations: List<Pronunciation>
    ): Boolean {
        pronunciations.forEach {
            if (!isExistFileInExternalFilesDir(context, it.audioFile.getFileName())) {
                return false
            }
        }
        return true
    }

    private fun isExistsPronunciationsInCacheDir(context: Context, pronunciations: List<Pronunciation>): Boolean {
        pronunciations.forEach {
            if (!isExistFileInCacheDir(context, it.audioFile.getFileName())) {
                return false
            }
        }
        return true
    }

    @KtorExperimentalAPI
    private fun downloadPronunciationsToExternalFilesDir(pronunciations: List<Pronunciation>) {
        pronunciations.forEach {
            downloadCompat(
                context,
                it.audioFile,
                getFilePathFromExternalFilesDir(context!!, it.audioFile.getFileName())
            )
        }
    }

    @KtorExperimentalAPI
    private fun savePronunciationAudioFiles(dictionaryWord: DictionaryWord): Boolean {
        val pronunciations = getUniquePronunciations(dictionaryWord)

        return when {
            Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED -> false
            isExistPronunciationsInExternalFilesDir(context!!, pronunciations) -> true
            isExistsPronunciationsInCacheDir(context!!, pronunciations) -> {
                pronunciations.forEach {
                    File(getFilePathFromCacheDir(context!!, it.audioFile.getFileName())).apply {
                        copyTo(File(getFilePathFromExternalFilesDir(context!!, it.audioFile.getFileName())))
                        delete()
                    }
                }
                true
            }
            isConnected() -> {
                downloadPronunciationsToExternalFilesDir(pronunciations)
                true
            }
            else -> false
        }
    }

    @KtorExperimentalAPI
    private fun saveDictionaryWord(db: SQLiteDatabase, dictionaryWord: DictionaryWord) =
        (putDictionaryWordToDb(db, dictionaryWord) >= 0) && savePronunciationAudioFiles(dictionaryWord)

    @KtorExperimentalAPI
    private fun downloadFileToCacheDir(context: Context, link: String) =
        downloadCompat(context, link, getFilePathFromCacheDir(context, link.getFileName())).path

    private fun getFilePathFormCacheOrExternalDirs(
        context: Context,
        fileName: String,
        success: (path: String) -> Unit,
        error: () -> Unit
    ) {
        when {
            isExistFileInCacheDir(context, fileName) -> success(getFilePathFromCacheDir(context, fileName))
            isExistFileInExternalFilesDir(context, fileName) -> success(
                getFilePathFromExternalFilesDir(context, fileName)
            )
            else -> error()
        }
    }

    @KtorExperimentalAPI
    fun playPronunciation(context: Context, pronunciation: Pronunciation) {
        getFilePathFormCacheOrExternalDirs(
            context,
            pronunciation.audioFile.getFileName(),
            { path: String -> playSound(context, path) },
            {
                kotlin.runCatching {
                    downloadFileToCacheDir(context, pronunciation.audioFile)
                }.onSuccess {
                    playSound(context, it)
                }.onFailure {
                    Toast.makeText(context, "Error get pronunciations", Toast.LENGTH_LONG).show()
                }
            }
        )
    }
}