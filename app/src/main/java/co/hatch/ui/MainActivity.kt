package co.hatch.ui

import android.content.Context
import android.icu.text.DateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.hatch.HatchDispatchers
import co.hatch.R
import co.hatch.databinding.FragmentDeviceListBinding
import co.hatch.databinding.ViewHolderDeviceListItemBinding
import co.hatch.deviceClientLib.connectivity.ConnectivityClient
import co.hatch.deviceClientLib.model.Device
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

class DeviceListAdapter : ListAdapter<DeviceListViewModel.DeviceUiModel, DeviceListViewHolder>(
    DIFFER
) {

    companion object {
        private val DIFFER = object : DiffUtil.ItemCallback<DeviceListViewModel.DeviceUiModel>() {
            override fun areItemsTheSame(
                oldItem: DeviceListViewModel.DeviceUiModel,
                newItem: DeviceListViewModel.DeviceUiModel
            ): Boolean {
                // using listIndex instead of id so the scroll position is stable when submitting a
                //  new list.
                return oldItem.listIndex == newItem.listIndex
            }

            override fun areContentsTheSame(
                oldItem: DeviceListViewModel.DeviceUiModel,
                newItem: DeviceListViewModel.DeviceUiModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceListViewHolder {
        return DeviceListViewHolder(parent)
    }

    override fun onBindViewHolder(holder: DeviceListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: DeviceListViewHolder) {
        holder.recycle()
    }

}

class DeviceListViewHolder(
    parent: ViewGroup,
    private val binding: ViewHolderDeviceListItemBinding = ViewHolderDeviceListItemBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    ),
) : RecyclerView.ViewHolder(
    binding.root
) {

    private val context: Context get() = binding.root.context

    fun bind(item: DeviceListViewModel.DeviceUiModel) {
        binding.deviceName.text =
            item.name
        binding.deviceRssi.text =
            context.getString(R.string.device_list_item_rssi, item.rssi)
        binding.deviceDate.text =
            context.getString(R.string.device_list_item_last_connected, item.lastConnectedDate)
    }

    fun recycle() {
        binding.deviceName.text = null
        binding.deviceRssi.text = null
        binding.deviceDate.text = null
    }
}

@AndroidEntryPoint
class DeviceListFragment : Fragment() {

    private var _binding: FragmentDeviceListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DeviceListViewModel by viewModels()

    private val deviceListAdapter by lazy { DeviceListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDeviceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
    }

    private fun initViews() {
        binding.recyclerView.adapter = deviceListAdapter
        binding.recyclerView.itemAnimator = null
        // TODO add dividers
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    showFetchingDevices(it.isFetchingDevices)
                    submitDeviceList(it.devices)
                }
            }
        }
    }

    private fun showFetchingDevices(isFetchingDevices: Boolean) {
        binding.progressIndicator.isVisible = isFetchingDevices
    }

    private fun submitDeviceList(devices: List<DeviceListViewModel.DeviceUiModel>) {
        deviceListAdapter.submitList(devices)
    }

}

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
            delay(10.seconds)
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

/*
@AndroidEntryPoint
class DeviceDetailFragment : Fragment() {

}

@HiltViewModel
class DeviceDetailViewModel @Inject constructor() : ViewModel() {

}
 */