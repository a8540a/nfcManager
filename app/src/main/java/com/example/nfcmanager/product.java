package com.example.nfcmanager;

import java.util.Date;

public class product {

    private int uid;
    private String prodName;
    private String loc;
    private int qty;
    private Date date;

    public int getuID(){
        return uid;
    }
    public String getName(){
        return prodName;
    }
    public String getLoc(){
        return loc;
    }
    public int getQty(){
        return qty;
    }
    public Date getDate(){
        return date;
    }

}