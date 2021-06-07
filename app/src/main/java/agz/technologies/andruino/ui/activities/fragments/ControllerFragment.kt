package agz.technologies.andruino.ui.activities.fragments

import agz.technologies.andruino.R
import agz.technologies.andruino.databinding.FragmentControllerBinding
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*


class ControllerFragment : Fragment() {
    private lateinit var binding: FragmentControllerBinding
    private var tInicio = System.nanoTime()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_controller, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentControllerBinding.bind(view)
        webViewSetup()
        FirebaseFirestore.getInstance().collection("user")
            .document(FirebaseAuth.getInstance().currentUser?.email.toString()).collection("datos")
            .document("datos").get().addOnSuccessListener {
                val date = SimpleDateFormat("dd-MM-yyyy").format(Date())

                if (it.get("ultimaFechaCon") != null) {
                    val fech = it.get("ultimaFechaCon") as String
                    if (fech != date) {
                        FirebaseFirestore.getInstance().collection("user")
                            .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                            .collection("datos").document("datos").update(
                                mapOf(
                                    "tiempoUso" to "0",
                                    "ultimaFechaCon" to date
                                )
                            )
                    }
                } else {
                    FirebaseFirestore.getInstance().collection("user")
                        .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                        .collection("datos")
                        .document("datos").update(
                            mapOf("ultimaFechaCon" to date)
                        ).addOnFailureListener {
                            FirebaseFirestore.getInstance().collection("user")
                                .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                                .collection("datos").document("datos").set(
                                    mapOf("ultimaFechaCon" to date)
                                )
                        }
                }
            }

        binding.up.setOnTouchListener { v, event ->
            if (MotionEvent.ACTION_DOWN == event.action) {
                controlador("1")
            } else if (MotionEvent.ACTION_UP == event.action) {
                controlador("0")
            }
            false
        }


        binding.down.setOnTouchListener { v, event ->
            if (MotionEvent.ACTION_DOWN == event.action) {
                controlador("4")
            } else if (MotionEvent.ACTION_UP == event.action) {
                controlador("0")
            }
            false
        }
        binding.left.setOnTouchListener { v, event ->
            if (MotionEvent.ACTION_DOWN == event.action) {
                controlador("3")
            } else if (MotionEvent.ACTION_UP == event.action) {
                controlador("0")
            }
            false
        }
        binding.right.setOnTouchListener { v, event ->
            if (MotionEvent.ACTION_DOWN == event.action) {
                controlador("2")
            } else if (MotionEvent.ACTION_UP == event.action) {
                controlador("0")
            }
            false
        }
    }

    private fun controlador(data: String) {
        FirebaseFirestore.getInstance().collection("user")
            .document(FirebaseAuth.getInstance().currentUser?.email.toString()).update(
                mapOf("controller" to data)
            ).addOnFailureListener {
                FirebaseFirestore.getInstance().collection("user")
                    .document(FirebaseAuth.getInstance().currentUser?.email.toString()).set(
                        mapOf("controller" to data)
                    )
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun webViewSetup() {
        binding.webView.webViewClient = WebViewClient()
        binding.webView.webChromeClient = WebChromeClientCustomPoster()

        val newUA =
            "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false

            settings.userAgentString = newUA
            loadUrl("https://www.youtube.com/channel/UCQc2A4frlafZ8RDTKW6PpXQ")
        }
    }


    override fun onResume() {
        super.onResume()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun onStop() {
        super.onStop()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        var tuso = System.nanoTime() - tInicio
        tuso = (tuso * Math.pow(10.0, -9.0)).toLong()

        FirebaseFirestore.getInstance().collection("user")
            .document(FirebaseAuth.getInstance().currentUser?.email.toString()).collection("datos")
            .document("datos").get().addOnSuccessListener {
                var tiempoUsoTotal: Long = 0
                if (it.get("tiempoUsoTotal") != null) {
                    tiempoUsoTotal = (it.get("tiempoUsoTotal") as String).toLong()

                }
                tiempoUsoTotal += tuso

                var tiempoUso:Long = 0
                if (it.get("tiempoUso") != null) {
                    tiempoUso = (it.get("tiempoUso") as String).toLong()
                }
                    tiempoUso += tuso

                    FirebaseFirestore.getInstance().collection("user")
                        .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                        .collection("datos")
                        .document("datos").update(
                            mapOf(
                                "tiempoUso" to "$tiempoUso",
                                "tiempoUsoTotal" to "$tiempoUsoTotal"
                            )
                        ).addOnFailureListener{
                            FirebaseFirestore.getInstance().collection("user")
                                .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                                .collection("datos").document("datos").set(
                                    mapOf("tiempoUso" to "$tiempoUso",
                                        "tiempoUsoTotal" to "$tiempoUsoTotal")
                                )
                        }
            }
    }

    open inner class WebChromeClientCustomPoster : WebChromeClient() {
        override fun getDefaultVideoPoster(): Bitmap? {
            return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        }
    }
}