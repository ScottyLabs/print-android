package org.scottylabs.print;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
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
    private String fileName;
    private Context context;
    private static String API_URL="https://apis.scottylabs.org/print/v0/printfile";
    public PrintApiRequest(String andrewId, Uri filePath, String fileName, Context context){
        this.andrewId = andrewId;
        this.filePath = filePath;
        this.fileName = fileName;
        this.context = context;
    }
    public void send(){
        RequestData data = new RequestData(andrewId, filePath, fileName);
        new PrintApiRequestNew(data, context).execute();
    }
}
