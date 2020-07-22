package com.kostrifon.mydictionary

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.ktor.util.KtorExperimentalAPI

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.io.File

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.kostrifon.mydictionary", appContext.packageName)
    }

    @KtorExperimentalAPI
    @Test
    fun testDownloadCompat() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val link = "https://audio.oxforddictionaries.com/en/mp3/water_us_1_rr.mp3"
        val path = "/sdcard/1.mp3"
        downloadCompat(appContext, link, path)
        assert(File(path).exists()) { println("File $path doesn't exists")}
        File(path).delete()
    }
}
