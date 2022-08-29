package com.wallace.sms_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log

class SmsBroadcastReceiver(
    private var listener: SmsBroadcastReceiverListener
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras
            try {
                var originNumber = ""
                var message = ""
                val pdus = bundle?.get("pdus") as Array<*>
                for (i in pdus.indices) {
                    val smsMessage = SmsMessage.createFromPdu(
                        pdus[i] as ByteArray, bundle.getString("format")
                    )
                    originNumber = smsMessage.displayOriginatingAddress
                    message = smsMessage.messageBody
                }
                listener.onSMSCode(originNumber.plus("\n$message"))
            } catch (e: Exception) {
                Log.d(TAG, e.message ?: "")
            }
        }
    }
}