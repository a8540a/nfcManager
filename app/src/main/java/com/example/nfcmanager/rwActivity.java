package com.example.nfcmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class rwActivity extends AppCompatActivity {
    TextView uidval,dateval;
    EditText prodname, qty, loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rw);

        Intent intent = getIntent();
        String uidinput = intent.getStringExtra("uidinput");
        String prodnameinput = intent.getStringExtra("prodnameinput");
        String qtyinput = intent.getStringExtra("qtyinput");
        String locinput = intent.getStringExtra("locinput");
        String dateinput = intent.getStringExtra("dateinput");


        uidval = findViewById(R.id.textView11);
        dateval =findViewById(R.id.textView12);
        prodname= findViewById(R.id.editTextTextPersonName8);
        qty=findViewById(R.id.editTextTextPersonName9);
        loc=findViewById(R.id.editTextTextPersonName10);

        uidval.setText(uidinput);
        dateval.setText(dateinput);
        prodname.setText(prodnameinput);
        qty.setText(qtyinput);
        loc.setText(locinput);




    }
}