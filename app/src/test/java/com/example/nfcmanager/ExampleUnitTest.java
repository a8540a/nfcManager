package com.example.nfcmanager;

import org.junit.Test;
import com.example.nfcmanager.dataController;
import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void stringByteTest() {
        dataController a = new dataController();
        String b = "가나다abcd123";
        byte[] c = a.stringToByte(b);
        String d = a.byteToString(c);

        assertEquals(d,"가나다abcd123");

    }

}