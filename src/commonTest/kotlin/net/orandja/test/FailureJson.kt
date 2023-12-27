package net.orandja.test

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.orandja.failure.Failure
import net.orandja.failure.FailureException
import net.orandja.failure.failure
import kotlin.test.Test
import kotlin.test.assertEquals


class FailureJson {
    companion object {
        private const val TAG = "EXAMPLE"
        private const val CODE = 100
        private const val DESCRIPTION = "DESCRIPTION"
        private const val INFO = "INFO"
    }

    private val codec = Json { encodeDefaults = false }

    private val failureEmpty = failure(TAG)
    private val failureFull = failure(TAG, CODE, DESCRIPTION, INFO)
    private val failureAttach = failure(TAG, attached = setOf(failureEmpty, failureFull))
    private val failureException = failure(TAG, cause = Exception("AN EXCEPTION"))

    private val jsonEmpty = """{"id":"$TAG"}"""
    private val jsonFull = """{"id":"$TAG","code":$CODE,"description":"$DESCRIPTION","information":"$INFO"}"""
    private val jsonWithAttach = """{"id":"$TAG","attached":[$jsonEmpty,$jsonFull]}"""

    @Test
    fun serialize() {
        assertEquals(jsonEmpty, codec.encodeToString(failureEmpty))
        assertEquals(jsonFull, codec.encodeToString(failureFull))
        assertEquals(jsonWithAttach, codec.encodeToString(failureAttach))
        assertEquals(jsonEmpty, codec.encodeToString(failureException))
    }


    // TODO: Some build variant cannot serialize Exception even with @Serializable (Ex: ios, wasm)
    /*
    @Test
    fun serializeException() {
        // assertEquals(jsonEmpty, codec.encodeToString(catch { failureEmpty.throws() }))
        // assertEquals(jsonFull, codec.encodeToString(catch { failureFull.throws() }))
        // assertEquals(jsonWithAttach, codec.encodeToString(catch { failureAttach.throws() }))
        // assertEquals(jsonEmpty, codec.encodeToString(catch { failureException.throws() }))
    }
     */

    @Test
    fun deserialize() {
        assertEquals(codec.decodeFromString<Failure>(jsonEmpty), failureEmpty)
        assertEquals(codec.decodeFromString<Failure>(jsonFull), failureFull)
        assertEquals(codec.decodeFromString<Failure>(jsonWithAttach), failureAttach)
    }

    private fun catch(block: () -> Nothing): FailureException {
        try {
            block()
        } catch (e: FailureException) {
            return e
        }
    }
}