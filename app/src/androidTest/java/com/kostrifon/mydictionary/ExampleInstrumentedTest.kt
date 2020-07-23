package com.kostrifon.mydictionary

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.ktor.util.KtorExperimentalAPI
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


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
    fun testPlaySound() {
        playSound(context, testLink)
    }
}
