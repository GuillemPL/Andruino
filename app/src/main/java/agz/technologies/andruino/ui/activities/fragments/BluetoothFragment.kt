package agz.technologies.andruino.ui.activities.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agz.technologies.andruino.R
import agz.technologies.andruino.databinding.FragmentBluetoothBinding
import agz.technologies.andruino.databinding.FragmentCameraBinding
import agz.technologies.andruino.model.BluetoothDevice
import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class BluetoothFragment : Fragment() {
    private lateinit var binding: FragmentBluetoothBinding
    private var list : MutableList<BluetoothDevice> = ArrayList()
    private lateinit var  bluetoothAdapter : BluetoothAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("DeviceListActivity", "onCreate()")
        return inflater.inflate(R.layout.fragment_bluetooth, container, false)
    }

    // TODO: 19/05/2021 implementar listener en el recycler view para crear la conexión con el ble

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBluetoothBinding.bind(view)
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
            )
            ActivityCompat.requestPermissions(requireActivity(), permissions, 0)
        }

        setRecyclerView(list)
    }

    private val bleScanner = object :ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            Log.d("pepe","onScanResult: ${result?.device?.address} - ${result?.device?.name}")
            if(result?.device?.name?.isNotEmpty() == true){
                var bluetoothDevice = result?.device?.name?.let { BluetoothDevice(it) }
                if (bluetoothDevice != null) {
                    list.add(bluetoothDevice)
                    bluetoothAdapter.notifyDataSetChanged()
                }
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            Log.d("DeviceListActivity","onBatchScanResults:${results.toString()}")
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.d("DeviceListActivity", "onScanFailed: $errorCode")
        }

    }

    private val bluetoothLeScanner: BluetoothLeScanner
        get() {
            val bluetoothManager = requireActivity().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter = bluetoothManager.adapter
            return bluetoothAdapter.bluetoothLeScanner
        }

    class ListDevicesAdapter(context: Context?, resource: Int) : ArrayAdapter<String>(context!!, resource)


    override fun onStart() {
        Log.d("DeviceListActivity","onStart()")
        super.onStart()
        bluetoothLeScanner.startScan(bleScanner)
    }

    override fun onStop() {
        bluetoothLeScanner.stopScan(bleScanner)
        super.onStop()
    }

    private fun setRecyclerView(allCategories: List<BluetoothDevice>) {
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        binding.rvBluetooth.layoutManager = layoutManager
        bluetoothAdapter = BluetoothAdapter(allCategories)
        binding.rvBluetooth.adapter = bluetoothAdapter
    }
}