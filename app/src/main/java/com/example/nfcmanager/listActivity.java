package com.example.nfcmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.io.IOException;
import java.util.List;

public class listActivity extends AppCompatActivity {
    List<product> p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        DataService d = new DataService();

        Thread th = new Thread(){
            public void run(){
                try {
                    p =d.select.selectAll().execute().body();
                    ListView listView = (ListView)findViewById(R.id.listView);
                    customProductAdapter a = new customProductAdapter(listActivity.this,p);
                    listView.setAdapter(a);
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

    }




}