package com.wallace.sms_receiver

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

val TAG = "LOG"

fun Activity.hasSMSPermission() =
    ActivityCompat.checkSelfPermission(
        this, Manifest.permission.RECEIVE_SMS
    ) == PackageManager.PERMISSION_GRANTED