package com.kliq.app.service.notification

import org.junit.Assert.assertEquals
import org.junit.Test

class PushTestReceiverTest {

    @Test
    fun `action simulate push matches specification`() {
        assertEquals("com.kliq.app.ACTION_SIMULATE_PUSH", PushTestReceiver.ACTION_SIMULATE_PUSH)
    }
}
