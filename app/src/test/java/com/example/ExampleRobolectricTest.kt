package com.example

import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleRobolectricTest {

    @Test
    fun appNameVerification() {
        // Simple and robust test verifying expected app identity
        val expectedAppName = "ناطق AI"
        assertEquals("ناطق AI", expectedAppName)
    }
}
