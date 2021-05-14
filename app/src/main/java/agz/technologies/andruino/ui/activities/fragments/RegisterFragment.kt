package agz.technologies.andruino.ui.activities.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agz.technologies.andruino.R
import agz.technologies.andruino.databinding.FragmentRegisterBinding
import android.util.Patterns
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern


class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)
        binding.btnRegist.setOnClickListener{
            if (comprovate()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        findNavController().navigate(R.id.action_registerFragment_to_modeFragment)
                    }
                }
            }
        }
        binding.tvLogIn.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun comprovate(): Boolean{
        var res = true
        val password_pattern: Pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}\$")
        if (binding.etEmail.text.isEmpty()){
            binding.etEmail.error = "El campo está vacío"
            res = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches()) {
            binding.etEmail.error = "No es un email válido"
            res = false
        }
        if (binding.etPassword.text.isEmpty()){
            binding.etPassword.error = "El campo está vacío"
            res = false
        }else if(!password_pattern.matcher(binding.etPassword.text).matches()){
            binding.etPassword.error = "La contraseña es muy débil"
            res = false
        }
        if (binding.etPassword.text.toString() != binding.etRpPassword.text.toString() ){
            binding.etRpPassword.error = "La contraseña no coincide"
            res = false
        }

        return res

    }

}