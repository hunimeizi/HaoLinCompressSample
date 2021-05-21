package com.haolin.image.compress.library.core

import android.content.Context
import android.os.Environment

class CompressPathUtils(private val context: Context) {
    /**
     * 返回手机外部储存的应用图片路径 (/storage/emulated/0/Android/data/{packageName}/files/Pictures/Compress)
     * @return String
     */
    fun getPicturesDirPathCompress(): String =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath!!+"/Compress"
}