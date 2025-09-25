package net.orandja.failure

import kotlinx.serialization.Serializable


/**
 * Exception class representing a Failure during code execution.
 * This class is also a failure.
 *
 * @property failure The Failure instance associated with the exception.
 */
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(FailureSerializer::class)
class FailureException(
    private val failure: Failure,
) : Exception(
    buildString { buildMessage(failure) },
    failure.cause
), Failure by failure {

    companion object {
        /**
         * Builds the exception message based on the given [Failure].
         *
         * @param failure The Failure instance.
         * @param builder The StringBuilder to append the message to. If not provided, a new StringBuilder will be created.
         */
        private fun StringBuilder.buildMessage(failure: Failure, prepend: String = "") {
            if (prepend.isNotEmpty()) {
                append('\n')
                append(prepend)
            }
            append(failure.id)
            failure.code?.let {
                append(" [")
                append(it)
                append(']')
            }
            failure.description?.let {
                append(" (")
                append(it)
                append(')')
            }
            failure.information?.let {
                append(" ")
                append(it)
            }
            val attached = failure.attached ?: return
            attached.forEach {
                buildMessage(it, "$prepend> ")
            }
        }
    }

    override val cause: Throwable? get() = failure.cause
}