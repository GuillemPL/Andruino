package agz.technologies.andruino.ui.activities.fragments

import agz.technologies.andruino.R
import agz.technologies.andruino.databinding.FragmentCameraBinding
import android.Manifest
import android.annotation.TargetApi
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment


class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding

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

        webViewSetup()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun  webViewSetup() {
        binding.webView.webViewClient = WebViewClient()
        binding.webView.webChromeClient = object : WebChromeClientCustomPoster() {
            override fun onPermissionRequest(request: PermissionRequest) {
                request.grant(request.resources)
            }
        }


        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            val permissions = arrayOf(
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.CAMERA
            )
            ActivityCompat.requestPermissions(requireActivity(), permissions, 0)
        }

        binding.webView.apply {
            loadUrl("https://appr.tc")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
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