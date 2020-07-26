package com.kostrifon.personaldictionary

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val testLink = "https://audio.oxforddictionaries.com/en/mp3/water_us_1_rr.mp3"


    @Test
    fun useAppContext() {
        // Context of the app under test.
        assertEquals("com.kostrifon.mydictionary", context.packageName)
    }

    @KtorExperimentalAPI
    @Test
    fun testDownloadCompat() {
        val path = "${context.cacheDir}/1.mp3"
        val file = downloadCompat(context, testLink, path)
        assert(file.exists()) { println("File $path doesn't exists") }
        file.delete()
    }

    @KtorExperimentalAPI
    @Test
    fun testDownloadFiles() {
        mapOf(
            ("https://audio.oxforddictionaries.com/en/mp3/water_us_1_rr.mp3" to "${context.cacheDir}/water_us_1_rr.mp3"),
            ("https://audio.oxforddictionaries.com/en/mp3/water_us_2_rr.mp3" to "${context.cacheDir}/water_us_2_rr.mp3")
        ).map {
            downloadCompat(context, it.key, it.value)
        }.forEach {
            println("Test file: ${it.path}")
            assertTrue(it.exists(), "File ${it.path} doesn't exists")
            it.delete()
        }
    }

    @KtorExperimentalAPI
    @ExperimentalStdlibApi
    @Test
    fun testDownloadPronunciations() {
        runBlocking {
            getTranslatedWord("water", { dictionaryWord: DictionaryWord ->
                assert(true)
                getUniquePronunciations(dictionaryWord).map { pronunciation ->
                    pronunciation.audioFile to "${context.cacheDir}/${pronunciation.audioFile.substringAfterLast("/")}"
                }.toMap().map {
                    downloadCompat(context, it.key, it.value)
                }.forEach {file ->
                    println("Test file: ${file.path}")
                    assert(file.exists()) { "File ${file.path} doesn't exists" }
                    file.delete()
                }
            }, { message: String ->
                assert(false) { println(message) }
            })
        }
    }
}
