package io.rsbox.net

import io.rsbox.net.session.Session
import io.rsbox.net.session.SessionRegistry
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(Session::class)
class SessionRegistryTests {

    @Test
    fun addAndRemoveTests() {
        val session = mock(Session::class.java)!!
        val registry = SessionRegistry()
        registry.add(session)

        assertEquals(1, registry.count())

        registry.remove(session)

        assertEquals(0, registry.count())
    }

}