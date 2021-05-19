package agz.technologies.andruino.ui.activities.fragments

import agz.technologies.andruino.R
import agz.technologies.andruino.model.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BluetoothAdapter (var devicesList: List<BluetoothDevice>) : RecyclerView.Adapter<BluetoothAdapter.BluetoothViewHolder>() {

    private lateinit var listener: OnItemClickListener


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BluetoothAdapter.BluetoothViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.device_row, parent, false)
        return BluetoothViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BluetoothAdapter.BluetoothViewHolder, position: Int) {
        val currentItem = devicesList[position]
        holder.device.text = currentItem.deviceName
    }

    override fun getItemCount() = devicesList.size

    inner class BluetoothViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val device: TextView = itemView.findViewById(R.id.tv_device)
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}