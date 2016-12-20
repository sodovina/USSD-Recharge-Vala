package ussdtranscom.bsk.transferokredit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private USSDReceiver ussdr = new USSDReceiver(this);
    private TextView bilanci_label, rezultati_label;
    private Button butoni_ekzekutues;
    private EditText numri, vlera;
    private KtheVlerenTeIma konverto = new KtheVlerenTeIma();
    private Context context;
    private double bilanci = 0;
    private String[] numbers = {"44723140", "44723359", "44723408", "44723409", "44723432", "44723441",
            "44723447", "44723448", "44723454", "44723455", "44723457", "44723459", "44723467", "44723482",
            "44722059", "44719020", "44717203", "44716898" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP)
            checkPermissions();
        context = getApplicationContext();

        IntentFilter filter = new IntentFilter("com.times.transfero.action.REFRESH");
        LocalBroadcastManager.getInstance(this).registerReceiver(ussdr, filter);
        startService(new Intent(this, USSDCodeHandler.class));

        bilanci_label = (TextView) findViewById(R.id.info_text);
        bilanci_label.setText("Bilanci juaj eshte: 0 \u20ac");
        rezultati_label = (TextView) findViewById(R.id.rezultati_text);
        butoni_ekzekutues = (Button) findViewById(R.id.butoni_ekzekutues);
        numri = (EditText) findViewById(R.id.fusha_numrit);
        vlera = (EditText) findViewById(R.id.fusha_vlera);
        butoni_ekzekutues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if ((numri.getText().toString().startsWith("44") | numri.getText().toString().startsWith("45")) & numri.getText().toString().length() == 8) {
                        if (numri.getText().toString().startsWith("455") | numri.getText().toString().startsWith("456")) {
                            Toast.makeText(MainActivity.this, "Numer i ZMobiles, nuk shkojn mbushjet ne kete numer!", Toast.LENGTH_LONG).show();
                        } else {
                            int nr = new Integer(numri.getText().toString()).intValue();
                            int vl = new Integer(vlera.getText().toString()).intValue();
                            if (bilanci >= Math.abs(vl) & bilanci > 0) {
                                ussdr.setNumri(nr);
                                ussdr.setVlerat(konverto.merrListenEVlerave(vl));
                                ussdr.firstRun();
                            } else {
                                Toast.makeText(MainActivity.this, "Nuk keni bilanc te mjaftueshem.", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Numri nuk eshte i shenuar ne formatin e sakte, shenoni 44/45 pastaj numrin!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Se pari shkruani numrin dhe vleren qe deshironi ta transferoni", Toast.LENGTH_LONG).show();
                }
            }
        });
        getBalance();
    }

    protected void checkPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
    }

    public void getBalance() {
        Log.i("getBalance", "getBalance called");
        dialNumber("101");
    }

    public void setInfoText(String text) {
        Log.i("setInfoText", "setInfoText changed");
        double d = new Double(text).doubleValue();
        bilanci = d;
        DecimalFormat df = new DecimalFormat("#,###.##");
        bilanci_label.setText("Bilanci juaj eshte: " + df.format(d) + " \u20ac");
    }

    public void dialNumber(String code) {
        String ussdCode = "*" + code + Uri.encode("#");
        Intent i = new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussdCode));

        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_FROM_BACKGROUND);
        //startActivity(launchCall);

        startActivityForResult(i, 111);
    }

    protected void write() {
        File path = context.getExternalFilesDir(null);
        File file = new File(path, new Date() + ".txt");
        try {
            FileOutputStream stream = new FileOutputStream(file);
            for (int i = 0; i < ussdr.getResponses().size(); i++) {
                try {
                    stream.write((ussdr.getResponses().get(i) + "\n").getBytes());
                } catch (Exception e) {
                    stream.write((ussdr.getResponses().get(i) + "\n").getBytes());
                }
            }
            stream.close();
            getBalance();
            DecimalFormat df = new DecimalFormat("#,###.##");
            rezultati_label.setText("Krediti prej " + df.format(new Double(vlera.getText().toString()).doubleValue()) + " \u20ac u transferua ne numrin " + numri.getText().toString() + " me sukses!");
            vlera.setText("");
            numri.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}