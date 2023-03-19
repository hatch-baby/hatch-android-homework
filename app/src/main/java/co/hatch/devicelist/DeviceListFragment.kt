package co.hatch.devicelist

import android.os.Bundle
import android.view.View
import android.view.ViewStub
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.hatch.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import co.hatch.deviceClientLib.connectivity.ConnectivityClient
import co.hatch.devicelist.DeviceListViewModel.DeviceListUiState

class DeviceListFragment : Fragment(R.layout.layout_fragment_device_list) {

    private lateinit var viewModel: DeviceListViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateViewStub: ViewStub
    private lateinit var deviceListAdapter: DeviceListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            DeviceListViewModel.Factory(ConnectivityClient.Factory.create())
        ).get(DeviceListViewModel::class.java)

        initDevicesRecyclerView(view)

        // Just putting this here to show the initial loading state
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                delay(2000)
                viewModel.startPollingDevices()
            }
        }

        // Observe Ui State
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.collectLatest { uiState ->
                    when (uiState) {
                        is DeviceListUiState.Loading -> {
                            emptyStateViewStub.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        }

                        is DeviceListUiState.Error -> {
                            // TODO: Create UI to show error case to user
                        }

                        is DeviceListUiState.Success -> {
                            emptyStateViewStub.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            deviceListAdapter.updateData(uiState.deviceList)
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopPolling()
    }

    private fun initDevicesRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.devices_recycler_view)
        emptyStateViewStub = view.findViewById(R.id.empty_state_view_stub)
        val lm = LinearLayoutManager(view.context)

        deviceListAdapter =
            DeviceListAdapter(onItemClickListener = object : DeviceListAdapter.OnItemClickListener {
                override fun onClick(
                    deviceId: String,
                    deviceName: String,
                    isConnected: Boolean,
                    lastConnectedTime: String?
                ) {
                    findNavController().navigate(
                        DeviceListFragmentDirections.actionDeviceListFragmentToDeviceDetailsFragment(
                            deviceId = deviceId,
                            deviceName = deviceName,
                            isConnected = isConnected,
                            lastConnectedTime = lastConnectedTime
                        )
                    )
                }
            })

        recyclerView.apply {
            layoutManager = lm
            adapter = deviceListAdapter

            addItemDecoration(
                DividerItemDecoration(
                    context,
                    (layoutManager as LinearLayoutManager).orientation
                )
            )
        }
    }
}