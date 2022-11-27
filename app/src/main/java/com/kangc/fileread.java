package com.kangc;

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


public class fileread {


    /*
    //no enter
    public static String readTxt(String path){
        String str = "";
        try {
            File urlFile = new File(path);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            String mimeTypeLine = null ;
            while ((mimeTypeLine = br.readLine()) != null) {
                str = str+mimeTypeLine;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  str;
    }
*/
/*
    public static String ReadTxtWithEnter(File file) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }*/


/*
    InputStream inputStream = mContext.getResources().getAssets().open("address.txt");
    byte[] arrayOfByte = new byte[inputStream.available()];
    inputStream.read(arrayOfByte);
    String address = EncodingUtils.getString(arrayOfByte, "utf-8");
 */

    public static String getFileContent(File file) {
        String content = "";
        if (!file.isDirectory()) {
                try {
                    InputStream instream = new FileInputStream(file);
                    if (instream != null) {
                        InputStreamReader inputreader
                                = new InputStreamReader(instream, "UTF-8");
                        BufferedReader buffreader = new BufferedReader(inputreader);
                        String line = "";
                        while ((line = buffreader.readLine()) != null) {
                            content += line + "\n";
                        }
                        instream.close(); //close instream
                    }
                } catch (java.io.FileNotFoundException e) {
                    Log.d("TestFile", "The File doesn't not exist.");
                } catch (IOException e) {
                    Log.d("TestFile", e.getMessage());
                }
        }
        return content;
    }

    //no enter
    public static String readTxt(String path) {
        String str = "";
        try {
            File urlFile = new File(path);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            String mimeTypeLine = null;
            while ((mimeTypeLine = br.readLine()) != null) {
                str = str + mimeTypeLine;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }


}