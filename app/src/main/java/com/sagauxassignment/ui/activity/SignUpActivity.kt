package com.sagauxassignment.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.sagauxassignment.R
import com.sagauxassignment.databinding.ActivitySignUpBinding
import com.sagauxassignment.domain.User
import com.sagauxassignment.ui.viewmodel.SignUpViewModel
import com.sagauxassignment.util.isValidEmail
import com.sagauxassignment.util.isValidPassword
import com.sagauxassignment.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private var binding: ActivitySignUpBinding? = null
    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        initViewClickListener()
        setUpObserver()
        addTextWatcher()
    }

    private fun initViewClickListener() {
        binding?.signUpButton?.setOnClickListener {

            val username = binding?.usernameEditText?.text?.toString()?.trim() ?: ""
            val email = binding?.emailEditText?.text?.toString()?.trim() ?: ""
            val password = binding?.passwordEditText?.text?.toString()?.trim() ?: ""

            if (isEmptyFields(email, password, username).not() && isValidFormat(email, password)) {
                signUpViewModel.signUpWithFirebase(
                    User(
                        username, email, password
                    )
                )
            }
        }

        binding?.tvLoginLink?.setOnClickListener {
            startLoginActivity()
        }
    }

    private fun addTextWatcher() {
        binding?.run {
            usernameEditText.addTextChangedListener {
                if(it?.isNotEmpty() == true) {
                    usernameEditText.error = null
                }
            }

            emailEditText.addTextChangedListener {
                if(it?.isNotEmpty() == true) {
                    emailEditText.error = null
                }
            }

            passwordEditText.addTextChangedListener {
                if(it?.isNotEmpty() == true) {
                    passwordEditText.error = null
                }
            }
        }
    }

    private fun setUpObserver() {
        signUpViewModel.showLoader.observe(this) {
            binding?.loader?.isVisible = it
        }

        signUpViewModel.isSigneUpSuccessfully.observe(this) {
            showToast("Account created successfully!")
            startLoginActivity()
        }

        signUpViewModel.errorMessage.observe(this) {
            val errorMessage = it.ifEmpty { "Something went wrong" }
            showToast(errorMessage)
        }
    }

    private fun startLoginActivity() {
        startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
        finish()
    }

    private fun isEmptyFields(email: String, password: String, username: String): Boolean {
        val isEmailEmpty = email.isEmpty()
        val isPassEmpty = password.isEmpty()
        val isUsernameEmpty = username.isEmpty()

        if (isUsernameEmpty) {
            binding?.passwordEditText?.error = "Please enter your username."
        }

        if (isEmailEmpty) {
            binding?.emailEditText?.error = "Please enter your email."
        }

        if (isPassEmpty) {
            binding?.passwordEditText?.error = "Please enter your password."
        }
        return isEmailEmpty && isPassEmpty
    }

    private fun isValidFormat(email: String, password: String): Boolean {

        val isEmailValid = isValidEmail(email)
        val isPasswordValid = isValidPassword(password)

        if (isEmailValid.not()) {
            binding?.emailEditText?.error = "Please enter valid email."
        }

        if (isPasswordValid.not()) {
            binding?.passwordEditText?.error = "Please enter strong password."
        }

        return isEmailValid && isPasswordValid
    }

    override fun onDestroy() {
        super.onDestroy()
        if(binding != null)
            binding = null
    }
}