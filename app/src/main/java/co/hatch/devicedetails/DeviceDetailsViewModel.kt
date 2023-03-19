package co.hatch.devicedetails

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import co.hatch.deviceClientLib.connectivity.ConnectivityClient
import co.hatch.deviceClientLib.model.Device
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeviceDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val connectivityClient: ConnectivityClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default) : ViewModel() {

    // Better if we have a ConnectivityClient#getDevice(deviceId) to get the latest device
    // instead of relying on the args that are passed in via the onClick
    data class DeviceDetailsUiState(
        val deviceId: String? = null,
        val deviceName: String? = null,
        val isConnected: Boolean = false,
        val lastConnectedTime: String? = null,
    )

    private val _uiState = MutableStateFlow(DeviceDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // TODO: Can save these constants out somewhere to ensure that we are using the same ones
        val deviceId: String? = savedStateHandle["deviceId"]
        val deviceName: String? = savedStateHandle["deviceName"]
        val isConnected = savedStateHandle["isConnected"] ?: false
        val lastConnectedTime: String? = savedStateHandle["lastConnectedTime"]

        _uiState.value = DeviceDetailsUiState(deviceId, deviceName, isConnected, lastConnectedTime)
    }

    fun connectToDevice() {
        _uiState.value.deviceId?.let {
            viewModelScope.launch(dispatcher) {
                connectivityClient.connectToDeviceBy(
                    it,
                    object : ConnectivityClient.OnDeviceStateChangeListener {
                        override fun onDeviceStateChanged(deviceId: String, device: Device) {
                            viewModelScope.launch(dispatcher) {
                                _uiState.emit(
                                    _uiState.value.copy(
                                        isConnected = device.connected,
                                        lastConnectedTime = device.latestConnectedTime?.toString()
                                    )
                                )
                            }
                        }
                    })
            }
        }
    }

    fun disconnectFromDevice() {
        _uiState.value.deviceId?.let {
            viewModelScope.launch(dispatcher) {
                // Assuming that boolean returned is whether disconnect was successful
                val success = connectivityClient.disconnectFromDevice(it)

                // Not great here because lastConnectedTime should've been updated too
                // but at the very least this can toggle the connected UI state and button
                if (success) {
                    _uiState.emit(
                        _uiState.value.copy(
                            isConnected = false,
                        )
                    )
                }
            }
        }
    }

    class Factory(val connectivityClient: ConnectivityClient) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return DeviceDetailsViewModel(extras.createSavedStateHandle(), connectivityClient) as T
        }
    }
}

