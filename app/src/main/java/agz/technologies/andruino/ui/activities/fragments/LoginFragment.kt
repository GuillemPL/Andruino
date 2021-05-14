package agz.technologies.andruino.ui.activities.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agz.technologies.andruino.R
import agz.technologies.andruino.databinding.FragmentLoginBinding
import android.content.Intent
import android.util.Patterns
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        binding.btnLogin.setOnClickListener{
            if (checkLoginFields()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        findNavController().navigate(R.id.action_loginFragment_to_modeFragment)
                    } else {
                        binding.etEmail.error = "El email o la contraseña no son correctas"
                        binding.etPassword.error = "El email o la contraseña no son correctas"
                    }
                }

            }
        }
        binding.tvRgister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
    fun checkLoginFields(): Boolean {
        if (binding.etEmail.text.isEmpty() || binding.etPassword.text.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(
                binding.etEmail.text.toString()
            ).matches()) {
            if (binding.etEmail.text.isEmpty()) {
                binding.etEmail.error = "El campo está vacío"

            }else{
                binding.etEmail.error = "No es un email válido"
            }
            if (binding.etPassword.text.isEmpty()) {
                binding.etPassword.error = "El campo está vacío"
            }
            return false
        }

        return true
    }

}