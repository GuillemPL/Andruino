package agz.technologies.andruino.ui.activities.fragments

import agz.technologies.andruino.R
import agz.technologies.andruino.databinding.FragmentModeBinding
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


class ModeFragment : Fragment() {
    private lateinit var binding: FragmentModeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentModeBinding.bind(view)
        var buttonCamera : CardView = binding.buttonCamera
        buttonCamera.setOnClickListener {
           // findNavController().navigate(R.id.action_modeFragment_to_cameraFragment)
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
        binding.buttonController.setOnClickListener {
            findNavController().navigate(R.id.action_modeFragment_to_controllerFragment)
        }

    }

}