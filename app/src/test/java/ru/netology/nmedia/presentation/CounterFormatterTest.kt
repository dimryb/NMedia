package ru.netology.nmedia.presentation

import org.junit.Assert.*

import org.junit.Test

class CounterFormatterTest {

    @Test
    fun counterCompression_999_999() {
        val model = CounterFormatter()
        val value = model.counterCompression(999)
        assertTrue(value == "999")
    }

    @Test
    fun counterCompression_1K_1000() {
        val model = CounterFormatter()
        val value = model.counterCompression(1000)
        assertTrue(value == "1K")
    }

    @Test
    fun counterCompression_1K_1001() {
        val model = CounterFormatter()
        val value = model.counterCompression(1001)
        assertTrue(value == "1K")
    }

    @Test
    fun counterCompression_1K_1099() {
        val model = CounterFormatter()
        val value = model.counterCompression(1099)
        assertTrue(value == "1K")
    }

    @Test
    fun counterCompression_1K1_1100() {
        val model = CounterFormatter()
        val value = model.counterCompression(1100)
        assertTrue(value == "1.1K")
    }

    @Test
    fun counterCompression_1K9_1999() {
        val model = CounterFormatter()
        val value = model.counterCompression(1999)
        assertTrue(value == "1.9K")
    }

    @Test
    fun counterCompression_9K9_9900() {
        val model = CounterFormatter()
        val value = model.counterCompression(9900)
        assertTrue(value == "9.9K")
    }

    @Test
    fun counterCompression_10K_10000() {
        val model = CounterFormatter()
        val value = model.counterCompression(10_000)
        assertTrue(value == "10K")
    }

    @Test
    fun counterCompression_10K_10100() {
        val model = CounterFormatter()
        val value = model.counterCompression(10_100)
        assertTrue(value == "10K")
    }

    @Test
    fun counterCompression_100K_100100() {
        val model = CounterFormatter()
        val value = model.counterCompression(100_100)
        assertTrue(value == "100K")
    }

    @Test
    fun counterCompression_999K_999999() {
        val model = CounterFormatter()
        val value = model.counterCompression(999_999)
        assertTrue(value == "999K")
    }

    @Test
    fun counterCompression_1M_1000000() {
        val model = CounterFormatter()
        val value = model.counterCompression(1_000_000)
        assertTrue(value == "1M")
    }

    @Test
    fun counterCompression_9M9_9900000() {
        val model = CounterFormatter()
        val value = model.counterCompression(9_900_000)
        assertTrue(value == "9.9M")
    }

    @Test
    fun counterCompression_999M_999999999() {
        val model = CounterFormatter()
        val value = model.counterCompression(999_999_999)
        assertTrue(value == "999M")
    }
}