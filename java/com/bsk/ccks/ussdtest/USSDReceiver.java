package com.bsk.ccks.ussdtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

public class USSDReceiver extends BroadcastReceiver {
    private ArrayList<String> responses = new ArrayList<String>();
    private FullscreenActivity fsa;

    public USSDReceiver(FullscreenActivity fsa) {
        this.fsa = fsa;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String response = intent.getStringExtra("message");
        Log.i("USSDReceiver", "Response: " + response);
        if (response.contains("Me nuk mund te rimbushni llogarine e juaj. Ju lutem kontaktoni sherbimin per konsumatore.")) {
            responses.add(response);
            fsa.stopCheckingPinCodes();
        } else
            responses.add(response);
    }

    public ArrayList<String> getResponses() {
        return responses;
    }
}
