package co.hatch.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import co.hatch.R
import co.hatch.databinding.FragmentDeviceDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeviceDetailFragment : Fragment() {

    private var _binding: FragmentDeviceDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DeviceDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDeviceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
    }

    private fun initViews() {
        binding.deviceDetailName.editText?.doAfterTextChanged {
            viewModel.nameChanged(it?.toString())
        }
        binding.deviceDetailName.editText?.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    viewModel.onClickDone()
                    true
                }
                else -> false
            }
        }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    if (it.name != null && binding.deviceDetailName.editText?.text?.toString() != it.name) {
                        binding.deviceDetailName.editText?.setText(it.name)
                    }
                    binding.deviceDetailRssi.text =
                        getString(R.string.device_detail_rssi, it.rssi)
                    binding.deviceDetailLastConnected.text =
                        getString(R.string.device_detail_last_connected, it.latestConnectedTime)
                    binding.deviceDetailConnectionSeconds.text =
                        getString(R.string.device_detail_elapsed_seconds, it.elapsedSecsConnected)
                    binding.deviceDetailConnectionSeconds.isVisible = it.isConnected
                    binding.deviceDetailError.isVisible = it.isConnectedError
                    it.nameEditEvent?.consume()?.let { stringRes ->
                        Toast.makeText(requireContext(), stringRes, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}