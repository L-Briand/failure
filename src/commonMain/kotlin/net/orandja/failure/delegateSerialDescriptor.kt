package net.orandja.failure

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SealedSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.serializer

/**
 * Creates a delegated implementation of the [SerialDescriptor] interface using the provided block.
 *
 * @param block A lambda function that returns a [SerialDescriptor] instance.
 * @return A [SerialDescriptor] implementation that delegates its methods.
 */
@OptIn(ExperimentalSerializationApi::class, SealedSerializationApi::class)
internal inline fun delegatedSerialDescriptor(crossinline block: () -> SerialDescriptor) = object : SerialDescriptor {
    private val delegate: SerialDescriptor by lazy { block() }
    override val serialName: String get() = delegate.serialName
    override val kind: SerialKind get() = delegate.kind
    override val elementsCount: Int get() = delegate.elementsCount
    override fun getElementName(index: Int): String = delegate.getElementName(index)
    override fun getElementIndex(name: String): Int = delegate.getElementIndex(name)
    override fun getElementAnnotations(index: Int): List<Annotation> = delegate.getElementAnnotations(index)
    override fun getElementDescriptor(index: Int): SerialDescriptor = delegate.getElementDescriptor(index)
    override fun isElementOptional(index: Int): Boolean = delegate.isElementOptional(index)
}