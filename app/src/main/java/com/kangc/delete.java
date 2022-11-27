package com.kangc;
import java.io.File;

/**
 * Created by Jidoer on 2019/9/13.
 *
 */

public class delete {


    public static Boolean deleteFile(File file) {

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            file.delete();
        } else if (file.exists()) {
            file.delete();
        }
        return true;
    }


}



