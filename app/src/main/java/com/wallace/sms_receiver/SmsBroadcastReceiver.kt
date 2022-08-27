package com.wallace.sms_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetriever.SMS_RETRIEVED_ACTION
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status


class SmsBroadcastReceiver : BroadcastReceiver() {
    private var listener: SmsBroadcastReceiverListener? = null

    override fun onReceive(context: Context?, intent: Intent?) {

        when (intent?.action) {
            SMS_RETRIEVED_ACTION -> {

                val extras = intent.extras
                val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

                when (smsRetrieverStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val code = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                        code?.let {
                            listener?.onSMSCode(code)
                        }
                    }

                    CommonStatusCodes.TIMEOUT -> {
                        Log.d(TAG,"SMS TIMEDOUT")
                    }
                }
            }
            SMS_RECEIVED_ACTION -> {
                val extras = intent.extras
                var currentSMS: SmsMessage
                try {
                    val pduObjects = extras?.get("pdus") as Array<*>
                    for (aObject in pduObjects) {
                        currentSMS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            SmsMessage.createFromPdu(aObject as ByteArray?, SmsMessage.FORMAT_3GPP)
                        } else {
                            SmsMessage.createFromPdu(aObject as ByteArray?)
                        }
                        val origin = currentSMS.displayOriginatingAddress
                        val message = currentSMS.displayMessageBody
                        Log.d(TAG,"SMS_RECEIVED_ACTION >> Origin: $origin | Message: $message")
                    }
                } catch (e: Exception) {
                    abortBroadcast()
                    Log.d(TAG,"SMS_RECEIVED_ACTION >> Error: ${e.message}")
                }
            }
            else -> {
                Log.d(TAG,"No SMS to retrieve")
            }
        }
    }
}