package com.humam.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.humam.myapplication.databinding.ActivityUpdateEmailBinding

class UpdateEmailActivity : AppCompatActivity() {

    lateinit var binding: ActivityUpdateEmailBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUpdateEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        binding.cardVerifikasiPassword.visibility = View.VISIBLE
        binding.cardUpdateEmail.visibility = View.GONE

        binding.btnOtorisasiPassword.setOnClickListener {

            val pass = binding.edtPasssword.text.toString()

            if (pass.isEmpty()){
                binding.edtPasssword.error = "Password ga boleh kosong"
                binding.edtPasssword.requestFocus()
                return@setOnClickListener
            }

            //Buat Credential user
            user.let {
                val userCredensial = EmailAuthProvider.getCredential(it?.email!!, pass)
                it.reauthenticate(userCredensial).addOnCompleteListener { Task ->
                    when{
                        Task.isSuccessful -> {
                            binding.cardVerifikasiPassword.visibility = View.GONE
                            binding.cardUpdateEmail.visibility = View.VISIBLE
                        }
                        Task.exception is FirebaseAuthInvalidCredentialsException -> {
                            binding.edtPasssword.error = "Password Salah"
                            binding.edtPasssword.requestFocus()
                        }
                        else -> {
                            val toast = Toast.makeText(this, "${Task.exception?.message}", Toast.LENGTH_SHORT)
                                toast.setGravity(Gravity.TOP, 0,0)
                                toast.show()
                        }
                    }
                }
            }

            binding.btnUpdateEmail.setOnClickListener updateEmail@ {

                val email = binding.edtEmail.text.toString()

                if (email.isEmpty()){
                    binding.edtEmail.error = "Email Tidak boleh kosong"
                    binding.edtEmail.requestFocus()
                    return@updateEmail
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    binding.edtEmail.error = "Email tidak valid"
                    binding.edtEmail.requestFocus()
                    return@updateEmail
                }

                user?.let {
                    user.updateEmail(email).addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(applicationContext, "Email updated", Toast.LENGTH_SHORT).show()
                            finish()
                        }else {
                            Toast.makeText(applicationContext, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }
}