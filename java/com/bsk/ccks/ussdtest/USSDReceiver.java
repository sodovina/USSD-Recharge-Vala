package ussdtranscom.bsk.transferokredit;

/**
 * Created by bsk' on 12/11/2016.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

public class USSDReceiver extends BroadcastReceiver {
    private ArrayList<String> responses = new ArrayList<String>();
    private ArrayList<Integer> vlerat = new ArrayList<Integer>();
    private int numri, i;
    private MainActivity ma;

    public USSDReceiver() {
    }

    public void setVlerat(ArrayList<Integer> vlerat) {
        this.vlerat = vlerat;
    }

    public void setNumri(int numri) {
        this.numri = numri;
    }

    public USSDReceiver(MainActivity ma) {
        this.ma = ma;
    }

    public void firstRun() {
        i=0;
        responses.removeAll(responses);
        ma.dialNumber("121*" + numri + "*" + vlerat.get(i) + "*00000");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String response = intent.getStringExtra("message");
        Log.i("USSDReceiver", "Response: " + response);
        if (response.length() < 12)
            ma.setInfoText(response);
        else {
            responses.add(response);
            try {
                Log.i("onReceive", "response to contains jane transferuar...");
                if (response.contains("euro jane transferuar nga llogaria juaj ne MSISDN")) {
                    i++;
                    Log.i("onReceive", "contains jane transferuar...");
                    if (i < vlerat.size())
                        ma.dialNumber("121*" + numri + "*" + vlerat.get(i) + "*00000");
                    else if (i == vlerat.size())
                        ma.write();
                }
            } catch (Exception e) {
                Log.i("onReceive Exception", e.getMessage());
            }
        }
    }

    public ArrayList<String> getResponses() {
        return responses;
    }
}

