package com.frank.anim.gallery

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.frank.anim.platform.AndroidContextHolder
import java.util.Arrays
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.util.Log

/**
 * Android权限请求管理器
 */
class PermissionRequestManager {
    private val _permissionResult = MutableStateFlow<Boolean?>(null)
    val permissionResult: StateFlow<Boolean?> = _permissionResult

    companion object {
        private var instance: PermissionRequestManager? = null

        fun getInstance(): PermissionRequestManager {
            if (instance == null) {
                instance = PermissionRequestManager()
            }
            return instance!!
        }
    }

    /**
     * 检查权限状态
     */
    fun hasPermissions(context: Context? = null): Boolean {
        val ctx = context ?: AndroidContextHolder.context
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasImages = ContextCompat.checkSelfPermission(
                ctx,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
            val hasVideos = ContextCompat.checkSelfPermission(
                ctx,
                Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED
            hasImages || hasVideos
        } else {
            ContextCompat.checkSelfPermission(
                ctx,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * 从Activity请求权限
     */
    fun requestPermissionsFromActivity(activity: Activity) {
        Log.d("PermissionRequest", "开始请求权限")

        if (hasPermissions(activity)) {
            Log.d("PermissionRequest", "已有权限，直接返回true")
            _permissionResult.value = true
            return
        }

        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        Log.d("PermissionRequest", "请求权限: ${Arrays.toString(permissions)}")

        // 检查是否需要显示权限说明
        var shouldShowRationale = false
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                shouldShowRationale = true
                break
            }
        }

        Log.d("PermissionRequest", "shouldShowRationale: $shouldShowRationale")

        if (shouldShowRationale) {
            // 显示权限说明后请求权限
            _permissionResult.value = false
            requestPermissionsWithExplanation(activity, permissions)
        } else {
            // 直接请求权限
            requestPermissions(activity, permissions)
        }
    }

    /**
     * 直接请求权限
     */
    private fun requestPermissions(activity: Activity, permissions: Array<String>) {
        try {
            val requestCode = 1001
            Log.d("PermissionRequest", "调用ActivityCompat.requestPermissions")
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        } catch (e: Exception) {
            Log.e("PermissionRequest", "权限请求异常", e)
            _permissionResult.value = false
        }
    }

    /**
     * 带说明的权限请求
     */
    private fun requestPermissionsWithExplanation(activity: Activity, permissions: Array<String>) {
        _permissionResult.value = false
        requestPermissions(activity, permissions)
    }

    /**
     * 处理权限请求结果
     */
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1001) {
            var allGranted = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false
                    break
                }
            }
            Log.d("PermissionRequest", "权限请求结果: allGranted=$allGranted, grantResults=${Arrays.toString(grantResults)}")
            _permissionResult.value = allGranted
        }
    }

    /**
     * 重置权限结果
     */
    fun reset() {
        _permissionResult.value = null
    }

    /**
     * 获取权限状态文本
     */
    fun getPermissionStatusText(): String {
        return when {
            hasPermissions() -> "已获取相册权限"
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                val hasImages = ContextCompat.checkSelfPermission(
                    AndroidContextHolder.context,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
                val hasVideos = ContextCompat.checkSelfPermission(
                    AndroidContextHolder.context,
                    Manifest.permission.READ_MEDIA_VIDEO
                ) == PackageManager.PERMISSION_GRANTED

                when {
                    hasImages && hasVideos -> "已获取相册权限"
                    hasImages || hasVideos -> "已获取部分相册权限"
                    else -> "需要照片和视频访问权限"
                }
            }
            else -> "需要存储访问权限"
        }
    }
}
