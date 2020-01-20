package com.example.mydictionary

import kotlinx.serialization.Serializable

@Serializable
data class Domains(val id: String, val text: String)

@Serializable
data class Regions(val id: String, val text: String)

@Serializable
data class Registers(val id: String, val text: String)

@Serializable
data class DerivativeOf(
    val domains: List<Domains>,
    val id: String,
    val language: String,
    val regions: List<Regions>,
    val registers: List<Registers>,
    val text: String
)

@Serializable
data class Derivatives(
    val domains: List<Domains>? = null,
    val id: String,
    val language: String? = null,
    val regions: List<Regions>? = null,
    val registers: List<Registers>? = null,
    val text: String
)

@Serializable
data class GrammaticalFeatures(
    val id: String,
    val text: String,
    val type: String
)

@Serializable
data class Notes(
    val id: String? = null,
    val text: String,
    val type: String
)

@Serializable
data class Pronunciations(
    val audioFile: String? = null,
    val dialects: List<String>? = null,
    val phoneticNotation: String? = null,
    val phoneticSpelling: String? = null,
    val regions: List<Regions>? = null,
    val registers: List<Registers>? = null
)

@Serializable
data class Constructions(
    val domains: List<Domains>? = null,
    val examples: List<List<String>>? = null,
    val notes: List<Notes>? = null,
    val regions: List<Regions>? = null,
    val registers: List<Registers>? = null,
    val text: String
)

@Serializable
data class CrossReferences(
    val id: String,
    val text: String,
    val type: String
)

@Serializable
data class Examples(
    val definitions: List<String>? = null,
    val domains: List<Domains>? = null,
    val notes: List<Notes>? = null,
    val regions: List<Regions>? = null,
    val registers: List<Registers>? = null,
    val senseIds: List<String>? = null,
    val text: String
)

@Serializable
data class Subsenses(
    val crossReferenceMarkers: List<String>? = null,
    val crossReferences: List<CrossReferences>? = null,
    val definitions: List<String>? = null,
    val domains: List<Domains>? = null,
    val examples: List<Examples>? = null,
    val id: String,
    val shortDefinitions: List<String>? = null,
    val registers: List<Registers>? = null,
    val constructions: List<Constructions>? = null,
    val notes: List<Notes>? = null,
    val thesaurusLinks: List<ThesaurusLinks>? = null,
    val regions: List<Regions>? = null
)

@Serializable
data class ThesaurusLinks(
    val entry_id: String,
    val sense_id: String
)

@Serializable
data class VariantForms(
    val domains: List<Domains>? = null,
    val notes: List<Notes>? = null,
    val pronunciations: List<Pronunciations>? = null,
    val regions: List<Regions>? = null,
    val registers: List<Registers>? = null,
    val text: String? = null
)

@Serializable
data class Senses(
    val constructions: List<Constructions>? = null,
    val crossReferenceMarkers: List<String>? = null,
    val crossReferences: List<CrossReferences>? = null,
    val definitions: List<String>? = null,
    val domains: List<Domains>? = null,
    val etymologies: List<String>? = null,
    val examples: List<Examples>? = null,
    val id: String,
    val notes: List<Notes>? = null,
    val pronunciations: List<Pronunciations>? = null,
    val regions: List<Regions>? = null,
    val registers: List<Registers>? = null,
    val shortDefinitions: List<String>? = null,
    val subsenses: List<Subsenses>? = null,
    val thesaurusLinks: List<ThesaurusLinks>? = null,
    val variantForms: List<VariantForms>? = null
)

@Serializable
data class Entries(
    val etymologies: List<String>? = null,
    val grammaticalFeatures: List<GrammaticalFeatures>? = null,
    val homographNumber: String? = null,
    val notes: List<Notes>? = null,
    val pronunciations: List<Pronunciations>? = null,
    val senses: List<Senses>? = null,
    val variantForms: List<VariantForms>? = null
)

@Serializable
data class LexicalCategory(
    val id: String,
    val text: String
)

@Serializable
data class LexicalEntries(
    val derivativeOf: List<DerivativeOf>? = null,
    val derivatives: List<Derivatives>? = null,
    val entries: List<Entries>? = null,
    val grammaticalFeatures: List<GrammaticalFeatures>? = null,
    val language: String? = null,
    val lexicalCategory: LexicalCategory? = null,
    val notes: List<Notes>? = null,
    val pronunciations: List<Pronunciations>? = null,
    val text: String? = null,
    val variantForms: List<VariantForms>? = null
)

@Serializable
data class Metadata(
    val operation: String,
    val provider: String,
    val schema: String
)

@Serializable
data class Results(
    val id: String,
    val language: String,
    val lexicalEntries: List<LexicalEntries>,
    val pronunciations: List<Pronunciations>? = null,
    val type: String,
    val word: String
)

@Serializable
data class OxfordDictionaryModel(
    val id: String,
    val metadata: Metadata,
    val results: List<Results>,
    val word: String
)