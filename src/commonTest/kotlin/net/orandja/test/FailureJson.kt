package net.orandja.test

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.orandja.failure.Failure
import net.orandja.failure.failure
import kotlin.test.Test
import kotlin.test.assertEquals


class FailureJson {
    companion object {
        private const val TAG = "EXAMPLE"
        private const val DESCRIPTION = "DESCRIPTION"
        private const val INFO = "INFO"
    }

    private val codec = Json { encodeDefaults = false }

    private val failureEmpty = failure(TAG)
    private val failureFull = failure(TAG, DESCRIPTION, INFO)
    private val failureAttach = failure(TAG, attached = setOf(failureEmpty, failureFull))
    private val failureException = failure(TAG, cause = Exception("AN EXCEPTION"))

    private val jsonEmpty = """{"id":"$TAG"}"""
    private val jsonFull = """{"id":"$TAG","description":"$DESCRIPTION","information":"$INFO"}"""
    private val jsonWithAttach = """{"id":"$TAG","attached":[$jsonEmpty,$jsonFull]}"""

    @Test
    fun serialize() {
        assertEquals(jsonEmpty, codec.encodeToString(failureEmpty))
        assertEquals(jsonFull, codec.encodeToString(failureFull))
        assertEquals(jsonWithAttach, codec.encodeToString(failureAttach))
        assertEquals(jsonEmpty, codec.encodeToString(failureException))
    }

    @Test
    fun deserialize() {
        assertEquals(codec.decodeFromString<Failure>(jsonEmpty), failureEmpty)
        assertEquals(codec.decodeFromString<Failure>(jsonFull), failureFull)
        assertEquals(codec.decodeFromString<Failure>(jsonWithAttach), failureAttach)
    }
}