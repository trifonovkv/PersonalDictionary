package com.kostrifon.personaldictionary

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import android.database.sqlite.SQLiteOpenHelper
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


object DictionaryContract {
    const val ID = "_id"
    const val TABLE_NAME = "dictionary"
    const val COLUMN_NAME_WORD = "word"

    object Pronunciations {
        const val TABLE_NAME = "pronunciations"
        const val COLUMN_NAME_SPELLING = "spelling"
        const val COLUMN_NAME_AUDIO = "audio"
        const val COLUMN_NAME_LEXICAL = "lexical"
        const val COLUMN_NAME_KEY_PRONUNCIATIONS = "key_pronunciations"
    }

    object Translates {
        const val TABLE_NAME = "translates"
        const val COLUMN_NAME_TRANSLATE = "translate"
        const val COLUMN_NAME_LEXICAL = "lexical"
        const val COLUMN_NAME_KEY_TRANSLATES = "key_translates"
    }

    object Etymologies {
        const val TABLE_NAME = "etymologies"
        const val COLUMN_NAME_ETYMOLOGY = "etymology"
        const val COLUMN_NAME_LEXICAL = "lexical"
        const val COLUMN_NAME_KEY_ETYMOLOGIES = "key_etymologies"
    }
}

private const val SQL_CREATE_WORD_TABLE =
    "CREATE TABLE ${DictionaryContract.TABLE_NAME} (" +
            "${DictionaryContract.ID} INTEGER PRIMARY KEY," +
            "${DictionaryContract.COLUMN_NAME_WORD} TEXT UNIQUE)"

private const val SQL_DELETE_WORD_TABLE = "DROP TABLE IF EXISTS ${DictionaryContract.TABLE_NAME}"

private const val SQL_CREATE_PRONUNCIATIONS_TABLE =
    "CREATE TABLE ${DictionaryContract.Pronunciations.TABLE_NAME} (" +
            "${DictionaryContract.ID} INTEGER PRIMARY KEY," +
            "${DictionaryContract.Pronunciations.COLUMN_NAME_SPELLING} TEXT," +
            "${DictionaryContract.Pronunciations.COLUMN_NAME_AUDIO} TEXT," +
            "${DictionaryContract.Pronunciations.COLUMN_NAME_LEXICAL} TEXT," +
            "${DictionaryContract.Pronunciations.COLUMN_NAME_KEY_PRONUNCIATIONS} INTEGER)"

private const val SQL_DELETE_PRONUNCIATIONS_TABLE =
    "DROP TABLE IF EXISTS ${DictionaryContract.Pronunciations.TABLE_NAME}"

private const val SQL_CREATE_TRANSLATIONS_TABLE =
    "CREATE TABLE ${DictionaryContract.Translates.TABLE_NAME} (" +
            "${DictionaryContract.ID} INTEGER PRIMARY KEY," +
            "${DictionaryContract.Translates.COLUMN_NAME_TRANSLATE} TEXT," +
            "${DictionaryContract.Translates.COLUMN_NAME_LEXICAL} TEXT," +
            "${DictionaryContract.Translates.COLUMN_NAME_KEY_TRANSLATES} INTEGER)"

private const val SQL_DELETE_TRANSLATIONS_TABLE =
    "DROP TABLE IF EXISTS ${DictionaryContract.Translates.TABLE_NAME}"

private const val SQL_CREATE_ETYMOLOGIES_TABLE =
    "CREATE TABLE ${DictionaryContract.Etymologies.TABLE_NAME} (" +
            "${DictionaryContract.ID} INTEGER PRIMARY KEY," +
            "${DictionaryContract.Etymologies.COLUMN_NAME_ETYMOLOGY} TEXT," +
            "${DictionaryContract.Etymologies.COLUMN_NAME_LEXICAL} TEXT," +
            "${DictionaryContract.Etymologies.COLUMN_NAME_KEY_ETYMOLOGIES} INTEGER)"

private const val SQL_DELETE_ETYMOLOGIES_TABLE =
    "DROP TABLE IF EXISTS ${DictionaryContract.Etymologies.TABLE_NAME}"


class DictionaryWordDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_WORD_TABLE)
        db.execSQL(SQL_CREATE_PRONUNCIATIONS_TABLE)
        db.execSQL(SQL_CREATE_TRANSLATIONS_TABLE)
        db.execSQL(SQL_CREATE_ETYMOLOGIES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_WORD_TABLE)
        db.execSQL(SQL_DELETE_PRONUNCIATIONS_TABLE)
        db.execSQL(SQL_DELETE_TRANSLATIONS_TABLE)
        db.execSQL(SQL_DELETE_ETYMOLOGIES_TABLE)
        onCreate(db)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "DictionaryWordTest.db"
    }
}


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedTests {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var waterWord: DictionaryWord
    private lateinit var fireWord: DictionaryWord
    private lateinit var earthWord: DictionaryWord

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

        waterWord = DictionaryWord(
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
                listOf("волны", "поливать", "мочить"),
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

        fireWord = DictionaryWord(
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

        earthWord = DictionaryWord(
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
    }

    @Test
    fun testGetDictionaryWordFromSql() {
        val dbHelper = DictionaryWordDbHelper(context)
        val db = dbHelper.readableDatabase

        printDictionaryWord(getDictionaryWordFromSql(db, "fire"))
        dbHelper.close()
    }

    @Test
    fun testPutDictionaryWordToSql() {
        val dbHelper = DictionaryWordDbHelper(context)
        val db = dbHelper.readableDatabase
        putDictionaryWordToSql(db, waterWord)

        printDictionaryWord(getDictionaryWordFromSql(db, waterWord.word))
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

fun putDictionaryWordToSql(db: SQLiteDatabase, dictionaryWord: DictionaryWord) {
    fun putWord(db: SQLiteDatabase, word: String): Long {
        var key: Long
        ContentValues().apply {
            put(DictionaryContract.COLUMN_NAME_WORD, word)
        }.let {
            key = db.insertWithOnConflict(DictionaryContract.TABLE_NAME, null, it, CONFLICT_IGNORE)
        }
        return key
    }

    fun putPronunciations(db: SQLiteDatabase, pronunciations: List<Pronunciation>, lexical: String, key: Long) {
        pronunciations.map {
            ContentValues().apply {
                put(DictionaryContract.Pronunciations.COLUMN_NAME_SPELLING, it.phoneticSpelling)
                put(DictionaryContract.Pronunciations.COLUMN_NAME_AUDIO, it.audioFile)
                put(DictionaryContract.Pronunciations.COLUMN_NAME_LEXICAL, lexical)
                put(DictionaryContract.Pronunciations.COLUMN_NAME_KEY_PRONUNCIATIONS, key)
            }
        }.forEach {
            db.insertWithOnConflict(
                DictionaryContract.Pronunciations.TABLE_NAME, null, it,
                CONFLICT_IGNORE
            )
        }
    }

    fun putTranslates(db: SQLiteDatabase, translates: List<String>, lexical: String, key: Long) {
        translates.map {
            ContentValues().apply {
                put(DictionaryContract.Translates.COLUMN_NAME_TRANSLATE, it)
                put(DictionaryContract.Translates.COLUMN_NAME_LEXICAL, lexical)
                put(DictionaryContract.Translates.COLUMN_NAME_KEY_TRANSLATES, key)
            }
        }.forEach {
            db.insertWithOnConflict(DictionaryContract.Translates.TABLE_NAME, null, it, CONFLICT_IGNORE)
        }
    }

    fun putEtymologies(db: SQLiteDatabase, etymologies: List<String>, lexical: String, key: Long) {
        etymologies.map {
            ContentValues().apply {
                put(DictionaryContract.Etymologies.COLUMN_NAME_ETYMOLOGY, it)
                put(DictionaryContract.Etymologies.COLUMN_NAME_LEXICAL, lexical)
                put(DictionaryContract.Etymologies.COLUMN_NAME_KEY_ETYMOLOGIES, key)
            }
        }.forEach {
            db.insertWithOnConflict(DictionaryContract.Etymologies.TABLE_NAME, null, it, CONFLICT_IGNORE)
        }
    }

    val key = putWord(db, dictionaryWord.word)
    listOf(
        Pair(dictionaryWord.noun, "noun"),
        Pair(dictionaryWord.verb, "verb"),
        Pair(dictionaryWord.adjective, "adjective")
    ).forEach {
        putPronunciations(db, it.first.pronunciations, it.second, key)
        putTranslates(db, it.first.translates, it.second, key)
        putEtymologies(db, it.first.etymologies, it.second, key)
    }
}

fun getDictionaryWordFromSql(db: SQLiteDatabase, word: String): DictionaryWord {
    fun getTranslates(db: SQLiteDatabase, lexical: String, key: Long): List<String> {
        val translates = mutableListOf<String>()

        db.query(
            DictionaryContract.Translates.TABLE_NAME,
            arrayOf(DictionaryContract.Translates.COLUMN_NAME_TRANSLATE),
            "${DictionaryContract.Translates.COLUMN_NAME_LEXICAL} = ? AND " +
                    "${DictionaryContract.Translates.COLUMN_NAME_KEY_TRANSLATES} = ?",
            arrayOf(lexical, key.toString()), null, null, null
        ).apply {
            while (moveToNext()) {
                translates += getString(getColumnIndex(DictionaryContract.Translates.COLUMN_NAME_TRANSLATE))
            }
        }

        return translates
    }

    fun getPronunciations(db: SQLiteDatabase, lexical: String, key: Long): List<Pronunciation> {
        val pronunciations = mutableListOf<Pronunciation>()

        db.query(
            DictionaryContract.Pronunciations.TABLE_NAME,
            arrayOf(
                DictionaryContract.Pronunciations.COLUMN_NAME_SPELLING,
                DictionaryContract.Pronunciations.COLUMN_NAME_AUDIO
            ),
            "${DictionaryContract.Pronunciations.COLUMN_NAME_LEXICAL} = ? AND " +
                    "${DictionaryContract.Pronunciations.COLUMN_NAME_KEY_PRONUNCIATIONS} = ?",
            arrayOf(lexical, key.toString()), null, null, null
        ).apply {
            while (moveToNext()) {
                pronunciations += Pronunciation(
                    getString(getColumnIndex(DictionaryContract.Pronunciations.COLUMN_NAME_SPELLING)),
                    getString(getColumnIndex(DictionaryContract.Pronunciations.COLUMN_NAME_AUDIO))
                )
            }
        }
        return pronunciations
    }

    fun getEtymologies(db: SQLiteDatabase, lexical: String, key: Long): List<String> {
        val etymologies = mutableListOf<String>()

        db.query(
            DictionaryContract.Etymologies.TABLE_NAME,
            arrayOf(DictionaryContract.Etymologies.COLUMN_NAME_ETYMOLOGY),
            "${DictionaryContract.Etymologies.COLUMN_NAME_LEXICAL} = ? AND " +
                    "${DictionaryContract.Etymologies.COLUMN_NAME_KEY_ETYMOLOGIES} = ?",
            arrayOf(lexical, key.toString()), null, null, null
        ).apply {
            while (moveToNext()) {
                etymologies += getString(getColumnIndex(DictionaryContract.Etymologies.COLUMN_NAME_ETYMOLOGY))
            }
        }

        return etymologies
    }

    fun getKey(db: SQLiteDatabase, word: String): Long {
        var key: Long = -1

        db.query(
            DictionaryContract.TABLE_NAME,
            arrayOf(DictionaryContract.ID),
            "${DictionaryContract.COLUMN_NAME_WORD} = ?",
            arrayOf(word), null, null, null
        ).apply {
            while (moveToNext()) {
                key = getLong(getColumnIndex(DictionaryContract.ID))
            }
        }

        return key
    }

    fun getDictionaryEntry(db: SQLiteDatabase, lexical: String, key: Long): DictionaryEntry {
        val translates = getTranslates(db, lexical, key)
        val pronunciations = getPronunciations(db, lexical, key)
        val etymologies = getEtymologies(db, lexical, key)
        return DictionaryEntry(translates, pronunciations, etymologies)
    }

    val key = getKey(db, word)
    val noun = getDictionaryEntry(db, "noun", key)
    val verb = getDictionaryEntry(db, "verb", key)
    val adjective = getDictionaryEntry(db, "adjective", key)
    return DictionaryWord(word, noun, verb, adjective)
}

/*
Delete information from a database

To delete rows from a table, you need to provide selection criteria that identify the rows to the delete() method.
The mechanism works the same as the selection arguments to the query() method. It divides the selection specification
into a selection clause and selection arguments. The clause defines the columns to look at, and also allows you to
combine column tests. The arguments are values to test against that are bound into the clause. Because the result
isn't handled the same as a regular SQL statement, it is immune to SQL injection.

// Define 'where' part of query.
val selection = "${FeedEntry.COLUMN_NAME_TITLE} LIKE ?"
// Specify arguments in placeholder order.
val selectionArgs = arrayOf("MyTitle")
// Issue SQL statement.
val deletedRows = db.delete(FeedEntry.TABLE_NAME, selection, selectionArgs)

The return value for the delete() method indicates the number of rows that were deleted from the database.
Update a database


Since getWritableDatabase() and getReadableDatabase() are expensive to call when the database is closed, you should
leave your database connection open for as long as you possibly need to access it. Typically, it is optimal to close
the database in the onDestroy() of the calling Activity.
Kotlin

override fun onDestroy() {
    dbHelper.close()
    super.onDestroy()
}
*/