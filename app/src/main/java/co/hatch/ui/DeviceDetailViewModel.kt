package co.hatch.ui

import android.icu.text.DateFormat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.hatch.HatchDispatchers
import co.hatch.R
import co.hatch.deviceClientLib.connectivity.ConnectivityClient
import co.hatch.deviceClientLib.model.Device
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceDetailViewModel @Inject constructor(
    private val connectivityClient: ConnectivityClient,
    private val state: SavedStateHandle,
    private val dispatchers: HatchDispatchers,
) : ViewModel() {

    private val args = DeviceDetailFragmentArgs.fromSavedStateHandle(state)

    private val _isConnected: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    private val _isError = MutableStateFlow(false)
    private val _nameEdit: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _nameEditEvent: MutableStateFlow<ConsumableEvent<Int>?> = MutableStateFlow(null)

    private val _connectedDevice = callbackFlow<Device?> {
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
        _isConnected.filterNotNull(),
        _isError,
        _nameEdit,
        _nameEditEvent,
        _connectedDevice,
    ) { isConnected, isError, nameEdit, nameEditEvent, device ->
        DeviceDetailUiState(
            name = nameEdit ?: device?.name,
            rssi = device?.rssi,
            elapsedSecsConnected = device?.elapsedSecsConnected,
            latestConnectedTime = device?.latestConnectedTime?.let { date ->
                DateFormat.getDateTimeInstance().format(date)
            },
            isConnected = isConnected,
            isConnectedError = isError,
            nameEditEvent = nameEditEvent,
        )
    }

    fun nameChanged(name: String?) {
        _nameEdit.value = name
    }

    fun onClickDone() {
        viewModelScope.launch(dispatchers.IO) {
            _nameEdit.value?.let {
                val isSuccess = connectivityClient.updateDeviceName(args.id, it)
                if (isSuccess) {
                    _nameEdit.value = null
                    _nameEditEvent.value = ConsumableEvent(R.string.device_detail_name_update_success)
                } else {
                    _nameEditEvent.value = ConsumableEvent(R.string.device_detail_name_update_error)
                }
            } ?: run {
                _nameEditEvent.value = ConsumableEvent(R.string.device_detail_name_update_error_null)
            }
        }
    }

    data class DeviceDetailUiState(
        val name: String?,
        val rssi: Int?,
        val latestConnectedTime: String?,
        val elapsedSecsConnected: Long?,
        val isConnected: Boolean,
        val isConnectedError: Boolean,
        val nameEditEvent: ConsumableEvent<Int>?,
    )

}
