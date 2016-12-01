package org.scottylabs.print;

import android.content.Context;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tom on 11/19/2016.
 */

public class PrintApiRequest {
    private String andrewId;
    private Uri filePath;
    private Context context;
    private static String API_URL="http://apis.scottylabs.org/supersecrethash0123456789/howdidyouguessthis/goodjob/print/printfile";
    public PrintApiRequest(String andrewId, Uri filePath, Context context){
        this.andrewId = andrewId;
        this.filePath = filePath;
        this.context = context;
    }
    public boolean send(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try{
            InputStream iStream = context.getContentResolver().openInputStream(filePath);
            MultipartUtility request = new MultipartUtility(API_URL,"UTF-8");
            request.addFormField("andrew_id",andrewId);
            String path="";
            try{
                path=(new java.net.URI(filePath.toString())).getPath();
            }
            catch (java.net.URISyntaxException uriException){
                return false;
            }
            request.addFilePart("file",iStream,"filename.pdf");
            request.finish();
            return true;
        }
        catch (IOException exception){
            String test=exception.getMessage();
            Log.d("*******",test);
            return false;
        }
    }
}
