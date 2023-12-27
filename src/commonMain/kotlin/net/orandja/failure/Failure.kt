package net.orandja.failure

import kotlinx.serialization.Serializable
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Represent a generic Failure during code execution.
 */
@Serializable(FailureSerializer::class)
interface Failure {
    /** A unique identifier for the failure. */
    val id: String


    /** An optional integer code associated with a failure. */
    val code: Int?

    /** A human-readable description of the failure. */
    val description: String?

    /** Additional information about the failure. */
    val information: String?

    /** The underlying cause of the failure, if any. This field is not serialized. */
    val cause: Throwable?

    /** A set of other failures that are attached to this failure. */
    val attached: Set<Failure>?

    /**
     * Throws a [FailureException] initialized with the current [Failure] instance.
     *
     * @throws FailureException if called
     */
    fun throws(): Nothing = throw FailureException(this)


    /**
     * Converts the current instance of a failure to a [GenericFailure] instance.
     * If the current instance is already a [GenericFailure], it is returned as is.
     * Otherwise, a new [GenericFailure] instance is created with the same values as the current instance.
     *
     * @return The [GenericFailure] instance.
     */
    fun defaults(): GenericFailure = if (this is GenericFailure) this
    else GenericFailure(id, code, description, information, cause, attached)
}

/**
 * Returns a [ReadOnlyProperty] that lazily initializes a [Failure] instance which has the field name as [Failure.id].
 *
 * Usage:
 * ```kotlin
 * val MY_FAILURE by Failure.name()
 * assert(MY_FAILURE.id == "MY_FAILURE")
 * ```
 *
 * @param description The human-readable description of the failure. Default is null.
 * @param information Additional information about the failure. Default is null.
 * @param cause The underlying cause of the failure, if any. Default is null.
 * @param attached A set of other failures that are attached to this failure. Default is null.
 * @return A new instance of [GenericFailure].
 */
fun namedFailure(
    code: Int? = null,
    description: String? = null,
    information: String? = null,
    cause: Throwable? = null,
    attached: Set<Failure>? = null,
) = object : ReadOnlyProperty<Any?, GenericFailure> {
    var delegate: GenericFailure? = null
    override fun getValue(thisRef: Any?, property: KProperty<*>): GenericFailure {
        delegate?.let { return it }
        delegate = GenericFailure(property.name, code, description, information, cause, attached)
        return delegate !!
    }
}

/**
 * Creates a [GenericFailure] instance with the specified parameters.
 *
 * @param id The unique identifier for the failure.
 * @param description The human-readable description of the failure. Default is null.
 * @param information Additional information about the failure. Default is null.
 * @param cause The underlying cause of the failure, if any. Default is null.
 * @param attached A set of other failures that are attached to this failure. Default is null.
 * @return A new instance of [GenericFailure].
 */
fun failure(
    id: String,
    code: Int? = null,
    description: String? = null,
    information: String? = null,
    cause: Throwable? = null,
    attached: Set<Failure>? = null,
) = GenericFailure(id, code, description, information, cause, attached)


/**
 * Creates a copy of the [Failure] instance with optional parameters.
 *
 * @param id The unique identifier for the failure. Defaults to the value of the current [Failure] instance's id.
 * @param description The human-readable description of the failure. Defaults to the value of the current [Failure] instance's description.
 * @param information Additional information about the failure. Defaults to the value of the current [Failure] instance's information.
 * @param cause The underlying cause of the failure, if any. Defaults to the value of the current [Failure] instance's cause.
 * @param attached A set of other failures that are attached to this failure. Defaults to the value of the current [Failure] instance's attached.
 * @return The newly created [GenericFailure] instance.
 */
fun Failure.genericCopy(
    id: String = this.id,
    code: Int? = this.code,
    description: String? = this.description,
    information: String? = this.information,
    cause: Throwable? = this.cause,
    attached: Set<Failure>? = this.attached?.map { it.genericCopy() }?.toSet(),
): Failure = GenericFailure(id, code, description, information, cause, attached)


/**
 * Converts the given throwable [T] to a [Failure] instance with the specified parameters.
 *
 * @param T the type of the throwable to convert.
 * @param id a unique identifier for the failure (default value is "EXCEPTION").
 * @param description a human-readable description of the failure (default value is the simple name or qualified name of [T]).
 * @param information additional information about the failure (default value is the message of the throwable [T]).
 * @param attached a set of other failures that are attached to this failure (default value is null).
 * @return the [Failure] instance created from the throwable [T].
 */
inline fun <reified T : Throwable> T.toFailure(
    id: String = "EXCEPTION",
    code: Int? = null,
    description: String? = T::class.simpleName,
    information: String? = message,
    attached: Set<Failure>? = null,
): Failure = GenericFailure(id, code, description, information, this, attached)