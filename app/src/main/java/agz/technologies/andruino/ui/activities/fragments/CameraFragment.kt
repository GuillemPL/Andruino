package agz.technologies.andruino.ui.activities.fragments

import agz.technologies.andruino.R
import agz.technologies.andruino.databinding.FragmentCameraBinding
import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.service.autofill.FieldClassification
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding
    private lateinit var client: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(requireActivity(), permissions, 0)
        }
        client = LocationServices.getFusedLocationProviderClient(requireActivity())
        ejecutar()
        binding.button.setOnClickListener {
            val urlString = "https://www.youtube.com/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")
            try {
                requireActivity().startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                // Chrome browser presumably not installed so allow user to choose instead
                intent.setPackage(null)
                requireActivity().startActivity(intent)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun onStop() {
        super.onStop()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        var activity = activity;
        if (isAdded() && activity != null) {
            val locationManager: LocationManager =
                activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

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
    }

    private fun ejecutar() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                getCurrentLocation() //llamamos nuestro metodo
                handler.postDelayed(this, 100000)
            }
        }, 5000)
    }

}