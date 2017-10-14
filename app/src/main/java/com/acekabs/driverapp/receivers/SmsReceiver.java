package com.acekabs.driverapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.acekabs.driverapp.interfaces.SmsListener;

public class SmsReceiver extends BroadcastReceiver {
    private static SmsListener mListener;
    private Bundle bundle;
    private SmsMessage currentSMS;
    private String message;

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdu_Objects = (Object[]) bundle.get("pdus");
                if (pdu_Objects != null) {

                    for (Object aObject : pdu_Objects) {
                        currentSMS = getIncomingMessage(aObject, bundle);
                        String senderNo = currentSMS.getDisplayOriginatingAddress();
                        message = currentSMS.getDisplayMessageBody();
                        //(408) 740-4467
                        // +1 (408) 740-4467
                        if (senderNo.equals("+14087404467") || senderNo.equals("(408) 740-4467") || senderNo.equals("+1 (408) 740-4467") || senderNo.equals("51465")) {
                            if (mListener != null) {
                                mListener.messageReceived(message);
                            }
                        }
                    }
                    this.abortBroadcast();
                    // End of loop
                }
            }
        } // bundle null

    }

    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;
    }
}

