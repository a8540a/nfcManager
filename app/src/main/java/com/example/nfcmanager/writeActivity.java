package com.example.nfcmanager;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class writeActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    String prodName,Loc,Date,Qty,uID = "00000000";
    byte[] byteString;
    final static String TAG = "testcode";
    EditText uid, ProdName, qty,loc,date;
    DataService dataService = new DataService();
    product product = new product();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        DataService dataService = new DataService();



        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(nfcAdapter==null){
            Toast.makeText(this,"no nfc",Toast.LENGTH_LONG).show();
        }
        pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),PendingIntent.FLAG_MUTABLE);


        //uid = findViewById(R.id.editTextTextPersonName);
        ProdName = findViewById(R.id.editTextTextPersonName2);
        qty = findViewById(R.id.editTextNumber2);
        loc = findViewById(R.id.editTextTextPersonName4);
        //date = findViewById(R.id.editTextTextPersonName5);
/*
        Button writeButton = (Button) findViewById(R.id.set);
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
*/
/*
        Button btn_add = (Button) findViewById(R.id.set2);
        btn_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap();
                map.put("prodName", ProdName.getText().toString());
                map.put("qty", qty.getText().toString());
                map.put("loc", loc.getText().toString());

                dataService.insert.insertOne(map).enqueue(new Callback<product>() {
                    @Override
                    public void onResponse(Call<product> call, Response<product> response) {


                        Toast.makeText(writeActivity.this, "등록 완료", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<product> call, Throwable t) {
                        System.out.println("등록실패");
                    }

                });
            }
        });
*/


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
        byte[] writeData = writeOnDB(ProdName, qty,loc,dataService);
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

    public byte[] writeOnDB(EditText ProdName, EditText qty, EditText loc, DataService dataService){
        Map<String, String> map = new HashMap();
        map.put("prodName", ProdName.getText().toString());
        map.put("qty", qty.getText().toString());
        map.put("loc", loc.getText().toString());

        Thread th = new Thread(){
            public void run(){
                try {
                    Map<String, String> map = new HashMap();
                    map.put("prodName", ProdName.getText().toString());
                    map.put("qty", qty.getText().toString());
                    map.put("loc", loc.getText().toString());
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
        System.out.println("처리완료");
        Toast.makeText(writeActivity.this, "등록 완료", Toast.LENGTH_SHORT).show();
        return byteString;
    }


}
