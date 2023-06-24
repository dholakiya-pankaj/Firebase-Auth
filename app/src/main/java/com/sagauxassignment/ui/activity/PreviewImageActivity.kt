package com.sagauxassignment.ui.activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.sagauxassignment.R
import com.sagauxassignment.databinding.ActivityPreviewImageBinding
import com.sagauxassignment.util.AppConstants

class PreviewImageActivity : AppCompatActivity() {

    private var binding: ActivityPreviewImageBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_preview_image)

        val imageUri = intent.getStringExtra(AppConstants.IMAGE_URI)
        imageUri?.let {
            binding?.ivPreviewImage?.setImageURI(Uri.parse(it))
        }

        binding?.btnUploadImage?.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(binding != null)
            binding = null
    }
}