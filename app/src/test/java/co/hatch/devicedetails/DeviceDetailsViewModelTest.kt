package co.hatch.devicedetails

import androidx.lifecycle.SavedStateHandle
import co.hatch.MainCoroutineRule
import co.hatch.deviceClientLib.connectivity.ConnectivityClient
import co.hatch.deviceClientLib.model.Device
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DeviceDetailsViewModelTest {

    private lateinit var viewModel: DeviceDetailsViewModel
    private val connectivityClient = mockk<ConnectivityClient>(relaxed = true)

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        viewModel = DeviceDetailsViewModel(
            createSavedStateHandler(),
            connectivityClient,
            mainCoroutineRule.dispatcher
        )
    }

    @Test
    fun `test connect to device`() {
        val deviceReturned = createDeviceUpdated()

        val slot = slot<ConnectivityClient.OnDeviceStateChangeListener>()
        every {
            connectivityClient.connectToDeviceBy(any(), capture(slot))
        } answers {
            slot.captured.onDeviceStateChanged(deviceReturned.id, deviceReturned)
            true
        }

        viewModel.connectToDevice()

        verify { connectivityClient.connectToDeviceBy(any(), any()) }

        // Confirm that no other connectivityClient calls occurred
        confirmVerified(connectivityClient)

        val currentUiState = viewModel.uiState.value
        // Verify that currentUiState is updated with the device given in the callback
        assertEquals(currentUiState.isConnected, deviceReturned.connected)
        assertEquals(currentUiState.lastConnectedTime, deviceReturned.latestConnectedTime)
    }

    @Test
    fun `test disconnect from device successfully`() {
        assertNotNull(viewModel.uiState.value.deviceId)

        every { connectivityClient.disconnectFromDevice(any()) } answers {
            true
        }

        viewModel.disconnectFromDevice()

        verify { connectivityClient.disconnectFromDevice(any()) }

        // Verify that the isConnected state is now false
        assertEquals(viewModel.uiState.value.isConnected, false)
    }

    companion object {
        fun createSavedStateHandler(): SavedStateHandle {
            val savedStateHandle = SavedStateHandle()
            savedStateHandle["deviceId"] = "1234-abcd-5678-efgh"
            savedStateHandle["deviceName"] = "Test Device"
            savedStateHandle["isConnected"] = false
            savedStateHandle["lastConnectedTime"] = "Wed Mar 26 7:09PM"

            return savedStateHandle
        }

        fun createDeviceUpdated(): Device {
            return Device("1234-abcd-5678-efgh", "Test Device", -27, true, 0, null)
        }
    }
}