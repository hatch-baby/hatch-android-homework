package co.hatch.devicedetails

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import co.hatch.R
import co.hatch.deviceClientLib.connectivity.ConnectivityClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DeviceDetailsFragment : Fragment(R.layout.layout_fragment_device_details) {

    private lateinit var viewModel: DeviceDetailsViewModel

    private lateinit var deviceNameView: TextView
    private lateinit var deviceIdView: TextView
    private lateinit var connectionStatusView: TextView
    private lateinit var lastConnectedTitleView: TextView
    private lateinit var lastConnectedTimeView: TextView
    private lateinit var connectToDeviceButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            DeviceDetailsViewModel.Factory(ConnectivityClient.Factory.create())
        ).get(DeviceDetailsViewModel::class.java)

        deviceNameView = view.findViewById(R.id.device_details_name_view)
        deviceIdView = view.findViewById(R.id.device_details_id_view)
        connectionStatusView = view.findViewById(R.id.device_details_connection_status_view)
        lastConnectedTitleView = view.findViewById(R.id.device_details_last_connected_title)
        lastConnectedTimeView = view.findViewById(R.id.device_details_last_connected_view)
        connectToDeviceButton = view.findViewById(R.id.device_details_connect_to_device_button)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { uiState ->
                    invalidateUI(
                        uiState.deviceId,
                        uiState.deviceName,
                        uiState.isConnected,
                        uiState.lastConnectedTime
                    )
                }
            }
        }
    }

    private fun invalidateUI(
        deviceId: String?,
        deviceName: String?,
        isConnected: Boolean,
        lastConnectedTime: String?
    ) {
        deviceNameView.text = deviceName
        deviceIdView.text = deviceId
        setUpIsConnectedUI(isConnected)
        setUpLastConnectedUI(lastConnectedTime)
    }

    private fun setUpLastConnectedUI(lastConnectedTime: String?) {
        if (lastConnectedTime.isNullOrEmpty()) {
            lastConnectedTitleView.visibility = View.GONE
            lastConnectedTimeView.visibility = View.GONE
        } else {
            lastConnectedTitleView.visibility = View.VISIBLE
            lastConnectedTimeView.visibility = View.VISIBLE
            lastConnectedTimeView.text = lastConnectedTime
        }
    }

    private fun setUpIsConnectedUI(isConnected: Boolean) {
        if (isConnected) {
            connectionStatusView.text = getString(R.string.device_details_connected_status)
            connectToDeviceButton.text = getString(R.string.device_details_disconnect_button)

            connectToDeviceButton.setOnClickListener {
                viewModel.disconnectFromDevice()
            }
        } else {
            connectionStatusView.text = getString(R.string.device_details_disconnected_status)
            connectToDeviceButton.text = getString(R.string.device_details_connect_button)

            connectToDeviceButton.setOnClickListener {
                viewModel.connectToDevice()
            }
        }
    }
}