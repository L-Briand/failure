package net.orandja.failure


/**
 * Exception class representing a Failure during code execution.
 *
 * @property failure The Failure instance associated with the exception.
 */
class FailureException(val failure: Failure) : Exception(buildString { buildMessage(failure, this) }, failure.cause) {
    companion object {
        /**
         * Builds the exception message based on the given [Failure].
         *
         * @param failure The Failure instance.
         * @param builder The StringBuilder to append the message to. If not provided, a new StringBuilder will be created.
         */
        private fun buildMessage(failure: Failure, builder: StringBuilder = StringBuilder(), prepend: String = "") {
            if (prepend.isNotEmpty()) {
                builder.append('\n')
                builder.append(prepend)
            }
            builder.append(failure.id)
            failure.code?.let {
                builder.append(" [")
                builder.append(it)
                builder.append(']')
            }
            failure.description?.let {
                builder.append(" (")
                builder.append(it)
                builder.append(')')
            }
            failure.information?.let {
                builder.append(" ")
                builder.append(it)
            }
            val attached = failure.attached ?: return
            attached.forEach {
                buildMessage(it, builder, "$prepend> ")
            }
        }
    }
}