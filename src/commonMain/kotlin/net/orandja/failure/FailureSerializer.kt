package net.orandja.failure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.serializer

/**
 * Serializer for the [Failure] interface.
 */
class FailureSerializer : KSerializer<Failure> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("net.orandja.failure.FailureSerializer") {
        element<String>("id", isOptional = false)
        element<String>("description", isOptional = true)
        element<String>("information", isOptional = true)
        element("attached", delegatedSerialDescriptor { serializer<Set<Failure>>().descriptor }, isOptional = true)
    }

    override fun deserialize(decoder: Decoder): Failure {
        var id: String? = null
        var description: String? = null
        var information: String? = null
        var attached: Set<Failure>? = null
        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    - 1 -> break
                    0 -> id = decodeStringElement(descriptor, index)
                    1 -> description = decodeStringElement(descriptor, index)
                    2 -> information = decodeStringElement(descriptor, index)
                    3 -> attached = decodeSerializableElement(descriptor, index, serializer(), attached)
                }
            }
        }
        val idValue = id ?: throw SerializationException("Deserialization of 'Failure' failed. Missing 'id' field.")
        return GenericFailure(idValue, description, information, null, attached)
    }

    override fun serialize(encoder: Encoder, value: Failure) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.id)
            value.description?.let { encodeStringElement(descriptor, 1, it) }
            value.information?.let { encodeStringElement(descriptor, 2, it) }
            value.attached?.let { encodeSerializableElement(descriptor, 3, serializer<Set<Failure>>(), it) }
        }
    }
}
