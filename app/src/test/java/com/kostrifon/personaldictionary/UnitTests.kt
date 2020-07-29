package com.kostrifon.personaldictionary

import io.mockk.every
import io.mockk.mockk
import org.junit.Test


class UnitTests {
    private var testWord = "water"

    @Test
    fun testParseOxfordDictionaryWord() {
        val mockJson = "{\n" +
                "  \"id\": \"water\",\n" +
                "  \"metadata\": {\n" +
                "    \"operation\": \"retrieve\",\n" +
                "    \"provider\": \"Oxford University Press\",\n" +
                "    \"schema\": \"RetrieveEntry\"\n" +
                "  },\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"id\": \"water\",\n" +
                "      \"language\": \"en-us\",\n" +
                "      \"lexicalEntries\": [\n" +
                "        {\n" +
                "          \"entries\": [\n" +
                "            {\n" +
                "              \"etymologies\": [\n" +
                "                \"Old English wæter (noun), wæterian (verb), of Germanic origin; related to Dutch water, German Wasser, from an Indo-European root shared by Russian voda (compare with vodka), also by Latin unda ‘wave’ and Greek hudōr ‘water’\"\n" +
                "              ],\n" +
                "              \"pronunciations\": [\n" +
                "                {\n" +
                "                  \"dialects\": [\n" +
                "                    \"American English\"\n" +
                "                  ],\n" +
                "                  \"phoneticNotation\": \"respell\",\n" +
                "                  \"phoneticSpelling\": \"ˈwôdər\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"audioFile\": \"https://audio.oxforddictionaries.com/en/mp3/water_us_1_rr.mp3\",\n" +
                "                  \"dialects\": [\n" +
                "                    \"American English\"\n" +
                "                  ],\n" +
                "                  \"phoneticNotation\": \"IPA\",\n" +
                "                  \"phoneticSpelling\": \"ˈwɔdər\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"dialects\": [\n" +
                "                    \"American English\"\n" +
                "                  ],\n" +
                "                  \"phoneticNotation\": \"respell\",\n" +
                "                  \"phoneticSpelling\": \"ˈwädər\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"audioFile\": \"https://audio.oxforddictionaries.com/en/mp3/water_us_2_rr.mp3\",\n" +
                "                  \"dialects\": [\n" +
                "                    \"American English\"\n" +
                "                  ],\n" +
                "                  \"phoneticNotation\": \"IPA\",\n" +
                "                  \"phoneticSpelling\": \"ˈwɑdər\"\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n" +
                "          ],\n" +
                "          \"language\": \"en-us\",\n" +
                "          \"lexicalCategory\": {\n" +
                "            \"id\": \"noun\",\n" +
                "            \"text\": \"Noun\"\n" +
                "          },\n" +
                "          \"text\": \"water\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"entries\": [\n" +
                "            {\n" +
                "              \"pronunciations\": [\n" +
                "                {\n" +
                "                  \"dialects\": [\n" +
                "                    \"American English\"\n" +
                "                  ],\n" +
                "                  \"phoneticNotation\": \"respell\",\n" +
                "                  \"phoneticSpelling\": \"ˈwôdər\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"audioFile\": \"https://audio.oxforddictionaries.com/en/mp3/water_us_1_rr.mp3\",\n" +
                "                  \"dialects\": [\n" +
                "                    \"American English\"\n" +
                "                  ],\n" +
                "                  \"phoneticNotation\": \"IPA\",\n" +
                "                  \"phoneticSpelling\": \"ˈwɔdər\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"dialects\": [\n" +
                "                    \"American English\"\n" +
                "                  ],\n" +
                "                  \"phoneticNotation\": \"respell\",\n" +
                "                  \"phoneticSpelling\": \"ˈwädər\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"audioFile\": \"https://audio.oxforddictionaries.com/en/mp3/water_us_2_rr.mp3\",\n" +
                "                  \"dialects\": [\n" +
                "                    \"American English\"\n" +
                "                  ],\n" +
                "                  \"phoneticNotation\": \"IPA\",\n" +
                "                  \"phoneticSpelling\": \"ˈwɑdər\"\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n" +
                "          ],\n" +
                "          \"language\": \"en-us\",\n" +
                "          \"lexicalCategory\": {\n" +
                "            \"id\": \"verb\",\n" +
                "            \"text\": \"Verb\"\n" +
                "          },\n" +
                "          \"text\": \"water\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"type\": \"headword\",\n" +
                "      \"word\": \"water\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"word\": \"water\"\n" +
                "}"
        val oxfordDictionaryModel = parseOxfordDictionaryModel(mockJson)
        val oxfordDictionaryWord = getOxfordDictionaryWord(oxfordDictionaryModel)

        assert(compareOxfordDictionary(oxfordDictionaryModel, oxfordDictionaryWord))
    }

    @Test
    fun testParseYandexDictionaryWord() {
        val mockJson = "{\n" +
                "  \"head\": {},\n" +
                "  \"def\": [\n" +
                "    {\n" +
                "      \"text\": \"dog\",\n" +
                "      \"pos\": \"noun\",\n" +
                "      \"ts\": \"dɒg\",\n" +
                "      \"tr\": [\n" +
                "        {\n" +
                "          \"text\": \"собака\",\n" +
                "          \"pos\": \"noun\",\n" +
                "          \"gen\": \"ж\",\n" +
                "          \"syn\": [\n" +
                "            {\n" +
                "              \"text\": \"пес\",\n" +
                "              \"pos\": \"noun\",\n" +
                "              \"gen\": \"м\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"text\": \"собачка\",\n" +
                "              \"pos\": \"noun\",\n" +
                "              \"gen\": \"ж\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"text\": \"песик\",\n" +
                "              \"pos\": \"noun\",\n" +
                "              \"gen\": \"м\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"text\": \"псина\",\n" +
                "              \"pos\": \"noun\",\n" +
                "              \"gen\": \"м,ж\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"mean\": [\n" +
                "            {\n" +
                "              \"text\": \"hound\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"text\": \"doggy\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"text\": \"doggie\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"ex\": [\n" +
                "            {\n" +
                "              \"text\": \"dog show\",\n" +
                "              \"tr\": [\n" +
                "                {\n" +
                "                  \"text\": \"выставка собак\"\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            {\n" +
                "              \"text\": \"large breed dogs\",\n" +
                "              \"tr\": [\n" +
                "                {\n" +
                "                  \"text\": \"собаки крупных пород\"\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            {\n" +
                "              \"text\": \"pack of stray dogs\",\n" +
                "              \"tr\": [\n" +
                "                {\n" +
                "                  \"text\": \"стая бродячих собак\"\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            {\n" +
                "              \"text\": \"dog of medium size\",\n" +
                "              \"tr\": [\n" +
                "                {\n" +
                "                  \"text\": \"собака среднего размера\"\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            {\n" +
                "              \"text\": \"wild dog\",\n" +
                "              \"tr\": [\n" +
                "                {\n" +
                "                  \"text\": \"дикая собака\"\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            {\n" +
                "              \"text\": \"big black dog\",\n" +
                "              \"tr\": [\n" +
                "                {\n" +
                "                  \"text\": \"большой черный пес\"\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            {\n" +
                "              \"text\": \"prairie dog\",\n" +
                "              \"tr\": [\n" +
                "                {\n" +
                "                  \"text\": \"луговая собачка\"\n" +
                "                }\n" +
                "              ]\n" +
                "            },\n" +
                "            {\n" +
                "              \"text\": \"wet dog\",\n" +
                "              \"tr\": [\n" +
                "                {\n" +
                "                  \"text\": \"мокрая псина\"\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"text\": \"кобель\",\n" +
                "          \"pos\": \"noun\",\n" +
                "          \"gen\": \"м\",\n" +
                "          \"mean\": [\n" +
                "            {\n" +
                "              \"text\": \"male\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"ex\": [\n" +
                "            {\n" +
                "              \"text\": \"stud dog\",\n" +
                "              \"tr\": [\n" +
                "                {\n" +
                "                  \"text\": \"племенной кобель\"\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"text\": \"дог\",\n" +
                "          \"pos\": \"noun\",\n" +
                "          \"gen\": \"м\",\n" +
                "          \"mean\": [\n" +
                "            {\n" +
                "              \"text\": \"great dane\"\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"text\": \"шавка\",\n" +
                "          \"pos\": \"noun\",\n" +
                "          \"gen\": \"ж\",\n" +
                "          \"mean\": [\n" +
                "            {\n" +
                "              \"text\": \"mongrel\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"text\": \"dog\",\n" +
                "      \"pos\": \"verb\",\n" +
                "      \"ts\": \"dɒg\",\n" +
                "      \"tr\": [\n" +
                "        {\n" +
                "          \"text\": \"выслеживать\",\n" +
                "          \"pos\": \"verb\",\n" +
                "          \"asp\": \"несов\",\n" +
                "          \"mean\": [\n" +
                "            {\n" +
                "              \"text\": \"track\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"text\": \"dog\",\n" +
                "      \"pos\": \"adjective\",\n" +
                "      \"ts\": \"dɒg\",\n" +
                "      \"tr\": [\n" +
                "        {\n" +
                "          \"text\": \"собачий\",\n" +
                "          \"pos\": \"adjective\",\n" +
                "          \"syn\": [\n" +
                "            {\n" +
                "              \"text\": \"кинологический\",\n" +
                "              \"pos\": \"adjective\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"mean\": [\n" +
                "            {\n" +
                "              \"text\": \"canine\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"text\": \"cynological\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"ex\": [\n" +
                "            {\n" +
                "              \"text\": \"sled dog race\",\n" +
                "              \"tr\": [\n" +
                "                {\n" +
                "                  \"text\": \"гонка на собачьих упряжках\"\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"text\": \"dog\",\n" +
                "      \"pos\": \"adverb\",\n" +
                "      \"ts\": \"dɒg\",\n" +
                "      \"tr\": [\n" +
                "        {\n" +
                "          \"text\": \"собачьи\",\n" +
                "          \"pos\": \"adverb\",\n" +
                "          \"mean\": [\n" +
                "            {\n" +
                "              \"text\": \"canine\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}"
        parseYandexDictionaryModel(mockJson).let {
            assert(compareYandexDictionary(it, getYandexDictionaryWord(it)))
        }
    }

    @ExperimentalStdlibApi
    @Test
    fun testGetTranslatedWord() {
        getTranslatedWord(testWord, { dictionaryWord: DictionaryWord ->
            assert(true)
            printDictionaryWord(dictionaryWord)
        }, { message: String -> assert(false) { println(message) } }
        )
    }

    @Test
    fun testGetUniquePronunciations() {
        val mockDictionaryWord = mockk<DictionaryWord>()
        every { mockDictionaryWord.word } returns "water"
        every { mockDictionaryWord.noun.translates } returns listOf(
            "вода",
            "водоем",
            "акватория",
            "влага",
            "водность",
            "волны"
        )
        every { mockDictionaryWord.noun.pronunciations } returns listOf(
            Pronunciation("ˈwôdər", ""),
            Pronunciation("ˈwɔdər", "https://audio.oxforddictionaries.com/en/mp3/water_us_1_rr.mp3"),
            Pronunciation("ˈwädər", ""),
            Pronunciation("ˈwɑdər", "https://audio.oxforddictionaries.com/en/mp3/water_us_2_rr.mp3")
        )
        every { mockDictionaryWord.noun.etymologies } returns listOf("Old English wæter (noun), wæterian (verb), of Germanic origin; related to Dutch water, German Wasser, from an Indo-European root shared by Russian voda (compare with vodka), also by Latin unda ‘wave’ and Greek hudōr ‘water’")
        every { mockDictionaryWord.verb.translates } returns listOf("поливать", "мочить")
        every { mockDictionaryWord.verb.pronunciations } returns listOf(
            Pronunciation("ˈwôdər", ""),
            Pronunciation("ˈwɔdər", "https://audio.oxforddictionaries.com/en/mp3/water_us_1_rr.mp3"),
            Pronunciation("ˈwädər", ""),
            Pronunciation("ˈwɑdər", "https://audio.oxforddictionaries.com/en/mp3/water_us_2_rr.mp3")
        )
        every { mockDictionaryWord.adjective.translates } returns listOf("водяной")
        every { mockDictionaryWord.adjective.pronunciations } returns listOf()
        getUniquePronunciations(mockDictionaryWord).let {
            assert(it.size == it.toSet().size) {
                println("List of pronunciations contain duplicates")
            }
            println(it.joinToString(separator = "\n"))
        }
    }
}

fun compareOxfordDictionary(model: OxfordDictionaryModel, word: OxfordDictionaryWord): Boolean {

    fun getLexicalCategoryCount(oxfordDictionaryModel: OxfordDictionaryModel): Int {
        var lexicalCategoryCount = 0
        oxfordDictionaryModel.results.forEach {
            it.lexicalEntries.forEach { it1 ->
                if (it1.lexicalCategory != null) lexicalCategoryCount++
            }
        }
        return lexicalCategoryCount
    }

    fun getLexicalCategoryCount(oxfordDictionaryWord: OxfordDictionaryWord): Int {
        var lexicalCategoryCount = 0
        oxfordDictionaryWord.entries.forEach {
            if (it.lexicalCategory != null) lexicalCategoryCount++
        }
        return lexicalCategoryCount
    }

    fun getPronunciationsCount(oxfordDictionaryModel: OxfordDictionaryModel): Int {
        var pronunciationsCount = 0
        oxfordDictionaryModel.results.forEach {
            it.lexicalEntries.forEach { it1 ->
                it1.entries?.forEach { it2 ->
                    if (it2.pronunciations != null) pronunciationsCount++
                }
            }
        }
        return pronunciationsCount
    }

    fun getPronunciationsCount(oxfordDictionaryWord: OxfordDictionaryWord): Int {
        var pronunciationsCount = 0
        oxfordDictionaryWord.entries.forEach {
            if (it.pronunciations != null) pronunciationsCount++
        }
        return pronunciationsCount
    }

    fun getEtymologiesCount(oxfordDictionaryModel: OxfordDictionaryModel): Int {
        var etymologiesCount = 0
        oxfordDictionaryModel.results.forEach {
            it.lexicalEntries.forEach { it1 ->
                it1.entries?.forEach { _ -> etymologiesCount++ }
            }
        }
        return etymologiesCount
    }

    fun getEtymologiesCount(oxfordDictionaryWord: OxfordDictionaryWord): Int {
        var etymologiesCount = 0
        repeat(oxfordDictionaryWord.entries.size) { etymologiesCount++ }
        return etymologiesCount
    }

    return (getLexicalCategoryCount(model) == getLexicalCategoryCount(word)) == (
            getPronunciationsCount(model) == getPronunciationsCount(word)) == (
            getEtymologiesCount(model) == getEtymologiesCount(word))
}

fun compareYandexDictionary(model: YandexDictionaryModel, word: YandexDictionaryWord): Boolean {

    fun getTranslationsCount(yandexDictionaryModel: YandexDictionaryModel): Int {
        var count = 0
        yandexDictionaryModel.def.forEach { count += it.tr.size }
        return count
    }

    fun getTranslationsCount(yandexDictionaryWord: YandexDictionaryWord): Int {
        var count = 0
        yandexDictionaryWord.entries.forEach { count += it.translations.size }
        return count
    }

    return (getTranslationsCount(model) == getTranslationsCount(word)) == (model.def.size == word.entries.size)
}

fun printDictionaryWord(dictionaryWord: DictionaryWord) {

    fun dictionaryEntryToString(dictionaryEntry: DictionaryEntry): String {
        var string = ""

        fun etymologiesToString(etymologies: List<String>): String {
            if (etymologies.isEmpty()) return ""
            var s = "\t\tEtymologies:\n"
            etymologies.forEach { s = s.plus("\t\t\t${it}\n") }
            return s
        }

        fun pronunciationsToString(pronunciations: List<Pronunciation>): String {
            if (pronunciations.isEmpty()) return ""
            var s = "\t\tPronunciations:\n"
            pronunciations.forEach { s = s.plus("\t\t\t${it.phoneticSpelling} ${it.audioFile}\n") }
            return s
        }

        fun translatesToString(translates: List<String>) =
            if (translates.isEmpty()) {
                ""
            } else {
                "\t\tTranslates:\n".plus("\t\t\t${translates.joinToString()}\n")
            }

        string = string.plus(translatesToString(dictionaryEntry.translates))
        string = string.plus(pronunciationsToString(dictionaryEntry.pronunciations))
        string = string.plus(etymologiesToString(dictionaryEntry.etymologies))

        return string
    }

    println("Word: ${dictionaryWord.word}")
    dictionaryEntryToString(dictionaryWord.noun).let { if (it.isNotBlank()) println("\tNoun:\n $it") }
    dictionaryEntryToString(dictionaryWord.verb).let { if (it.isNotBlank()) println("\tVerb:\n $it") }
    dictionaryEntryToString(dictionaryWord.adjective).let { if (it.isNotBlank()) println("\tAdjective:\n $it") }
    println()
}