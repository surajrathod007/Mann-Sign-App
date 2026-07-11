package com.surajmanshal.mannsign

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.surajmanshal.mannsign.data.response.SimpleResponse
import com.surajmanshal.mannsign.repository.Repository
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

    suspend fun createImageMultipart(): MultipartBody.Part {
        return with(activity) {
            val dir = applicationContext.filesDir
            val file = File(dir, "image.png")

            val outputStream = FileOutputStream(file)
            contentResolver.openInputStream(imageUri!!)?.copyTo(outputStream)

            val requestBody = RequestBody.create(MediaType.parse("image/jpg"), file)
            MultipartBody.Part.createFormData("product", file.name, requestBody)
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
