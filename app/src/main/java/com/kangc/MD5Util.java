package com.kangc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    public static String MD5Encryption(String password){
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] digest = messageDigest.digest(password.getBytes());
            for (int i = 0; i <digest.length ; i++) {
                int result = digest[i] & 0xff;
                String hexString = Integer.toHexString(result);
                if (hexString.length() < 2){
                    sb.append("0");
                }
                sb.append(hexString);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
//MD5Util.MD5Encryption(password_str);