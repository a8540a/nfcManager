package com.example.nfcmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class rwActivity extends AppCompatActivity {
    TextView uidval,dateval;
    EditText prodname, qty, loc;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    byte[] byteString;
    product product = new product();
    DataService dataservice = new DataService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rw);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(nfcAdapter==null){
            Toast.makeText(this,"no nfc",Toast.LENGTH_LONG).show();
        }
        pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);

        Intent intent = getIntent();
        String uidinput = intent.getStringExtra("uidinput");
        String prodnameinput = intent.getStringExtra("prodnameinput");
        String qtyinput = intent.getStringExtra("qtyinput");
        String locinput = intent.getStringExtra("locinput");
        String dateinput = intent.getStringExtra("dateinput");


        uidval = findViewById(R.id.textView11);
        dateval =findViewById(R.id.textView12);
        prodname= findViewById(R.id.editTextTextPersonName8);
        qty=findViewById(R.id.editTextNumber);
        loc=findViewById(R.id.editTextTextPersonName9);

        uidval.setText(uidinput);
        dateval.setText(dateinput);
        prodname.setText(prodnameinput);
        qty.setText(qtyinput);
        loc.setText(locinput);




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
                writeTag(mifareUlTag);
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




    public void writeTag(MifareUltralight mifareUlTag) {
        byte[] writeData = writeOnDB(uidval,prodname,loc,qty,dataservice);
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
            System.out.println("fail to write tag");
        } finally {
            try {
                mifareUlTag.close();
            } catch (IOException e) {
                System.out.println("fail to write tag");
            }
        }

    }

    public byte[] writeOnDB(TextView uid,EditText prodname, EditText loc,EditText qty, DataService dataService){
        Thread th = new Thread(){
            public void run(){
                try {
                    Map<String, String> map = new HashMap();
                    map.put("uid",uid.getText().toString());
                    map.put("prodName", prodname.getText().toString());
                    map.put("qty", qty.getText().toString());
                    map.put("loc", loc.getText().toString());
                    System.out.println(map.get("uid"));
                    System.out.println(map.get("prodName"));
                    System.out.println(map.get("qty"));
                    System.out.println(map.get("loc"));
                    dataService.insert.insertOne(map).execute();
                    List<product> p = dataService.select.selectAll().execute().body();
                    product = p.get(p.size()-1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        th.start();
        try{
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        dataController d = new dataController();

        System.out.println(product.getuID());
        String tempuid = Integer.toString(product.getuID());
        String tempprodName = product.getName();
        String tempLoc = product.getLoc();
        String tempqty = Integer.toString(product.getQty());
        Date tempdate = product.getDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String tempstr = format.format(tempdate);

        byteString = d.mergeStringToByte(tempuid,tempprodName,tempLoc,tempqty,tempstr);

        return byteString;
    }
}