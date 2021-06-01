package agz.technologies.andruino.ui.activities.fragments

import agz.technologies.andruino.R
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MapsFragment : Fragment() {

    private lateinit var callback:  OnMapReadyCallback
    private lateinit var ubicacion: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        FirebaseFirestore.getInstance().collection("user").document(FirebaseAuth.getInstance().currentUser?.email.toString()).collection("datos").document("datos").get().addOnSuccessListener {
            if (it.get("ubicacion") != null) {
                    ubicacion = it.get("ubicacion") as String
                    var cordenadas = ubicacion.split(",")
                    callback = OnMapReadyCallback { googleMap ->
                        var andruino = LatLng(cordenadas[0].toDouble(), cordenadas[1].toDouble())
                        googleMap.addMarker(MarkerOptions().position(andruino).title("Andruino"))
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(andruino))
                    }
                    mapFragment?.getMapAsync(callback)
            }else{
                var snackbar = Snackbar.make(
                    requireView(),
                    "Esta cuenta no tiene datos",
                    Snackbar.LENGTH_LONG
                )
                snackbar.show()
            }
        }

    }
}