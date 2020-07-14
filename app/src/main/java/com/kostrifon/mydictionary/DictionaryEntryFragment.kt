package com.kostrifon.mydictionary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_dictionary_entry.view.*
import kotlinx.android.synthetic.main.pronuncation_view.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DictionaryEntryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DictionaryEntryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_dictionary_entry,
            container,
            false
        )
        setTranslatedWord(view, "water")
        val testList = listOf(
            Pronunciation(
                "ˈwɔdər",
                "https://audio.oxforddictionaries.com/en/mp3/water_us_1_rr.mp3"
            ),
            Pronunciation(
                "ˈwɑdər",
                "https://audio.oxforddictionaries.com/en/mp3/water_us_2_rr.mp3"
            )
        )
        setPronunciations(view, testList)
        setTranslates(
            view,
            "вода, водоем, акватория, влага, водность, волны",
            "поливать, мочить",
            "водяной"
        )

        setEtymologies(
            view,
            listOf(
                "Old English wæter (noun), wæterian (verb), of" +
                        " Germanic origin; related to Dutch water, German Wasser, " +
                        "from an Indo-European root shared by Russian voda " +
                        "(compare with vodka), also by Latin unda ‘wave’ and " +
                        "Greek hudōr ‘water’", "blalalalalalalalal"
            )
        )

        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DictionaryEntryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DictionaryEntryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setTranslatedWord(view: View, word: String) {
        view.translatedWordTextView.text = word
    }

    private fun setPronunciations(
        view: View,
        pronunciations: List<Pronunciation>
    ) {

        fun createPronunciationView(text: String, audioUrl: String): View {
            val pronunciationView =
                layoutInflater.inflate(R.layout.pronuncation_view, null)
            pronunciationView.pronunciationTextView.text = text

            return pronunciationView
        }

        pronunciations.forEach {
            view.pronunciationsLinearLayout.addView(
                createPronunciationView(it.phoneticSpelling, it.audioFile)
            )
        }
    }

    private fun setTranslates(
        view: View,
        noun: String,
        verb: String,
        adjective: String
    ) {
        view.findViewById<TextView>(R.id.nounTextView).text = noun
        view.findViewById<TextView>(R.id.verbTextView).text = verb
        view.findViewById<TextView>(R.id.adjectiveTextView).text = adjective
    }

    private fun setEtymologies(view: View, etymologies: List<String>) {
        view.etymologyTextView.text = etymologies.joinToString(
            separator = "\n\t",
            prefix = "\t"
        )
    }
}
