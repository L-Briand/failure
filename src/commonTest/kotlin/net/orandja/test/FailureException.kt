package net.orandja.test

import kotlinx.serialization.builtins.ByteArraySerializer
import net.orandja.failure.Failure
import net.orandja.failure.FailureException
import net.orandja.failure.failure
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class FailureException {

    companion object {
        private const val TAG = "EXAMPLE"
        private const val CODE = 100
        private const val DESCRIPTION = "DESCRIPTION"
        private const val INFO = "INFO"
    }

    private val cause = Exception("AN EXCEPTION")
    private val failureEmpty = failure(TAG)
    private val failureFull = failure(TAG, CODE, DESCRIPTION, INFO)
    private val failureAttach1 = failure(TAG, attached = setOf(failureEmpty, failureFull))
    private val failureAttach2 = failure(TAG, attached = setOf(failureAttach1))
    private val failureException = failure(TAG, cause = cause)
    private val failurePayload = failure(TAG, payload = "PAYLOAD")

    @Test
    fun empty() {
        val exception = catch { failureEmpty.throws() }
        assertFailureEquals(failureEmpty, exception)
        assertEquals(TAG, exception.message)
        assertEquals(null, exception.cause)
    }

    @Test
    fun full() {
        ByteArraySerializer()
        val exception = catch { failureFull.throws() }
        assertFailureEquals(failureFull, exception)
        assertEquals(null, exception.cause)
        assertEquals("$TAG [$CODE] ($DESCRIPTION) $INFO", exception.message)
    }

    @Test
    fun attach() {
        val exception = catch { failureAttach1.throws() }
        assertFailureEquals(failureAttach1, exception)
        assertEquals(null, exception.cause)
        val message = """
            $TAG
            > $TAG
            > $TAG [$CODE] ($DESCRIPTION) $INFO
        """.trimIndent()
        assertEquals(message, exception.message)
    }

    @Test
    fun attach2() {
        val exception = catch { failureAttach2.throws() }
        assertFailureEquals(failureAttach2, exception)
        assertEquals(null, exception.cause)
        val message = """
            $TAG
            > $TAG
            > > $TAG
            > > $TAG [$CODE] ($DESCRIPTION) $INFO
        """.trimIndent()
        assertEquals(message, exception.message)
    }

    @Test
    fun exception() {
        val exception = catch { failureException.throws() }
        assertFailureEquals(failureException, exception)
        assertEquals(TAG, exception.message)
        assertEquals(cause, exception.cause)
    }

    @Test
    fun exceptionEquality() {
        val failure2: FailureException = catch { failureEmpty.throws() }
        assertEquals(failureEmpty.id, failure2.id)
        assertEquals(failureEmpty, failure2.defaults())
        assertNotEquals<Failure>(failureEmpty, failure2)
    }

    @Test
    fun payload() {
        val exception = catch { failurePayload.throws() }
        assertFailureEquals(failurePayload, exception)
        assertEquals(null, exception.cause)
        assertEquals("PAYLOAD", exception.payload)
    }

    private fun catch(block: () -> Nothing): FailureException {
        try {
            block()
        } catch (e: FailureException) {
            return e
        }
    }
}