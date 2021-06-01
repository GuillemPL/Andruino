package agz.technologies.andruino.ui.activities.fragments

import agz.technologies.andruino.R
import agz.technologies.andruino.databinding.FragmentModeBinding
import agz.technologies.andruino.ui.activities.BluetoothActivity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


class ModeFragment : Fragment(){
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
            val intent = Intent (context, BluetoothActivity::class.java)
            requireActivity().startActivity(intent)
        }
        binding.buttonController.setOnClickListener {
            findNavController().navigate(R.id.action_modeFragment_to_controllerFragment)
        }

    }

}