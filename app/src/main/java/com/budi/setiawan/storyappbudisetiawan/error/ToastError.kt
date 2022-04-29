package com.budi.setiawan.storyappbudisetiawan.error

import android.content.Context
import android.widget.Toast

object ToastError {
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}