package com.sagauxassignment.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.sagauxassignment.R
import com.sagauxassignment.databinding.ActivityForgotPasswordBinding
import com.sagauxassignment.ui.viewmodel.ForgotPasswordViewModel
import com.sagauxassignment.util.isValidEmail
import com.sagauxassignment.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {

    private var binding: ActivityForgotPasswordBinding? = null
    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)

        setUpObserver()
        initClickListener()
        addTextWatcher()
    }

    private fun setUpObserver() {
        forgotPasswordViewModel.showLoading.observe(this) {
            binding?.loader?.isVisible = it
        }

        forgotPasswordViewModel.isResetPassLinkSent.observe(this) {
            startLoginActivity()
        }

        forgotPasswordViewModel.errorMessage.observe(this) {
            val errorMessage = it.ifEmpty { "Something went wrong" }
            showToast(errorMessage)
        }
    }

    private fun startLoginActivity() {
        showToast("Check your email.")
        finish()
    }

    private fun initClickListener() {
        binding?.forgotPasswordButton?.setOnClickListener {
            val email = binding?.emailEditText?.text?.toString()?.trim() ?: ""

            if(email.isEmpty()) {
                binding?.emailEditText?.error = "Please enter email."
                return@setOnClickListener
            }

            if(isValidEmail(email).not()) {
                binding?.emailEditText?.error = "Please enter email."
                return@setOnClickListener
            }

            forgotPasswordViewModel.resetPasswordWithFirebase(email)
        }
    }

    private fun addTextWatcher() {
        binding?.run {
            emailEditText.addTextChangedListener {
                if(it?.isNotEmpty() == true) {
                    emailEditText.error = null
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(binding != null)
            binding = null
    }
}