package agz.technologies.andruino.ui.activities.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agz.technologies.andruino.R
import agz.technologies.andruino.databinding.FragmentDatosBinding
import agz.technologies.andruino.databinding.FragmentForgotPasswordBinding
import android.location.Address
import android.location.Geocoder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DatosFragment : Fragment() {
    private lateinit var binding: FragmentDatosBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_datos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDatosBinding.bind(view)

        FirebaseFirestore.getInstance().collection("user")
            .document(FirebaseAuth.getInstance().currentUser?.email.toString()).collection("datos")
            .document("datos").get().addOnSuccessListener {
            if (it.exists()) {
                if (it.get("tiempoUsoTotal") != null) {
                    var ret = ""
                    var m = (it.get("tiempoUsoTotal") as String).toInt()
                    var maux = m

                    if (m >= 3600) {
                        maux = m % 3600
                        m = m - maux
                        ret +="${m/3600} H "
                        m = maux
                    }
                    if (m >= 60) {
                        maux = m % 60
                        m = m - maux
                        ret +="${m/60} M "
                        m = maux
                    }
                    if (m > 0){
                        ret +="$m S "
                    }

                    binding.tvTTotal.text = ret
                }
                if (it.get("tiempoUso") != null) {
                    var ret = ""
                    var m = (it.get("tiempoUso") as String).toInt()
                    var maux = m

                    if (m >= 3600) {
                        maux = m % 3600
                        m = m - maux
                        ret +="${m/3600} H "
                        m = maux
                    }
                    if (m >= 60) {
                        maux = m % 60
                        m = m - maux
                        ret +="${m/60} M "
                        m = maux
                    }
                    if (m > 0){
                        ret +="$m S "
                    }

                    binding.tvTSesion.text = ret

                }
                if (it.get("metrosTotales") != null) {
                    var ret = ""
                    var m = (it.get("metrosTotales") as String).split(".")[0].toInt()
                    var maux = m

                    if (m >= 1000) {
                        maux = m % 1000
                        m = m - maux
                        ret +="${m/1000} K "
                        m = maux
                    }
                    if (m > 0){
                        ret +="$m M "
                    }
                    binding.tvDTotal.text = ret
                }
                if (it.get("metrosDiarios") != null) {
                    var ret = ""
                    var m = (it.get("metrosDiarios") as String).split(".")[0].toInt()
                    var maux = m

                    if (m >= 1000) {
                        maux = m % 1000
                        m = m - maux
                        ret +="${m/1000} K "
                        m = maux
                    }
                    if (m > 0){
                        ret +="$m M "
                    }
                    binding.tvDSesion.text = ret
                }
                if (it.get("ubicacion") != null) {
                    var ubicacion = it.get("ubicacion") as String
                    var cordenadas = ubicacion.split(",")
                    val geoCoder = Geocoder(context)
                    val addresses: List<Address> = geoCoder.getFromLocation(
                        cordenadas[0].toDouble(),
                        cordenadas[1].toDouble(),
                        1
                    )
                    val address: Address = addresses[0]
                    val locality: String = address.locality
                    binding.tvUbicacion.text = locality
                }
            }
        }

    }


}