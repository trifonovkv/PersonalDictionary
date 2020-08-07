package com.kostrifon.personaldictionary

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


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

private const val SQL_CREATE_PRONUNCIATIONS_TABLE =
    "CREATE TABLE ${DictionaryContract.Pronunciations.TABLE_NAME} (" +
            "${DictionaryContract.ID} INTEGER PRIMARY KEY," +
            "${DictionaryContract.Pronunciations.COLUMN_NAME_SPELLING} TEXT," +
            "${DictionaryContract.Pronunciations.COLUMN_NAME_AUDIO} TEXT," +
            "${DictionaryContract.Pronunciations.COLUMN_NAME_LEXICAL} TEXT," +
            "${DictionaryContract.Pronunciations.COLUMN_NAME_KEY_PRONUNCIATIONS} INTEGER)"

private const val SQL_CREATE_TRANSLATIONS_TABLE =
    "CREATE TABLE ${DictionaryContract.Translates.TABLE_NAME} (" +
            "${DictionaryContract.ID} INTEGER PRIMARY KEY," +
            "${DictionaryContract.Translates.COLUMN_NAME_TRANSLATE} TEXT," +
            "${DictionaryContract.Translates.COLUMN_NAME_LEXICAL} TEXT," +
            "${DictionaryContract.Translates.COLUMN_NAME_KEY_TRANSLATES} INTEGER)"

private const val SQL_CREATE_ETYMOLOGIES_TABLE =
    "CREATE TABLE ${DictionaryContract.Etymologies.TABLE_NAME} (" +
            "${DictionaryContract.ID} INTEGER PRIMARY KEY," +
            "${DictionaryContract.Etymologies.COLUMN_NAME_ETYMOLOGY} TEXT," +
            "${DictionaryContract.Etymologies.COLUMN_NAME_LEXICAL} TEXT," +
            "${DictionaryContract.Etymologies.COLUMN_NAME_KEY_ETYMOLOGIES} INTEGER)"

private const val SQL_DELETE_WORD_TABLE = "DROP TABLE IF EXISTS ${DictionaryContract.TABLE_NAME}"
private const val SQL_DELETE_PRONUNCIATIONS_TABLE =
    "DROP TABLE IF EXISTS ${DictionaryContract.Pronunciations.TABLE_NAME}"
private const val SQL_DELETE_TRANSLATIONS_TABLE =
    "DROP TABLE IF EXISTS ${DictionaryContract.Translates.TABLE_NAME}"
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

fun getDictionaryWordFromDb(db: SQLiteDatabase, word: String): DictionaryWord {
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
            close()
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
            close()
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
            close()
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
            close()
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

fun putDictionaryWordToDb(db: SQLiteDatabase, dictionaryWord: DictionaryWord): Int {
    fun putWord(db: SQLiteDatabase, word: String): Long {
        ContentValues().apply {
            put(DictionaryContract.COLUMN_NAME_WORD, word)
        }.let {
            return db.insertWithOnConflict(DictionaryContract.TABLE_NAME, null, it, SQLiteDatabase.CONFLICT_IGNORE)
        }
    }

    fun putPronunciations(db: SQLiteDatabase, pronunciations: List<Pronunciation>, lexical: String, key: Long): Int {
        pronunciations.map {
            ContentValues().apply {
                put(DictionaryContract.Pronunciations.COLUMN_NAME_SPELLING, it.phoneticSpelling)
                put(DictionaryContract.Pronunciations.COLUMN_NAME_AUDIO, it.audioFile)
                put(DictionaryContract.Pronunciations.COLUMN_NAME_LEXICAL, lexical)
                put(DictionaryContract.Pronunciations.COLUMN_NAME_KEY_PRONUNCIATIONS, key)
            }
        }.forEach {
            if (db.insert(DictionaryContract.Pronunciations.TABLE_NAME, null, it) < 0) return -1
        }
        return 0
    }

    fun putTranslates(db: SQLiteDatabase, translates: List<String>, lexical: String, key: Long): Int {
        translates.map {
            ContentValues().apply {
                put(DictionaryContract.Translates.COLUMN_NAME_TRANSLATE, it)
                put(DictionaryContract.Translates.COLUMN_NAME_LEXICAL, lexical)
                put(DictionaryContract.Translates.COLUMN_NAME_KEY_TRANSLATES, key)
            }
        }.forEach {
            if (db.insert(DictionaryContract.Translates.TABLE_NAME, null, it) < 0) return -1
        }
        return 0
    }

    fun putEtymologies(db: SQLiteDatabase, etymologies: List<String>, lexical: String, key: Long): Int {
        etymologies.map {
            ContentValues().apply {
                put(DictionaryContract.Etymologies.COLUMN_NAME_ETYMOLOGY, it)
                put(DictionaryContract.Etymologies.COLUMN_NAME_LEXICAL, lexical)
                put(DictionaryContract.Etymologies.COLUMN_NAME_KEY_ETYMOLOGIES, key)
            }
        }.forEach {
            if (db.insert(DictionaryContract.Etymologies.TABLE_NAME, null, it) < 0) return -1
        }
        return 0
    }

    val key = putWord(db, dictionaryWord.word)
    if (key < 0) return -1
    listOf(
        Pair(dictionaryWord.noun, "noun"),
        Pair(dictionaryWord.verb, "verb"),
        Pair(dictionaryWord.adjective, "adjective")
    ).forEach {
        if (putPronunciations(db, it.first.pronunciations, it.second, key) < 0) return -1
        if (putTranslates(db, it.first.translates, it.second, key) < 0) return -1
        if (putEtymologies(db, it.first.etymologies, it.second, key) < 0) return -1
    }
    return 0
}

fun getAllWordsFromDb(db: SQLiteDatabase): List<String> {
    val words = mutableListOf<String>()
    db.query(
        DictionaryContract.TABLE_NAME,
        arrayOf("*"), null, null, null, null, null
    ).apply {
        while (moveToNext()) {
            words += getString(getColumnIndex(DictionaryContract.COLUMN_NAME_WORD))
        }
        close()
    }
    return words
}

fun getAllDictionaryWordsFromDb(db: SQLiteDatabase) = getAllWordsFromDb(db).map { getDictionaryWordFromDb(db, it) }