package com.kostrifon.mydictionary

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import kotlinx.android.synthetic.main.activity_enter_word.*
import kotlinx.android.synthetic.main.activity_translate.*
import kotlinx.android.synthetic.main.pronuncation_view.*
import kotlinx.android.synthetic.main.pronuncation_view.view.*
import kotlinx.android.synthetic.main.translates_view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translate)

        setTranslatedWord("water")
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
        setPronunciations(testList)
        setTranslates(
            "вода, водоем, акватория, влага, водность, волны",
            "поливать, мочить",
            "водяной"
        )

        setEtymologies(
            listOf(
                "Old English wæter (noun), wæterian (verb), of" +
                        " Germanic origin; related to Dutch water, German Wasser, " +
                        "from an Indo-European root shared by Russian voda " +
                        "(compare with vodka), also by Latin unda ‘wave’ and " +
                        "Greek hudōr ‘water’", "blalalalalalalalal"
            )
        )

        val objectAnimator =
            ObjectAnimator.ofFloat(imageView, "rotation", 360f).apply {
                interpolator = LinearInterpolator()
                duration = 3000
                repeatCount = ValueAnimator.INFINITE
            }


//        imageView.setOnClickListener {
//            if (objectAnimator.isRunning) {
//                objectAnimator.cancel()
//            } else {
//                objectAnimator.start()
//            }
//        }


    }

    private fun setTranslatedWord(word: String) {
        translatedWordTextView.text = word
    }

    private fun setPronunciations(pronunciations: List<Pronunciation>) {

        fun createPronunciationView(text: String, audioUrl: String): View {
            val view = layoutInflater.inflate(R.layout.pronuncation_view, null)
            view.pronunciationTextView.text = text

            return view
        }

        pronunciations.forEach {
            pronunciationsLinearLayout.addView(
                createPronunciationView(it.phoneticSpelling, it.audioFile)
            )
        }
    }

    private fun setTranslates(noun: String, verb: String, adjective: String) {
        nounTextView.text = noun
        verbTextView.text = verb
        adjectiveTextView.text = adjective
    }

    private fun setEtymologies(etymologies: List<String>) {
        etymologyTextView.text = etymologies.joinToString(
            separator = "\n\t",
            prefix = "\t"
        )
    }
}





