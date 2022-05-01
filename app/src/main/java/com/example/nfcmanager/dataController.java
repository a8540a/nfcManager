package com.example.nfcmanager;




/*
tag입력을 위한 데이터 컨트롤
tag는 4byte로 구성된 block단위로 write지원
따라서 데이터를 byte형태로 변환하고 데이터간 구분을 위해 문자열 "\"(백슬래시) 사용 (입력값에는 백슬래시가 존재하면 안됨)
문자열 처리는 UTF-8이용
 */
public class dataController {

    //String을 byte배열로 변경해주고 구분을 위한 "\"(백슬래시) 추가
    public byte[] stringToByte(String a){
        String c = a+"\\";

        return c.getBytes();
    }

    public String byteToString(byte[] a){
        String string = new String(a);

        return string.replace("\\","");
    }

    public String byteToString2(byte[] a){
        String string = new String(a);
        return string;
    }

    public byte[] mergeStringToByte(String a,String b,String c,String d,String e){
        byte[] abyte = stringToByte(a);
        byte[] bbyte = stringToByte(b);
        byte[] cbyte = stringToByte(c);
        byte[] dbyte = stringToByte(d);
        byte[] ebyte = stringToByte(e);

        int totalLength = abyte.length+bbyte.length+cbyte.length+dbyte.length+ebyte.length;
        byte[] newbyte = new byte[totalLength];

        System.arraycopy(abyte,0,newbyte,0,abyte.length);
        System.arraycopy(bbyte,0,newbyte,abyte.length,bbyte.length);
        System.arraycopy(cbyte,0,newbyte,abyte.length+bbyte.length,cbyte.length);
        System.arraycopy(dbyte,0,newbyte,abyte.length+bbyte.length+cbyte.length,dbyte.length);
        System.arraycopy(ebyte,0,newbyte,abyte.length+bbyte.length+cbyte.length+dbyte.length,ebyte.length);

        return newbyte;
    }
    public String[] mergeByteToString(byte[] a){
        String[] array = byteToString2(a).split("\\\\");
        return array;
    }

    public boolean count92(byte[] a){
        int counter = 0;
        for(int i : a){
            if(i==92){
                counter++;
            }
            if(counter==5)
                return true;
        }
        return false;
    }



}
