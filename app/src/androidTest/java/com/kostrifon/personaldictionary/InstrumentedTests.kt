package com.kostrifon.personaldictionary

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedTests {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val waterWord = DictionaryWord(
        "water", DictionaryEntry(
            listOf("водоем", "акватория", "влага", "водность", "волны"),
            listOf(
                Pronunciation("ˈwɔdər", "https://audio.oxforddictionaries.com/en/mp3/water_us_1_rr.mp3"),
                Pronunciation("ˈwɑdər", "https://audio.oxforddictionaries.com/en/mp3/water_us_2_rr.mp3")
            ),
            listOf(
                "Old English wæter (noun), wæterian (verb), " +
                        "of Germanic origin; related to Dutch water, German Wasser, from an Indo-European root " +
                        "shared by Russian voda (compare with vodka), also by Latin unda ‘wave’ and Greek hudōr " +
                        "‘water’"
            )
        ), DictionaryEntry(
            listOf("поливать", "мочить"),
            listOf(
                Pronunciation("ˈwɔdər", "https://audio.oxforddictionaries.com/en/mp3/water_us_1_rr.mp3"),
                Pronunciation("ˈwɑdər", "https://audio.oxforddictionaries.com/en/mp3/water_us_2_rr.mp3")
            ),
            listOf()
        ), DictionaryEntry(
            listOf("водяной"),
            listOf(),
            listOf()
        )
    )
    private val fireWord = DictionaryWord(
        "fire", DictionaryEntry(
            listOf("огонь", "возгорание", "камин", "стрельба", "обстрел"),
            listOf(
                Pronunciation("ˈfaɪ(ə)r", "https://audio.oxforddictionaries.com/en/mp3/fire_us_1.mp3")
            ),
            listOf(
                "Old English fȳr (noun), fȳrian ‘supply with material for a fire’, of West Germanic origin; " +
                        "related to Dutch vuur and German Feuer"
            )
        ), DictionaryEntry(
            listOf("стрелять", "увольнять", "поджечь", "обстрелять"),
            listOf(
                Pronunciation("ˈfaɪ(ə)r", "https://audio.oxforddictionaries.com/en/mp3/fire_us_1.mp3")
            ),
            listOf()
        ), DictionaryEntry(
            listOf("огневой", "противопожарный", "огненный"),
            listOf(),
            listOf()
        )
    )
    private val earthWord = DictionaryWord(
        "earth", DictionaryEntry(
            listOf("земля", "заземление", "планета"),
            listOf(Pronunciation("ərθ", "https://audio.oxforddictionaries.com/en/mp3/earth_us_1.mp3")),
            listOf("Old English eorthe, of Germanic origin; related to Dutch aarde and German Erde")
        ), DictionaryEntry(
            listOf("заземлить"),
            listOf(),
            listOf()
        ), DictionaryEntry(
            listOf("земной", "земляной"),
            listOf(),
            listOf()
        )
    )

    @Test
    fun useAppContext() {
        // Context of the app under test.
        assertEquals("com.kostrifon.personaldictionary", context.packageName)
    }

    @Before
    fun initDB() {
        val dbHelper = DictionaryWordDbHelper(context)
        val db = dbHelper.writableDatabase

        // clear db
        listOf(
            DictionaryContract.TABLE_NAME,
            DictionaryContract.Pronunciations.TABLE_NAME,
            DictionaryContract.Translates.TABLE_NAME,
            DictionaryContract.Etymologies.TABLE_NAME
        ).forEach {
            db.delete(it, null, null)
        }

        dbHelper.close()
    }

    @Test
    fun testInsertDoublesWords() {
        val dbHelper = DictionaryWordDbHelper(context)
        val db = dbHelper.readableDatabase
        assertEquals(0, putDictionaryWordToSql(db, waterWord))
        assertEquals(-1,putDictionaryWordToSql(db, waterWord))
        dbHelper.close()
    }

    @Test
    fun testPutGetDictionaryWordToSql() {
        val dbHelper = DictionaryWordDbHelper(context)
        val db = dbHelper.readableDatabase
        putDictionaryWordToSql(db, waterWord)
        putDictionaryWordToSql(db, fireWord)
        putDictionaryWordToSql(db, earthWord)
        assertEquals(waterWord, getDictionaryWordFromSql(db, waterWord.word))
        assertEquals(fireWord, getDictionaryWordFromSql(db, fireWord.word))
        assertEquals(earthWord, getDictionaryWordFromSql(db, earthWord.word))
        dbHelper.close()
    }

    private val testLink = "https://audio.oxforddictionaries.com/en/mp3/water_us_1_rr.mp3"

    @KtorExperimentalAPI
    @Test
    fun testDownloadCompat() {
        val path = "${context.cacheDir}/1.mp3"
        val file = downloadCompat(context, testLink, path)
        assertTrue(file.exists(), "File $path doesn't exists")
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
                assertTrue(true)
                getUniquePronunciations(dictionaryWord).map { pronunciation ->
                    pronunciation.audioFile to "${context.cacheDir}/${pronunciation.audioFile.substringAfterLast("/")}"
                }.toMap().map {
                    downloadCompat(context, it.key, it.value)
                }.forEach { file ->
                    println("Test file: ${file.path}")
                    assertTrue(file.exists(), "File ${file.path} doesn't exists")
                    file.delete()
                }
            }, { message: String ->
                assertTrue(false, message)
            })
        }
        CountDownLatch(1).await(7, TimeUnit.SECONDS)
    }
}