package com.kostrifon.mydictionary

class DictionariesModelFacade(
    private val oxfordDictionaryModel: OxfordDictionaryModel,
    private val yandexDictionaryModel: YandexDictionaryModel
) {
    fun getWord() = yandexDictionaryModel.def[0].text

    // results[].lexicalEntries[].pronunciations[].phoneticSpelling
    fun getPronunciations(): List<Pronunciation> {
        val pronunciations = mutableListOf<Pronunciation>()
        oxfordDictionaryModel.results.forEach { result ->
            result.lexicalEntries.forEach { lexicalEntire ->
                lexicalEntire.pronunciations?.forEach { pronunciation ->
                    val phoneticSpelling = pronunciation.phoneticSpelling ?: "none"
                    val audioFile = pronunciation.audioFile ?: "none"
                    pronunciations += Pronunciation(phoneticSpelling, audioFile)
                }
            }
        }
        return pronunciations
    }

    // def[].tr[].text
    fun getTranslates(): List<String> {
        val translations = mutableListOf<String>()
        yandexDictionaryModel.def.forEach { def ->
            def.tr.forEach { translation ->
                translations += translation.text
            }
        }
        return translations
    }

    // results[].lexicalEntries[].entries[].etymologies[]
    fun getEtymologies(): List<String> {
        val etymologies = mutableListOf<String>()
        oxfordDictionaryModel.results.forEach { result ->
            result.lexicalEntries.forEach { lexicalEntire ->
                lexicalEntire.entries?.forEach { entire ->
                    etymologies += entire.etymologies ?: listOf("none")
                }
            }
        }
        return etymologies
    }
}

class OxfordDictionaryModelFacade(private val oxfordDictionaryModel: OxfordDictionaryModel) {

    // results[].lexicalEntries[].pronunciations[].phoneticSpelling
    fun getPronunciations(): List<Pronunciation> {
        val pronunciations = mutableListOf<Pronunciation>()
        oxfordDictionaryModel.results.forEach { result ->
            result.lexicalEntries.forEach { lexicalEntire ->
                lexicalEntire.pronunciations?.forEach { pronunciation ->
                    val phoneticSpelling = pronunciation.phoneticSpelling ?: "none"
                    val audioFile = pronunciation.audioFile ?: "none"
                    pronunciations += Pronunciation(phoneticSpelling, audioFile)
                }
            }
        }
        return pronunciations
    }

    // results[].lexicalEntries[].entries[].etymologies[]
    fun getEtymologies(): List<String> {
        val etymologies = mutableListOf<String>()
        oxfordDictionaryModel.results.forEach { result ->
            result.lexicalEntries.forEach { lexicalEntire ->
                lexicalEntire.entries?.forEach { entire ->
                    etymologies += entire.etymologies ?: listOf("none")
                }
            }
        }
        return etymologies
    }
}

class YandexDictionaryModelFacade(private val yandexDictionaryModel: YandexDictionaryModel) {
    fun getWord() = yandexDictionaryModel.def[0].text

    // def[].tr[].text
    fun getTranslates(): List<String> {
        val translations = mutableListOf<String>()
        yandexDictionaryModel.def.forEach { def ->
            def.tr.forEach { translation ->
                translations += translation.text
            }
        }
        return translations
    }
}

data class Pronunciation(val phoneticSpelling: String, val audioFile: String)