package agz.technologies.andruino.ui.activities.fragments

import agz.technologies.andruino.R
import android.bluetooth.le.ScanResult
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.layoutInflater

class ScanResultAdapter(
    private val items: List<ScanResult>,
    private val onClickListener: ((device: ScanResult) -> Unit)
) : RecyclerView.Adapter<ScanResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.context.layoutInflater.inflate(
            R.layout.bluetooth_row,
            parent,
            false
        )
        val bluetoothDev : TextView = view.findViewById(R.id.tvFila)
        return ViewHolder(view, onClickListener, bluetoothDev)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    class ViewHolder(
        private val view: View,
        private val onClickListener: ((device: ScanResult) -> Unit), val bluetoothDev: TextView
    ) : RecyclerView.ViewHolder(view) {

        fun bind(result: ScanResult) {
            bluetoothDev.text  = result.device.name ?: "Unnamed"
            view.setOnClickListener { onClickListener.invoke(result) }
        }
    }
}