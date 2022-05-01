package com.example.nfcmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigInteger;
import android.os.Bundle;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.MifareClassic;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.sql.Statement;



public class writeActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    String prodName,Loc,Date,Qty,uID = "00000000";
    byte[] byteString;

    final static String TAG = "testcode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(nfcAdapter==null){
            Toast.makeText(this,"no nfc",Toast.LENGTH_LONG).show();
        }
        pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);

        Button writeButton = (Button) findViewById(R.id.set);
        EditText uid = findViewById(R.id.editTextTextPersonName);
        EditText ProdName = findViewById(R.id.editTextTextPersonName2);
        EditText qty = findViewById(R.id.editTextTextPersonName3);
        EditText loc = findViewById(R.id.editTextTextPersonName4);
        EditText date = findViewById(R.id.editTextTextPersonName5);


        writeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                prodName = ProdName.getText().toString();
                Loc = loc.getText().toString();
                Date = date.getText().toString();
                Qty = qty.getText().toString();
                uID = uid.getText().toString();

                dataController d = new dataController();
                byteString = d.mergeStringToByte(uID,prodName,Loc,Qty,Date);
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
                writeTag(mifareUlTag,byteString);
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




    public void writeTag(MifareUltralight mifareUlTag,byte[] writeData) {


        try {
            mifareUlTag.connect();
            for(int i = 5;i<writeData.length/4+1+5;i++){

                byte[] temp = new byte[4];
                if(writeData.length-(i-5)*4<4)
                    System.arraycopy(writeData,(i-5)*4,temp,0,writeData.length-(i-5)*4);
                else
                    System.arraycopy(writeData,(i-5)*4,temp,0,4);
                mifareUlTag.writePage(i,temp);
                System.out.println("write compltet");
            }

        } catch (IOException e) {
            Log.e(TAG, "IOException while writing MifareUltralight...", e);
        } finally {
            try {
                mifareUlTag.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException while closing MifareUltralight...", e);
            }
        }

    }




}
