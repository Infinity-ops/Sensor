package util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public  class JSONHelper {
    public static void mCreateAndSaveFile(String params, String mJsonResponse,Context context) {
        try {
            FileWriter file = new FileWriter("/data/data/" + context.getPackageName() + "/" + params);
            file.write(mJsonResponse);
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String mReadJsonData(String params,Context context) {
        String mResponse="";
        try {
            File f = new File("/data/data/" + context.getPackageName() + "/" + params);
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
             mResponse = new String(buffer);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mResponse;
    }

    public static void deleteFile(String params, Context context) {
        try {
            File file = new File("/data/data/" + context.getPackageName() + "/" + params);
        if(file.exists()){
            file.getCanonicalFile().delete();
            if(file.exists()){
                context.deleteFile(file.getName());
            }
        }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
