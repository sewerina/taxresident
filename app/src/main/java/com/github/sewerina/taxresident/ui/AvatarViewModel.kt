package com.github.sewerina.taxresident.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AvatarViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {

    private val _avatarBitmap = MutableLiveData<Bitmap>()
    val avatarBitmap: LiveData<Bitmap> = _avatarBitmap

    @SuppressLint("StaticFieldLeak")
    val context: Context = getApplication()

    private val file = context.createImageFile()

    val avatarUri = FileProvider.getUriForFile(
        context,
        context.packageName + ".provider",
        file
    )

    fun loadAvatar() {
        viewModelScope.launch {
            val bitmap = BitmapFactory.decodeFile(file.path)
            _avatarBitmap.postValue(bitmap)
        }
    }

    fun updateAvatar(inputUri: Uri?) {
        viewModelScope.launch {
            if (inputUri != null) {
                val input = context.contentResolver.openInputStream(inputUri)
                val output = context.contentResolver.openOutputStream(avatarUri)
                val content = input!!.readBytes()
                output!!.write(content)
                output.flush()
                output.close()
                input.close()

                val bitmap = BitmapFactory.decodeFile(file.path)
                _avatarBitmap.postValue(bitmap)
            }
        }
    }
}

fun Context.createImageFile(): File {
    // Create an image file name
    val fileName = "avatar.jpg"

    val image = File(
        externalCacheDir, /* directory */
        fileName
    )
    return image
}