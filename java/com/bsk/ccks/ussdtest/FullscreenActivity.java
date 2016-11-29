package com.bsk.ccks.ussdtest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;


public class FullscreenActivity extends AppCompatActivity {
    private Uri selected_file;
    private ArrayList<String> pinkodet = new ArrayList<>();
    private TextView txt;
    private Context context;
    private USSDReceiver ussdr = new USSDReceiver(this);
    private Handler handler = new Handler();
    private Runnable dialNumberRunnable;
    private boolean lejohet[] = {false, false, false};

    private void dialNumber(String code) {
        String ussdCode = "*103*" + code + Uri.encode("#");
        Intent i = new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCode));
        startActivityForResult(i, 111);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP)
            checkPermissions();
        IntentFilter filter = new IntentFilter("com.times.ussd.action.REFRESH");
        LocalBroadcastManager.getInstance(this).registerReceiver(ussdr, filter);
        setContentView(R.layout.activity_fullscreen);
        startService(new Intent(this, USSDCodeHandler.class));
        txt = (TextView) findViewById(R.id.textView);
        final Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);
        Button button = (Button) findViewById(R.id.dummy_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lejohet[0]) {
                    startActivityForResult(Intent.createChooser(intent, "Selektoni fajllin"), 123);
                } else {
                    txt.setText("Nuk i keni dhene te drejta aplikacionit\nper ti lexuar te dhenat nga telefoni!");
                }
            }
        });
        Button button1 = (Button) findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("main class", "pinkodet.size(): " + pinkodet.size());
                if (pinkodet.size() != 0) {
                    if (lejohet[1]) {
                        txt.setText(txt.getText() + "\nChecking pincodes");
                        testPinkodin();
                    } else
                        txt.setText("Error: Nuk e keni lejuar aplikacionin te beje thirrje!");
                } else
                    txt.setText("Se pari selektoni fajllin me pinkode");
            }
        });
    }

    protected void testPinkodin() {
        for (int i = 0; i < pinkodet.size(); i++) {
            final int finalI = i;
            dialNumberRunnable = new Runnable() {
                public void run() {
                    dialNumber(pinkodet.get(finalI));
                }
            };
            handler.postDelayed(dialNumberRunnable, i * 10000);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    txt.setText(txt.getText() + "\nPincode: " + (finalI + 1) + " checked!");
                }
            }, (i + 1) * 10000);
            if (i == pinkodet.size() - 1)
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (lejohet[2]) write();
                        else
                            txt.setText("Error: Nuk e keni lejuar aplikacionin qe te shkruaje ne memorje!");
                    }
                }, (i + 1) * 15000);
        }
    }

    public void stopCheckingPinCodes() {
        handler.removeCallbacks(dialNumberRunnable);
        handler.removeMessages(0);
        dialNumberRunnable = new Runnable() {
            public void run() {
                if (lejohet[2]) write();
                else
                    txt.setText("Error: Nuk e keni lejuar aplikacionin qe te shkruaje ne memorje!");
            }
        };
        handler.postDelayed(dialNumberRunnable, 5000);
    }

    protected void fillArray() {
        try {
            boolean b = pinkodet.removeAll(pinkodet);
            BufferedReader br = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(selected_file)));
            String line;
            while ((line = br.readLine()) != null)
                pinkodet.add(line);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            selected_file = data.getData();
            fillArray();
            txt.setText("File Loaded Sucessfully");
        }
    }

    protected void write() {
        File path = context.getExternalFilesDir(null);
        File file = new File(path, new Date() + ".txt");
        try {
            FileOutputStream stream = new FileOutputStream(file);
            for (int i = 0; i < pinkodet.size(); i++) {
                try {
                    stream.write(("Pinkodi: " + pinkodet.get(i) + "; Response: " + ussdr.getResponses().get(i) + "\n").getBytes());
                } catch (Exception e) {
                    stream.write(("Pinkodi: " + pinkodet.get(i) + "; Response: U blloku sim kartela per mbushje me pinkod\n").getBytes());
                }
            }
            stream.close();
            txt.setText(txt.getText() + "\nSucessfully checked pincodes\nFile output has been created!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void checkPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    lejohet[0] = true;
                }if (grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    lejohet[1] = true;
                }if (grantResults.length > 0
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    lejohet[2] = true;
                }
            }
        }
    }
}