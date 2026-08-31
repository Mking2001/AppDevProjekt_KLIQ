package com.kliq.app.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class ZoomableImageBoundaryUnitTest {

    @Test
    fun `calculateClampedOffset returns zero offset when scale is one or less`() {
        val (offsetX, offsetY) = calculateClampedOffset(
            scale = 1.0f,
            rawOffsetX = 150f,
            rawOffsetY = -200f,
            containerWidth = 1000f,
            containerHeight = 2000f
        )
        assertEquals(0f, offsetX, 0.001f)
        assertEquals(0f, offsetY, 0.001f)
    }

    @Test
    fun `calculateClampedOffset allows offset within boundary limits when zoomed in`() {

        val (offsetX, offsetY) = calculateClampedOffset(
            scale = 2.0f,
            rawOffsetX = 300f,
            rawOffsetY = -800f,
            containerWidth = 1000f,
            containerHeight = 2000f
        )
        assertEquals(300f, offsetX, 0.001f)
        assertEquals(-800f, offsetY, 0.001f)
    }

    @Test
    fun `calculateClampedOffset clamps offset when pan exceeds boundary limits`() {

        val (offsetX, offsetY) = calculateClampedOffset(
            scale = 2.0f,
            rawOffsetX = 9999f,
            rawOffsetY = -9999f,
            containerWidth = 1000f,
            containerHeight = 2000f
        )
        assertEquals(500f, offsetX, 0.001f)
        assertEquals(-1000f, offsetY, 0.001f)
    }

    @Test
    fun `calculateClampedOffset scales boundary limits linearly with zoom factor`() {

        val (offsetX, offsetY) = calculateClampedOffset(
            scale = 4.0f,
            rawOffsetX = 2000f,
            rawOffsetY = -2000f,
            containerWidth = 1000f,
            containerHeight = 1000f
        )
        assertEquals(1500f, offsetX, 0.001f)
        assertEquals(-1500f, offsetY, 0.001f)
    }
}
