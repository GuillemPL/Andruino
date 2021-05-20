package agz.technologies.andruino.ui.activities.fragments

import agz.technologies.andruino.R
import agz.technologies.andruino.databinding.FragmentControllerBinding
import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.SCROLLBARS_OUTSIDE_OVERLAY
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ControllerFragment : Fragment() {
    private lateinit var binding: FragmentControllerBinding

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
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED){
            val permissions = arrayOf(
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.CAMERA
            )
            ActivityCompat.requestPermissions(requireActivity(), permissions, 0)
        }
        webViewSetup()
        binding.up.setOnTouchListener { v, event ->
            if (MotionEvent.ACTION_DOWN == event.action){
                controlador("1")
            }else if(MotionEvent.ACTION_UP == event.action){
                controlador("0")
            }
            false
        }


        binding.down.setOnTouchListener { v, event ->
            if (MotionEvent.ACTION_DOWN == event.action){
                controlador("4")
            }else if(MotionEvent.ACTION_UP == event.action){
                controlador("0")
            }
            false
        }
        binding.left.setOnTouchListener { v, event ->
            if (MotionEvent.ACTION_DOWN == event.action){
                controlador("3")
            }else if(MotionEvent.ACTION_UP == event.action){
                controlador("0")
            }
            false
        }
        binding.right.setOnTouchListener { v, event ->
            if (MotionEvent.ACTION_DOWN == event.action){
                controlador("2")
            }else if(MotionEvent.ACTION_UP == event.action){
                controlador("0")
            }
            false
        }

    }

    private fun controlador(data: String) {
        FirebaseFirestore.getInstance().collection("user").document(FirebaseAuth.getInstance().currentUser?.email.toString()).set(
            hashMapOf("controller" to data))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun  webViewSetup() {
        binding.webView.webViewClient = WebViewClient()
        binding.webView.webChromeClient = object : WebChromeClientCustomPoster() {
            override fun onPermissionRequest(request: PermissionRequest) {
                request.grant(request.resources)
            }
        }

        val newUA = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false

            settings.userAgentString = newUA
            loadUrl("https://www.youtube.com/watch?v=8SEQaB7BrLg&ab_channel=MightExos")
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


   open inner class WebChromeClientCustomPoster : WebChromeClient() {
       override fun getDefaultVideoPoster(): Bitmap? {
            return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        }
    }

}