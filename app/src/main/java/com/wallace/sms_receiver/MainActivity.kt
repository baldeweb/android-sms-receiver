package com.wallace.sms_receiver

import android.Manifest.permission.RECEIVE_SMS
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.phone.SmsRetriever

class MainActivity : AppCompatActivity(), SmsBroadcastReceiverListener {
    private var smsBroadcastReceiver: SmsBroadcastReceiver? = null
    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { hasPermission ->
        if (hasPermission) {
            startSmsRetriever()
            registerBroadcastReceiver()
        } else {
            Log.d(TAG, "Permission needed to use SMS")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkSmsPermission()
    }

    private fun checkSmsPermission() {
        if (hasSMSPermission()) {
            startSmsRetriever()
            registerBroadcastReceiver()
        } else {
            requestPermission.launch(RECEIVE_SMS)
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsBroadcastReceiver)
    }

    private fun startSmsRetriever() {
        SmsRetriever.getClient(this).also { smsRetriever ->
            smsRetriever.startSmsRetriever()
                .addOnSuccessListener {
                    Log.d(TAG, "LISTENING_SUCCESS")
                }
                .addOnFailureListener {
                    Log.d(TAG, "LISTENING_FAILURE")
                }
        }
    }

    private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReceiver, intentFilter, SmsRetriever.SEND_PERMISSION, null)
    }

    override fun onSMSCode(code: String) {
        Log.d(TAG, "SMS_RECEIVED_ACTION >> SMS Code: $code")
    }
}