package co.hatch.devicelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import co.hatch.deviceClientLib.connectivity.ConnectivityClient
import co.hatch.deviceClientLib.model.Device
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val POLLING_DELAY_MS = 10000L

class DeviceListViewModel(
    private val connectivityClient: ConnectivityClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default) : ViewModel() {

    sealed interface DeviceListUiState {
        object Loading : DeviceListUiState
        object Error : DeviceListUiState
        data class Success(val deviceList: List<Device>) : DeviceListUiState
    }

    private val _uiState = MutableStateFlow<DeviceListUiState>(DeviceListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private lateinit var pollingJob: Job

    fun startPollingDevices(): Job {
        pollingJob = viewModelScope.launch(dispatcher) {
            while (true) {
                val deviceList = connectivityClient.discoverDevices()

                // Sort base on RSSI strength (closer to 0 => stronger signal)
                val sortedDeviceList = deviceList.sortedBy { it.rssi * -1 }

                if (sortedDeviceList.isEmpty()) {
                    _uiState.emit(DeviceListUiState.Error)
                } else {
                    _uiState.emit(DeviceListUiState.Success(sortedDeviceList))
                }

                delay(POLLING_DELAY_MS)
            }
        }

        return pollingJob
    }

    fun stopPolling() {
        pollingJob.cancel()
    }

    class Factory(val connectivityClient: ConnectivityClient) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return DeviceListViewModel(connectivityClient) as T
        }
    }
}