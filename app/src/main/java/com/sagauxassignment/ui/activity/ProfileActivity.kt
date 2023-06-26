package com.sagauxassignment.ui.activity

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import coil.load
import com.sagauxassignment.R
import com.sagauxassignment.databinding.ActivityProfileBinding
import com.sagauxassignment.ui.viewmodel.ProfileViewModel
import com.sagauxassignment.util.AppConstants
import com.sagauxassignment.util.AppConstants.FILE_CHILD_DIR_NAME
import com.sagauxassignment.util.AppConstants.IMAGE_NAME
import com.sagauxassignment.util.AppConstants.IMAGE_PATH_KEY
import com.sagauxassignment.util.isCameraPermissionGranted
import com.sagauxassignment.util.isStoragePermissionGranted
import com.sagauxassignment.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private var binding: ActivityProfileBinding? = null
    private val profileViewModel: ProfileViewModel by viewModels()
    private val readImagesPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    private val cameraPermission = Manifest.permission.CAMERA
    private var imageUri: Uri? = null

    @Inject
    lateinit var preferences: SharedPreferences
    @Inject
    lateinit var preferenceEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

        setUpObserver()
        initClickListener()
        loadImageFromPreference()
    }

    private fun loadImageFromPreference() {
        val  imagePath = preferences.getString(IMAGE_PATH_KEY, "")
        binding?.ivProfileImage?.load(imagePath)
    }

    private fun setUpObserver() {
        profileViewModel.showLoading.observe(this) {
            binding?.loader?.isVisible = it
        }

        profileViewModel.isProfileImageUploaded.observe(this) {
            it?.let { imageUri ->
                binding?.ivProfileImage?.load(
                    imageUri
                )
                showToast("Image uploaded successfully.")
            } ?: kotlin.run {
                showToast("Something went wrong")
            }
        }

        profileViewModel.errorMessage.observe(this) {
            val errorMessage = it.ifEmpty { "Something went wrong" }
            showToast(errorMessage)
        }
    }

    private fun initClickListener() {
        binding?.run {
            btnUploadImage.setOnClickListener {
                showImagePickerDialog()
            }
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Choose from Gallery", "Capture Image")
        AlertDialog.Builder(this)
            .setTitle("Select Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> chooseFromGallery()
                    1 -> captureImage()
                }
            }
            .show()
    }

    private fun chooseFromGallery() {
        if (isStoragePermissionGranted(this)) {
            galleryLauncher.launch("image/*")
        } else {
            requestStoragePermissionLauncher.launch(readImagesPermission)
        }
    }

    private fun captureImage() {
        if (isCameraPermissionGranted(this)) {
            openCamera()
        } else {
            requestCameraPermissionLauncher.launch(cameraPermission)
        }
    }

    private fun openCamera() {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile("IMG_", ".jpg", storageDir)
        imageUri = FileProvider.getUriForFile(this, "${packageName}.provider", imageFile)

        cameraLauncher.launch(imageUri)
    }

    private val requestStoragePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        }
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // Handle the selected image from the gallery
                // You can use the 'uri' parameter to access the selected image
                Log.d(TAG, "Gallery Uri: $it")
                imageUri = it
                saveImageLocally(getBitmapFromUri(it))
                startPreviewActivity(imageUri)
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success) {
                // Handle the captured image
                // You can use the 'imageUri' variable to access the captured image
                Log.d(TAG, "Camera Uri: $imageUri")
                startPreviewActivity(imageUri)
            }
        }

    private val startPreviewActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            imageUri?.let { uri ->
                profileViewModel.uploadProfileImage(uri)
            }
        }
    }

    private fun startPreviewActivity(uri: Uri?) {
        val intent = Intent(
            this@ProfileActivity,
            PreviewImageActivity::class.java
        ).apply {
            putExtra(AppConstants.IMAGE_URI, uri.toString())
        }
        startPreviewActivity.launch(intent)
    }

    private fun getBitmapFromUri(imageUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(imageUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun saveImageLocally(bitmap: Bitmap?) {
        bitmap?.let {
            try {
                val directory = File(cacheDir, FILE_CHILD_DIR_NAME)
                if(!directory.exists()) directory.mkdir()
                val filePath = File(directory, IMAGE_NAME)
                val quality = 90
                val fos = FileOutputStream(filePath)
                it.compress(Bitmap.CompressFormat.JPEG, quality, fos)
                fos.close()
                preferenceEditor.putString(IMAGE_PATH_KEY, filePath.absolutePath).apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binding != null) {
            binding = null
        }
    }

    companion object {
        const val TAG = "ProfileActivity"
    }
}