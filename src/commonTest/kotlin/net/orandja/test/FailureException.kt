package net.orandja.test

import net.orandja.failure.FailureException
import net.orandja.failure.failure
import kotlin.test.Test
import kotlin.test.assertEquals

class FailureException {

    companion object {
        private const val TAG = "EXAMPLE"
        private const val DESCRIPTION = "DESCRIPTION"
        private const val INFO = "INFO"
    }

    private val cause = Exception("AN EXCEPTION")
    private val failureEmpty = failure(TAG)
    private val failureFull = failure(TAG, DESCRIPTION, INFO)
    private val failureAttach1 = failure(TAG, attached = setOf(failureEmpty, failureFull))
    private val failureAttach2 = failure(TAG, attached = setOf(failureAttach1))
    private val failureException = failure(TAG, cause = cause)

    @Test
    fun empty() {
        val exception = catch { failureEmpty.throws() }
        assertEquals(failureEmpty, exception.failure)
        assertEquals(TAG, exception.message)
        assertEquals(null, exception.cause)
    }

    @Test
    fun full() {
        val exception = catch { failureFull.throws() }
        assertEquals(failureFull, exception.failure)
        assertEquals(null, exception.cause)
        assertEquals("$TAG [$DESCRIPTION] $INFO", exception.message)
    }

    @Test
    fun attach() {
        val exception = catch { failureAttach1.throws() }
        assertEquals(failureAttach1, exception.failure)
        assertEquals(null, exception.cause)
        val message = """
            $TAG
            > $TAG
            > $TAG [$DESCRIPTION] $INFO
        """.trimIndent()
        assertEquals(message, exception.message)
    }

    @Test
    fun attach2() {
        val exception = catch { failureAttach2.throws() }
        assertEquals(failureAttach2, exception.failure)
        assertEquals(null, exception.cause)
        val message = """
            $TAG
            > $TAG
            > > $TAG
            > > $TAG [$DESCRIPTION] $INFO
        """.trimIndent()
        assertEquals(message, exception.message)
    }

    @Test
    fun exception() {
        val exception = catch { failureException.throws() }
        assertEquals(failureException, exception.failure)
        assertEquals(TAG, exception.message)
        assertEquals(cause, exception.cause)
    }

    private fun catch(block: () -> Nothing): FailureException {
        try {
            block()
        } catch (e: FailureException) {
            return e
        }
    }


}