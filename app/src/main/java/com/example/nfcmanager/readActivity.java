package com.example.nfcmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class readActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    TextView uid;
    TextView ProdName;
    TextView qty;
    TextView loc;
    TextView date;
    final static String TAG = "testcode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        uid = findViewById(R.id.textView);
        ProdName = findViewById(R.id.textView2);
        qty = findViewById(R.id.textView4);
        loc = findViewById(R.id.textView3);
        date = findViewById(R.id.textView6);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(nfcAdapter==null){
            Toast.makeText(this,"no nfc",Toast.LENGTH_LONG).show();
        }
        pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);

        Button writeButton = (Button) findViewById(R.id.btn_modify);
        writeButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String uidinput = uid.getText().toString();
                String ProdNameinput = ProdName.getText().toString();
                String qtyinput = qty.getText().toString();
                String locinput = loc.getText().toString();
                String dateinput = date.getText().toString();




                Intent intent = new Intent(readActivity.this, rwActivity.class);
                intent.putExtra("uidinput",uidinput);
                intent.putExtra("prodnameinput",ProdNameinput);
                intent.putExtra("qtyinput",qtyinput);
                intent.putExtra("locinput",locinput);
                intent.putExtra("dateinput",dateinput);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        assert nfcAdapter != null;
        nfcAdapter.enableForegroundDispatch(this,pendingIntent,null,null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Onpause stop listening
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        resolveIntent(intent);
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            assert tag != null;
            byte[] payload = detectTagData(tag).getBytes();

        }
    }

    private String detectTagData(Tag tag) {
        StringBuilder sb = new StringBuilder();
        byte[] id = tag.getId();
        sb.append("NFC ID (dec): ").append(toDec(id)).append('\n');
        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareUltralight.class.getName())) {
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String [] temp = readTag(mifareUlTag);


                uid.setText(temp[0]);
                ProdName.setText(temp[1]);
                qty.setText(temp[3]);
                loc.setText(temp[2]);
                date.setText(temp[4]);

            }
        }
        Log.v("test",sb.toString());
        return sb.toString();
    }
    private long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }
    public String[] readTag(MifareUltralight mifareUlTag) {
        try {
            mifareUlTag.connect();
            byte[] newbyte = new byte[504];
            int length = 0;
            dataController d = new dataController();
            for(int i = 5;i<127;i=i+4){
                byte[] temp = mifareUlTag.readPages(i);

                System.arraycopy(temp,0,newbyte,length,16);
                length = length + temp.length;



                if(d.count92(newbyte)){
                    break;
                }
            }
            return d.mergeByteToString(newbyte);
        } catch (IOException e) {
            Log.e(TAG, "IOException while reading MifareUltralight message...", e);
        } finally {
            if (mifareUlTag != null) {
                try {
                    mifareUlTag.close();
                }
                catch (IOException e) {
                    Log.e(TAG, "Error closing tag...", e);
                }
            }
        }
        return null;
    }
}