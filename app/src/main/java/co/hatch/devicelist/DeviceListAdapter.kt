package co.hatch.devicelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.hatch.R
import co.hatch.deviceClientLib.model.Device

class DeviceListAdapter(var data: List<Device>? = null, val onItemClickListener: OnItemClickListener): RecyclerView.Adapter<DeviceListItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceListItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_list_item_device, parent, false)
        return DeviceListItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceListItemViewHolder, position: Int) {
        data?.let {
            val device = it[position]

            holder.nameView.text = device.name
            holder.rssiView.text = device.rssi.toString()

            if (device.latestConnectedTime == null) {
                holder.lastConnectedTitleView.visibility = View.INVISIBLE
                holder.lastConnectedView.visibility = View.INVISIBLE
            } else {
                holder.lastConnectedTitleView.visibility = View.VISIBLE
                holder.lastConnectedView.visibility = View.VISIBLE
                holder.lastConnectedView.text = device.latestConnectedTime.toString()
            }

            // TODO: Date object can be further formatted, right now, just using toString()
            holder.itemView.setOnClickListener {
                // sending stale information
                onItemClickListener.onClick(
                    device.id,
                    device.name,
                    device.connected,
                    if (device.latestConnectedTime == null) null else device.latestConnectedTime.toString()
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    fun updateData(data: List<Device>?) {
        this.data = data;
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onClick(
            deviceId: String,
            deviceName: String,
            isConnected: Boolean,
            lastConnectedTime: String?
        )
    }
}