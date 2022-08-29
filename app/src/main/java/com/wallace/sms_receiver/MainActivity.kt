package com.wallace.sms_receiver

import android.Manifest.permission.RECEIVE_SMS
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.wallace.sms_receiver.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SmsBroadcastReceiverListener {
    private var smsBroadcastReceiver: SmsBroadcastReceiver? = null
    private lateinit var binding: ActivityMainBinding

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { hasPermission ->
        if (hasPermission) {
            startSmsRetriever()
        } else {
            Log.d(TAG, "Permission needed to use SMS")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkSmsPermission()
    }

    private fun checkSmsPermission() {
        if (hasSMSPermission()) {
            startSmsRetriever()
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
                    registerBroadcastReceiver()
                }
                .addOnFailureListener {
                    Log.d(TAG, "LISTENING_FAILURE")
                }
        }
    }

    private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver(this)
        val intentFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    override fun onSMSCode(code: String) {
        Log.d(TAG, "SMS_RECEIVED_ACTION >> [onSMSCode]: $code")
        binding.tvwCode.text = code
    }
}