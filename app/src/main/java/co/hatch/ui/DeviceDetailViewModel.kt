package co.hatch.ui

import android.icu.text.DateFormat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import co.hatch.HatchDispatchers
import co.hatch.deviceClientLib.connectivity.ConnectivityClient
import co.hatch.deviceClientLib.model.Device
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class DeviceDetailViewModel @Inject constructor(
    private val connectivityClient: ConnectivityClient,
    private val state: SavedStateHandle,
    private val dispatchers: HatchDispatchers,
) : ViewModel() {

    private val args = DeviceDetailFragmentArgs.fromSavedStateHandle(state)

    private val _isConnected = MutableStateFlow(false)
    private val _isError = MutableStateFlow(false)

    private val connectedDevice = callbackFlow<Device?> {
        val isConnected = connectivityClient.connectToDeviceBy(
            args.id,
            object : ConnectivityClient.OnDeviceStateChangeListener {
                override fun onDeviceStateChanged(deviceId: String, device: Device) {
                    this@callbackFlow.trySend(device)
                }
            })

        _isConnected.value = isConnected
        _isError.value = !isConnected

        awaitClose { connectivityClient.disconnectFromDevice(args.id) }
    }.onStart {
        emit(null)
    }.flowOn(dispatchers.IO)

    val uiState = combine(
        _isConnected,
        _isError,
        connectedDevice,
    ) { isConnected, isError, device ->
        DeviceDetailUiState(
            name = device?.name,
            rssi = device?.rssi,
            elapsedSecsConnected = device?.elapsedSecsConnected,
            latestConnectedTime = device?.latestConnectedTime?.let { date ->
                DateFormat.getDateTimeInstance().format(date)
            },
            isConnected = isConnected,
            isError = isError
        )
    }

    data class DeviceDetailUiState(
        val name: String?,
        val rssi: Int?,
        val latestConnectedTime: String?,
        val elapsedSecsConnected: Long?,
        val isConnected: Boolean,
        val isError: Boolean,
    )

}