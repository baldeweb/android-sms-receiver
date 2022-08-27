package com.wallace.sms_receiver

interface SmsBroadcastReceiverListener {
    fun onSMSCode(code: String)
}