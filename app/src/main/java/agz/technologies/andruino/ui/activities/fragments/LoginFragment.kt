package agz.technologies.andruino.ui.activities.fragments

import agz.technologies.andruino.R
import agz.technologies.andruino.databinding.FragmentLoginBinding
import agz.technologies.andruino.model.DrawerLocker
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    lateinit var snackbar: Snackbar;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        activity?.actionBar?.hide()
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
        binding.btnGoogle.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleClient = GoogleSignIn.getClient(requireContext(), googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, 200)

        }
        binding.tvRgister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.tvforgotPaswort.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200){
           val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            findNavController().navigate(R.id.action_loginFragment_to_modeFragment)
                        }else{
                            snackbar = Snackbar.make(
                                requireView(),
                                "Error al iniciar sesión con Google",
                                Snackbar.LENGTH_LONG
                            )
                            snackbar.show()
                        }
                    }
            }catch (e: ApiException){
                snackbar = Snackbar.make(
                    requireView(),
                    "Error al iniciar sesión con Google",
                    Snackbar.LENGTH_LONG
                )
                snackbar.show()
            }
        }
    }
}