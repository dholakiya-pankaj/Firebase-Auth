package com.sagauxassignment.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.sagauxassignment.R
import com.sagauxassignment.databinding.ActivityLoginBinding
import com.sagauxassignment.ui.viewmodel.LoginViewModel
import com.sagauxassignment.util.isValidEmail
import com.sagauxassignment.util.isValidPassword
import com.sagauxassignment.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding? = null
    private val loginViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding?.loginToolbar?.tvTitle?.text = getString(R.string.login)
        setUpObserver()
        initClickListener()
        addTextWatcher()
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            startProfileActivity()
        }
    }

    private fun setUpObserver() {
        loginViewModel.showLoading.observe(this) {
            binding?.loader?.isVisible = it
        }

        loginViewModel.isLoggedIn.observe(this) {
            startProfileActivity()
        }

        loginViewModel.errorMessage.observe(this) {
            val errorMessage = it.ifEmpty { "Something went wrong" }
            showToast(errorMessage)
        }
    }

    private fun initClickListener() {
        binding?.loginButton?.setOnClickListener {
            val email = binding?.emailEditText?.text?.toString()?.trim() ?: ""
            val password = binding?.passwordEditText?.text?.toString()?.trim() ?: ""

            if (email.isEmpty()) {
                binding?.emailEditText?.error = "Please enter email."
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding?.passwordEditText?.error = "Please enter password."
                return@setOnClickListener
            }

            if (isValidEmail(email).not()) {
                binding?.emailEditText?.error = "Please enter valid email."
                return@setOnClickListener
            }

            if (isValidPassword(password).not()) {
                binding?.passwordEditText?.error = "Please enter valid password."
                return@setOnClickListener
            }

            loginViewModel.loginWithFirebase(email, password)
        }

        binding?.tvSignUpLink?.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            finish()
        }

        binding?.tvForgotPassword?.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }
    }

    private fun addTextWatcher() {
        binding?.run {
            emailEditText.addTextChangedListener {
                if (it?.isNotEmpty() == true) {
                    emailEditText.error = null
                }
            }

            passwordEditText.addTextChangedListener {
                if (it?.isNotEmpty() == true) {
                    passwordEditText.error = null
                }
            }
        }
    }

    private fun startProfileActivity() {
        startActivity(Intent(this@LoginActivity, ProfileActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binding != null)
            binding = null
    }
}