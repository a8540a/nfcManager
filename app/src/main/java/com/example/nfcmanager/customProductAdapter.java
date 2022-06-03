package com.example.nfcmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class customProductAdapter extends ArrayAdapter<product> {
    public customProductAdapter(Context context, List<product> products) {
        super(context, 0, products);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_product, parent, false);
        }

        product p = getItem(position);


        TextView uidval = (TextView) convertView.findViewById(R.id.uidval);
        TextView prodnameval = (TextView) convertView.findViewById(R.id.prodnameval);
        TextView qtyval = (TextView) convertView.findViewById(R.id.qtyval);
        TextView locval = (TextView) convertView.findViewById(R.id.locval);
        TextView dateval = (TextView) convertView.findViewById(R.id.dateval);

        uidval.setText(String.valueOf(p.getuID()));
        prodnameval.setText(p.getName());
        qtyval.setText(String.valueOf(p.getQty()));
        locval.setText(p.getLoc());

        Date tempdate = p.getDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String tempstr = format.format(tempdate);
        dateval.setText(tempstr);
        return convertView;
    }
}