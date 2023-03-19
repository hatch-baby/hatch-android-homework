package co.hatch.devicelist

import co.hatch.MainCoroutineRule
import co.hatch.deviceClientLib.connectivity.ConnectivityClient
import co.hatch.deviceClientLib.model.Device
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test

class DeviceListViewModelTest {

    private lateinit var viewModel: DeviceListViewModel
    private val connectivityClient = mockk<ConnectivityClient>(relaxed = true)

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        viewModel = DeviceListViewModel(connectivityClient, mainCoroutineRule.dispatcher)
    }

    @Test
    fun `test start polling devices success`() {
        val deviceList = buildDeviceList()
        every { connectivityClient.discoverDevices() } returns deviceList
        viewModel.startPollingDevices()
        assertEquals(
            viewModel.uiState.value,
            DeviceListViewModel.DeviceListUiState.Success(deviceList)
        )
    }

    @Test
    fun `test start polling devices error`() {
        val deviceList = buildEmptyDeviceList()
        every { connectivityClient.discoverDevices() } returns deviceList
        viewModel.startPollingDevices()
        assertEquals(viewModel.uiState.value, DeviceListViewModel.DeviceListUiState.Error)
    }

    @Test
    fun `test stop polling devices`() {
        val job = viewModel.startPollingDevices()
        assertEquals(job.isActive, true)
        viewModel.stopPolling()
        assertEquals(job.isCancelled, true)
    }

    companion object {
        fun buildDeviceList(): List<Device> {
            val device = Device("1234-abcd-5678-efgh", "Test Device", -27, false, 0, null)
            return mutableListOf(device)
        }

        fun buildEmptyDeviceList(): List<Device> {
            return mutableListOf()
        }
    }
}