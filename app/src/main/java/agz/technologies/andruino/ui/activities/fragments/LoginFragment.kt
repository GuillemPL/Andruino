package agz.technologies.andruino.ui.activities.fragments

import agz.technologies.andruino.R
import agz.technologies.andruino.databinding.FragmentLoginBinding
import agz.technologies.andruino.model.DrawerLocker
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
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
    private lateinit var drawerLocker : DrawerLocker
    private lateinit var d : DrawerLayout
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
//        if(FirebaseAuth.getInstance().currentUser != null){
//            findNavController().navigate(R.id.modeFragment)
//        }
        binding.btnLogin.setOnClickListener {
            if (checkLoginFields()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        findNavController().navigate(R.id.action_loginFragment_to_modeFragment)
                    } else {
                        binding.etEmail.error = "El email o la contrase??a no son correctas"
                        binding.etPassword.error = "El email o la contrase??a no son correctas"
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

        binding.tvforgotPaswort.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

    }

    fun checkLoginFields(): Boolean {
        if (binding.etEmail.text.isEmpty() || binding.etPassword.text.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(
                binding.etEmail.text.toString()
            ).matches()
        ) {
            if (binding.etEmail.text.isEmpty()) {
                binding.etEmail.error = "El campo est?? vac??o"

            } else {
                binding.etEmail.error = "No es un email v??lido"
            }
            if (binding.etPassword.text.isEmpty()) {
                binding.etPassword.error = "El campo est?? vac??o"
            }
            return false
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            findNavController().navigate(R.id.action_loginFragment_to_modeFragment)
                        } else {
                            snackbar = Snackbar.make(
                                requireView(),
                                "Error al iniciar sesi??n con Google",
                                Snackbar.LENGTH_LONG
                            )
                            snackbar.show()
                        }
                    }
            } catch (e: ApiException) {
                snackbar = Snackbar.make(
                    requireView(),
                    "Error al iniciar sesi??n con Google",
                    Snackbar.LENGTH_LONG
                )
                snackbar.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val supportActionBar: ActionBar? = (requireActivity() as AppCompatActivity).supportActionBar
        if (supportActionBar != null) supportActionBar.hide()
        d = requireActivity().findViewById(R.id.drawer_layout)
        d.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun onStop() {
        super.onStop()
        val supportActionBar: ActionBar? = (requireActivity() as AppCompatActivity).supportActionBar
        if (supportActionBar != null) supportActionBar.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //(activity as MainActivity?)?.setDrawerUnlocked()
        d.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }
}