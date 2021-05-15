package agz.technologies.andruino.ui.activities.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agz.technologies.andruino.R
import agz.technologies.andruino.databinding.FragmentForgotPasswordBinding
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding =  FragmentForgotPasswordBinding.bind(view)
        binding.btnRegister.setOnClickListener {
            val email = binding.etRecuEmail.text.toString()
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Confirmado", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

}