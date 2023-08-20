package com.surajmanshal.mannsign

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.surajmanshal.mannsign.data.response.SimpleResponse
import com.surajmanshal.mannsign.repository.Repository
import com.surajmanshal.mannsign.utils.Constants
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

class ImageUploading(private val activity: Activity) {

    var imageUri: Uri? = null
    private val repository = Repository()

    private val _serverResponse = MutableLiveData<SimpleResponse>()
    val serverResponse: LiveData<SimpleResponse> get() = _serverResponse
    private val _imageUploadResponse = MutableLiveData<SimpleResponse>()
    val imageUploadResponse: LiveData<SimpleResponse> get() = _imageUploadResponse

    fun chooseProfileImageFromGallary() {
        with(activity) {

            var permissionRequest: MutableList<String> = ArrayList()

            val isRead = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            val isWrite = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            if (Build.VERSION.SDK_INT >= 33) {
                val audio = ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_AUDIO
                ) == PackageManager.PERMISSION_GRANTED

                val video = ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_VIDEO
                ) == PackageManager.PERMISSION_GRANTED

                val image = ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED

                if (!audio) {
                    permissionRequest.add(android.Manifest.permission.READ_MEDIA_AUDIO)
                }

                if (!video) {
                    permissionRequest.add(android.Manifest.permission.READ_MEDIA_VIDEO)
                }

                if (!image) {
                    permissionRequest.add(android.Manifest.permission.READ_MEDIA_IMAGES)
                }


            } else {

                if (!isRead) {
                    permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }

                if (!isWrite) {
                    permissionRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }

            }

            if (permissionRequest.isNotEmpty()) {
                val storageIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                // IT IS DEPRECATED FIND LATEST METHOD FOR IT
                startActivityForResult(storageIntent, Constants.CHOOSE_PROFILE_IMAGE)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    permissionRequest.toTypedArray(),
                    Constants.READ_EXTERNAL_STORAGE
                )
            }
        }
    }

    suspend fun createImageMultipart(): MultipartBody.Part {
        return with(activity) {
            val dir = applicationContext.filesDir
            val file = File(dir, "image.png")

            val outputStream = FileOutputStream(file)
            contentResolver.openInputStream(imageUri!!)?.copyTo(outputStream)

            val requestBody = RequestBody.create(MediaType.parse("image/jpg"), file)
            val part = MultipartBody.Part.createFormData("product", file.name, requestBody)
            println("${part.body().contentType()}" + "}")
            return@with part
        }
    }

    suspend fun sendProductImage(part: MultipartBody.Part, languageId: Int) {
        try {
            val response = repository.uploadProductImage(part, languageId)
            _serverResponse.postValue(response)
            _imageUploadResponse.postValue(response)
        } catch (e: Exception) {
            println("$e ${serverResponse.value?.message}")
        }
    }

    suspend fun sendProfileImage(part: MultipartBody.Part) {
        try {
            val response = repository.uploadProfileImage(part)
            _serverResponse.postValue(response)
            _imageUploadResponse.postValue(response)
        } catch (e: Exception) {
            println("$e ${serverResponse.value?.message}")
        }
    }

}