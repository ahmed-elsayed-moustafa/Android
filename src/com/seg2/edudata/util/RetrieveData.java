package com.seg2.edudata.util;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class RetrieveData {
    private final String url;
    private JSONArray array;

    public RetrieveData(String url) {
        this.url = url;
    }

    public JSONArray getData() {
        Thread updateList = new Thread() {
            public void run() {
                try (
                        InputStream is = new URL(url).openStream();
                        BufferedReader input = new BufferedReader(new InputStreamReader(is))) {
                    // Connect to the URL
                    // !Must have <uses-permission android:name="android.permission.INTERNET" /> in AndroidManifest.xml
                    StringBuilder sb = new StringBuilder();
                    int cp;
                    while ((cp = input.read()) != -1) {
                        sb.append((char) cp);
                    }
                    String json = sb.toString(); //the whole string (everything from the webpage)

                    array = (new JSONArray(json).getJSONArray(1));

                } catch (Exception e) {
                    array = null;
                }
            }
        };
        updateList.start();

        try {
            updateList.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return array;
    }
}
