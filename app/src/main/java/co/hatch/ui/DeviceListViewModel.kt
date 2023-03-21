package co.hatch.ui

import android.icu.text.DateFormat
import androidx.lifecycle.ViewModel
import co.hatch.HatchDispatchers
import co.hatch.deviceClientLib.connectivity.ConnectivityClient
import co.hatch.deviceClientLib.model.Device
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class DeviceListViewModel @Inject constructor(
    private val connectivityClient: ConnectivityClient, // TODO use a DeviceRepository instead of the ConnectivityClient directly
    private val dispatchers: HatchDispatchers,
) : ViewModel() {

    private val _isFetchingDevices = MutableStateFlow(true)

    private val devicesFlow = flow {
        while (true) {
            _isFetchingDevices.value = true
            emit(fetchDevices())
            _isFetchingDevices.value = false
            kotlinx.coroutines.delay(10.seconds)
        }
    }.flowOn(dispatchers.IO)

    val uiState: Flow<DeviceListUiState> = combine(
        _isFetchingDevices,
        devicesFlow
    ) { isFetchingDevices, devices ->
        DeviceListUiState(
            isFetchingDevices = isFetchingDevices,
            devices = devices,
        )
    }

    private fun fetchDevices(): List<DeviceUiModel> {
        return connectivityClient.discoverDevices()
            .sortedBy { it.rssi }
            .mapIndexed { index, model ->
                toUiModel(model, index)
            }
    }

    private fun toUiModel(device: Device, index: Int): DeviceUiModel {
        return DeviceUiModel(
            id = device.id,
            listIndex = index,
            name = device.name,
            rssi = device.rssi,
            lastConnectedDate = device.latestConnectedTime?.let { date ->
                DateFormat.getDateTimeInstance().format(date)
            }
        )
    }

    data class DeviceListUiState(
        val isFetchingDevices: Boolean = true,
        val devices: List<DeviceUiModel> = emptyList(),
    )

    data class DeviceUiModel(
        val id: String,
        val listIndex: Int,
        val name: String,
        val rssi: Int,
        val lastConnectedDate: String?,
    )
}