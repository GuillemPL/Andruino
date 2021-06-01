package agz.technologies.andruino.ui.activities

import agz.technologies.andruino.R
import agz.technologies.andruino.ui.activities.fragments.ScanResultAdapter
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class BluetoothActivity : AppCompatActivity() {
    private val ENABLE_BLUETOOTH_REQUEST_CODE = 1
    private val LOCATION_PERMISSION_REQUEST_CODE = 2
    lateinit var scanButton: Button
    lateinit var bluetoothGatt: BluetoothGatt
    lateinit var snack: Snackbar
    private lateinit var client: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    lateinit var uuid_service : UUID
    lateinit var uuid_characteristic : UUID
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)
        snack = Snackbar.make(
            findViewById(android.R.id.content),
            "Conexión establecida",
            Snackbar.LENGTH_LONG
        )
        setupRecyclerView()
        scanButton = findViewById(R.id.scan_button)
        scanButton.setOnClickListener {
            if (isScanning) {
                stopBleScan()
            } else {
                startBleScan()
            }
        }

        var buttonAdelante : Button = findViewById(R.id.btn_send)
        var buttonStop : Button = findViewById(R.id.btn_stop)
        uuid_service = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b")
        uuid_characteristic = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8")


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
               this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(this, permissions, 0)
        }

    }

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val indexQuery = scanResults.indexOfFirst { it.device.address == result.device.address }
            if (indexQuery != -1) { // A scan result already exists with the same address
                scanResults[indexQuery] = result
                scanResultAdapter.notifyItemChanged(indexQuery)
            } else {
                with(result.device) {
                    Log.i("ScanCallback", "Found BLE device! Name: ${name ?: "Unnamed"}, address: $address")
                }
                scanResults.add(result)
                scanResultAdapter.notifyItemInserted(scanResults.size - 1)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("ScanCallback", "onScanFailed: code $errorCode")
        }
    }

    private var isScanning = false
        set(value) {
            field = value
            runOnUiThread { scanButton.text = if (value) "Stop Scan" else "Start Scan" }
        }


    private val scanResults = mutableListOf<ScanResult>()


    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }


    override fun onResume() {
        super.onResume()
        if (!bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
        }
    }

    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) {
                    promptEnableBluetooth()
                }
            }
        }
    }

    val isLocationPermissionGranted
        get() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun startBleScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isLocationPermissionGranted) {
            requestLocationPermission()
        } else {
            scanResults.clear()
            scanResultAdapter.notifyDataSetChanged()
            bleScanner.startScan(null, scanSettings, scanCallback)
            isScanning = true
        }
    }

    private val scanResultAdapter: ScanResultAdapter by lazy {
        ScanResultAdapter(scanResults) { result ->
            // User tapped on a scan result
            if (isScanning) {
                stopBleScan()
            }
            with(result.device) {
                Log.w("ScanResultAdapter", "Connecting to $address")
                connectGatt(this@BluetoothActivity, false, gattCallback)
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {

            val deviceAddress = gatt.device.address

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d("BluetoothGattCallback", "Successfully connected to $deviceAddress")
                    bluetoothGatt = gatt
                    Handler(Looper.getMainLooper()).post {

                        bluetoothGatt?.discoverServices()
                    }
                    runOnUiThread {
                        client = LocationServices.getFusedLocationProviderClient(this@BluetoothActivity)
                        actualizarUbicacion()
                        recyclerView.visibility = View.GONE
                        var button : Button = findViewById(R.id.button_directo)
                        var button2 : Button = findViewById(R.id.scan_button)
                       snack.show()

                        button.visibility = View.VISIBLE
                        button2.visibility = View.GONE
                        button.setOnClickListener {
                            comandos()
                            val urlString = "https://www.youtube.com/"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.setPackage("com.android.chrome")
                            try {
                               startActivity(intent)
                            } catch (ex: ActivityNotFoundException) {
                                // Chrome browser presumably not installed so allow user to choose instead
                                intent.setPackage(null)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }

        private fun BluetoothGatt.printGattTable() {
            if (services.isEmpty()) {
                Log.i("printGattTable", "No service and characteristic available, call discoverServices() first?")
                return
            }
            services.forEach { service ->
                val characteristicsTable = service.characteristics.joinToString(
                    separator = "\n|--",
                    prefix = "|--"
                ) { it.uuid.toString() }
                Log.i("printGattTable", "\nService ${service.uuid}\nCharacteristics:\n$characteristicsTable"
                )
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            with(gatt) {
                Log.w("BluetoothGattCallback", "Discovered ${services.size} services for ${device.address}")
                printGattTable() // See implementation just above this section
                // Consider connection setup as complete here
            }
        }
    }

    private fun stopBleScan() {
        bleScanner.stopScan(scanCallback)
        isScanning = false
    }


    private fun requestLocationPermission() {
        var alert = AlertDialog.Builder(this)
        if (isLocationPermissionGranted) {
            return
        }
        runOnUiThread {
            alert.setTitle("Location permission required")
            alert.setMessage("Starting from Android M (6.0), the system requires apps to be granted " +
                    "location access in order to scan for BLE devices.")
            alert.setCancelable(false)
            alert.setPositiveButton(android.R.string.ok) { dialogInterface: DialogInterface, i: Int ->
                requestPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }.show()
        }
    }


    private fun Activity.requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_DENIED) {
                    requestLocationPermission()
                } else {
                    startBleScan()
                }
            }
        }
    }

    fun connectGatt(context: Context,
                    autoConnect: Boolean,
                    callback: BluetoothGattCallback
    ) {

    }


    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.apply {
            adapter = scanResultAdapter
            layoutManager = LinearLayoutManager(
                this@BluetoothActivity,
                RecyclerView.VERTICAL,
                false
            )
            isNestedScrollingEnabled = false
        }

        val animator = recyclerView.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }

    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, payload: ByteArray) {
        val writeType = when {
            characteristic.isWritable() -> BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            characteristic.isWritableWithoutResponse() -> {
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            }
            else -> error("Characteristic ${characteristic.uuid} cannot be written to")
        }

        bluetoothGatt?.let { gatt ->
            characteristic.writeType = writeType
            characteristic.value = payload
            gatt.writeCharacteristic(characteristic)
        } ?: error("Not connected to a BLE device!")
    }


    fun BluetoothGattCharacteristic.isReadable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_READ)

    fun BluetoothGattCharacteristic.isWritable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE)

    fun BluetoothGattCharacteristic.isWritableWithoutResponse(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)

    fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean {
        return properties and property != 0
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
            val locationManager: LocationManager =
                getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
                )
            ) {
                val cancellationTokenSource = CancellationTokenSource()
                client.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnCompleteListener {
                    if (it.result != null) {
                        latitude = it.result.latitude
                        longitude = it.result.longitude


                        FirebaseFirestore.getInstance().collection("user")
                            .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                            .collection("datos").document("datos").update(
                                mapOf("ubicacion" to "$latitude,$longitude")
                            ).addOnFailureListener {
                                FirebaseFirestore.getInstance().collection("user")
                                    .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                                    .collection("datos").document("datos").set(
                                        mapOf("ubicacion" to "$latitude,$longitude")
                                    )
                            }
                    }
                }
            }
    }

    private fun actualizarUbicacion() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                getCurrentLocation() //llamamos nuestro metodo
                handler.postDelayed(this, 10000)
            }
        }, 500)
    }

    private fun comandos (){
        FirebaseFirestore.getInstance().collection("user")
            .document(FirebaseAuth.getInstance().currentUser?.email.toString())
            .addSnapshotListener { value, error ->
                if (value!!.exists() && value.get("controller") != null) {
                    val v = value.get("controller") as String
                    val charset = Charsets.UTF_8
                    val byteArray = v.toByteArray(charset)
                    writeCharacteristic(bluetoothGatt.getService(uuid_service).getCharacteristic(uuid_characteristic), byteArray )
                }
            }
    }



}