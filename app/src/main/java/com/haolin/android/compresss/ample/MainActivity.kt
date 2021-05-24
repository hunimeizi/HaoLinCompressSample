package com.haolin.android.compresss.ample

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.haolin.android.compresss.ample.utils.UriParseUtils
import com.haolin.image.compress.library.CompressImageManager
import com.haolin.image.compress.library.config.CompressConfig
import com.haolin.image.compress.library.core.CompressPathUtils
import com.haolin.image.compress.library.listener.CompressImage
import com.haolin.image.compress.library.utils.CachePathUtils
import com.haolin.image.compress.library.utils.CommonUtils
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() , CompressImage.CompressListener {

    var compressConfig // 压缩配置
            : CompressConfig? = null
    var dialog // 压缩加载框
            : ProgressDialog? = null
    var cameraCachePath // 拍照源文件路径
            : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 运行时权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val perms = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(perms[1]) == PackageManager.PERMISSION_DENIED
            ) {
                requestPermissions(perms, 200)
            }
        }
        compressConfig = CompressConfig.builder()
            .setUnCompressMinPixel(1000) // 最小像素不压缩，默认值：1000
            .setUnCompressNormalPixel(2000) // 标准像素不压缩，默认值：2000
            .setMaxPixel(1000) // 长或宽不超过的最大像素 (单位px)，默认值：1200
            .setMaxSize(100 * 1024) // 压缩到的最大大小 (单位B)，默认值：200 * 1024 = 200KB
            .enablePixelCompress(true) // 是否启用像素压缩，默认值：true
            .enableQualityCompress(true) // 是否启用质量压缩，默认值：true
            .enableReserveRaw(true) // 是否保留源文件，默认值：true
            .setCacheDir("") // 压缩后缓存图片路径，默认值：Constants.COMPRESS_CACHE
            .setShowCompressDialog(true) // 是否显示压缩进度条，默认值：false
            .create()

    }

    /**
     * 点击拍照
     *
     * @param view v
     */
    fun camera(view: View?) {
        val outputUri: Uri
        val file: File = CachePathUtils.getCameraCacheFile()
        outputUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            UriParseUtils.getCameraOutPutUri(this, file)
        } else {
            Uri.fromFile(file)
        }
        cameraCachePath = file.absolutePath
        // 启动拍照
        CommonUtils.hasCamera(
            this,
            CommonUtils.getCameraIntent(outputUri),
            com.haolin.image.compress.library.utils.Constants.CAMERA_CODE
        )
    }


    /**
     * 相册
     *
     * @param view v
     */
    fun album(view: View?) {
        CommonUtils.openAlbum(this, com.haolin.image.compress.library.utils.Constants.ALBUM_CODE)
    }

    override fun onCompressSuccess(images: ArrayList<com.haolin.image.compress.library.bean.Photo>) {
        for (image in images) {
            Log.e("lyb >>> ", "压缩成功 压缩后的路径为" + image.getCompressPath())
        }
        if (dialog != null && !isFinishing()) {
            dialog!!.dismiss()
        }
        Toast.makeText(this@MainActivity, "压缩成功", Toast.LENGTH_SHORT).show()
    }

    override fun onCompressFailed(
        images: ArrayList<com.haolin.image.compress.library.bean.Photo?>?,
        error: String?
    ) {
        Log.e("lyb >>> ", error!!)
        if (dialog != null && !isFinishing()) {
            dialog!!.dismiss()
        }
    }

    // 准备压缩，封装图片集合
    open fun preCompress(photoPath: String?) {
        val photos: ArrayList<com.haolin.image.compress.library.bean.Photo> =
            ArrayList<com.haolin.image.compress.library.bean.Photo>()
        photos.add(com.haolin.image.compress.library.bean.Photo(photoPath))
        if (!photos.isEmpty()) compress(photos)
    }

    // 开始压缩
    open fun compress(photos: ArrayList<com.haolin.image.compress.library.bean.Photo>) {
        if (compressConfig!!.isShowCompressDialog) {
            Log.e("lyb >>> ", "开启了加载框")
            dialog = CommonUtils.showProgressDialog(this, "压缩中……")
        }
        CompressImageManager.build(this, compressConfig, photos, this).compress()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 拍照返回
        if (requestCode == com.haolin.image.compress.library.utils.Constants.CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            // 压缩（集合？单张）
            preCompress(cameraCachePath)
        }

        // 相册返回
        if (requestCode == com.haolin.image.compress.library.utils.Constants.ALBUM_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val uri = data.data
                val path: String = UriParseUtils.getPath(this, uri)
                // 压缩（集合？单张）
                preCompress(path)
            }
        }
    }

    /*
    * 压缩路径 context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath!!+"/Compress"
    * */
}