package net.orandja.failure

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


/**
 * Represents the default implementation of the [Failure] interface.
 *
 * @property id A unique identifier for the failure.
 * @property description A human-readable description of the failure.
 * @property information Additional information about the failure.
 * @property cause The underlying cause of the failure, if any.
 * @property attached A set of other failures that are attached to this failure.
 */
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable
data class GenericFailure(
    override val id: String,
    override val description: String? = null,
    override val information: String? = null,
    @Transient override val cause: Throwable? = null,
    override val attached: Set<Failure>? = null,
) : Failure