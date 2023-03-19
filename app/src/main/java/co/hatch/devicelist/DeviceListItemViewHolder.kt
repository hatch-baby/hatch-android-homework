package co.hatch.devicelist

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import co.hatch.R

class DeviceListItemViewHolder(view: View): ViewHolder(view) {

    val nameView: TextView
    val rssiView: TextView
    val lastConnectedTitleView: TextView
    val lastConnectedView: TextView

    init {
        nameView = view.findViewById(R.id.name_text_view)
        rssiView = view.findViewById(R.id.rssi_text_view)
        lastConnectedTitleView = view.findViewById(R.id.last_connected_title_view)
        lastConnectedView = view.findViewById(R.id.last_connected_text_view)
    }
}