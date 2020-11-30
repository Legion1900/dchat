package com.legion1900.dchat.view.util

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import java.io.InputStream

class PhotoChooser(private val fragment: Fragment) {
    fun launchPhotoChoosing(requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        fragment.startActivityForResult(intent, requestCode)
    }

    /**
     * @return Pair<file byte stream, file extension>
     * */
    fun onActivityResult(
        requestCode: Int,
        targetCode: Int,
        resultCode: Int,
        data: Intent
    ): Pair<InputStream, String>? {
        return if (requestCode == targetCode && resultCode == Activity.RESULT_OK) {
            val activity = fragment.requireActivity()
            val uri = data.data!!
            val inputStream = activity.contentResolver.openInputStream(uri)!!
            val type = activity.contentResolver.getType(uri)!!.split("/").last()
            inputStream to type
        } else null
    }
}
