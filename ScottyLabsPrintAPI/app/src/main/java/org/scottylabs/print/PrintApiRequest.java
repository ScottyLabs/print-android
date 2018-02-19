package org.scottylabs.print;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Tom on 11/19/2016.
 */

public class PrintApiRequest extends AsyncTask<Void, Void, RequestResult> {
    private static String API_URL="https://apis.scottylabs.org/print/v0/printfile";
    private RequestData requestData;
    private Context context;

    public PrintApiRequest(RequestData data, Context context) {
        requestData = data;
        this.context = context;
    }

    protected RequestResult doInBackground(Void... params) {
        try{
            Log.d("ApiRequest", "Starting upload");
            ContentResolver contentResolver=context.getContentResolver();
            InputStream iStream = contentResolver.openInputStream(requestData.filePath);
            MultipartUtility request = new MultipartUtility(API_URL,"UTF-8");
            request.addFormField("andrew_id",requestData.andrewId);
            request.addFilePart("file",iStream,requestData.fileName, ".pdf");
            List<String> result = request.finish();
            Log.d("ApiRequest", "Upload completed");
            RequestResult requestResult = new RequestResult(result);
            if (!requestResult.success) {
                Log.e("ApiRequest", "Error: " + requestResult.error);
            }
            return requestResult;
        }
        catch (IOException exception){
            Log.e("ApiRequest",exception.getMessage());
            return new RequestResult(false, "Failed to upload file.");
        }
    }
}
