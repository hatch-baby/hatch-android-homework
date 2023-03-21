package co.hatch.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import co.hatch.databinding.FragmentDeviceListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeviceListFragment : Fragment() {

    private var _binding: FragmentDeviceListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DeviceListViewModel by viewModels()

    private val deviceListAdapter by lazy { DeviceListAdapter(this::onClickDevice) }

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

    private fun onClickDevice(id: String) {
        findNavController().navigate(DeviceListFragmentDirections.toDeviceDetail(id))
    }

}