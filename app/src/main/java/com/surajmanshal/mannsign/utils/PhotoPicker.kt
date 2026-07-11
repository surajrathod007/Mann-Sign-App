package com.surajmanshal.mannsign.utils

import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts

/**
 * Thin wrapper around the modern Photo Picker (`ActivityResultContracts.PickVisualMedia`).
 * no storage/media permission needed.
 *
 * Must be constructed as a field of an Activity/Fragment (both implement [ActivityResultCaller])
 * so the launcher is registered before the host reaches STARTED.
 */
class PhotoPicker(
    caller: ActivityResultCaller,
    private val onImagePicked: (Uri) -> Unit
) {
    private val launcher: ActivityResultLauncher<PickVisualMediaRequest> =
        caller.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let(onImagePicked)
        }

    fun launch() {
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}
