package co.hatch.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.hatch.R
import co.hatch.databinding.ViewHolderDeviceListItemBinding

class DeviceListAdapter(
    private val onClickDevice: (String) -> Unit,
) :
    ListAdapter<DeviceListViewModel.DeviceUiModel, DeviceListAdapter.DeviceListViewHolder>(
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

    inner class DeviceListViewHolder(
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
            binding.root.setOnClickListener {
                onClickDevice(item.id)
            }
        }

        fun recycle() {
            binding.deviceName.text = null
            binding.deviceRssi.text = null
            binding.deviceDate.text = null
        }
    }
}
