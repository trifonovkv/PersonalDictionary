import com.example.mydictionary.BuildConfig
import com.example.mydictionary.OxfordDictionaryApi
import com.example.mydictionary.OxfordDictionaryResponse
import com.example.mydictionary.RequestedWord
import kotlinx.coroutines.runBlocking
import org.junit.Test


class OxfordDictionaryApiTest {

    private val oxfordDictionaryApi = OxfordDictionaryApi(
        BuildConfig.OXFORD_APP_ID,
        BuildConfig.OXFORD_APP_KEY
    )

    @Test
    fun oxfordDictionaryApi() {
        val requestedWord = RequestedWord("oxford","pronunciations,etymologies")
        when (val oxfordDictionaryResponse = runBlocking { oxfordDictionaryApi.getWord(requestedWord) }) {
            is OxfordDictionaryResponse.Success -> assert(oxfordDictionaryResponse.oxfordDictionaryModel.results.isNotEmpty())
            is OxfordDictionaryResponse.Failure -> assert(false) { oxfordDictionaryResponse.message }
        }
    }
}