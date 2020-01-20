import kotlinx.serialization.Serializable

@Serializable
data class YandexDictionaryModel(val head: Head, val def: List<Def>)

@Serializable
data class Head(val notUsed: String? = null)

@Serializable
data class Def(val text: String, val pos: String, val ts: String? = null, val tr: List<Tr>)

@Serializable
data class Tr(
    val text: String,
    val pos: String? = null,
    val syn: List<Syn>? = null,
    val gen: String? = null,
    val mean: List<Mean>? = null,
    val ex: List<Ex>? = null,
    val asp: String? = null
)

@Serializable
data class Syn(val text: String, val pos: String? = null, val gen: String? = null)

@Serializable
data class Mean(val text: String)

@Serializable
data class Ex(val text: String, val tr: List<Tr>)