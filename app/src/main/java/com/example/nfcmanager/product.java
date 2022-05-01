package com.example.nfcmanager;

import java.util.Date;

public class product {

    private int uID;
    private String prodName;
    private String loc;
    private int qty;
    private Date date;

    public int getuID(){
        return uID;
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
    public Date date(){
        return date;
    }

}