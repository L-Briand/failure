package net.orandja.test

import net.orandja.failure.Failure
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

fun assertFailureEquals(first: Failure, second: Failure) {
    assertEquals(first.id, second.id)
    assertEquals(first.code, second.code)
    assertEquals(first.description, second.description)
    assertEquals(first.information, second.information)
    assertEquals(first.cause, second.cause)
    assertContentEquals(first.attached, second.attached?.asIterable())
}