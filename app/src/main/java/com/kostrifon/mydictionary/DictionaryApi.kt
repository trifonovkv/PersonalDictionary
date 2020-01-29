package com.kostrifon.mydictionary

interface DictionaryApi<T> {
    suspend fun getWord(requestedWord: RequestedWord): Result<T>
}