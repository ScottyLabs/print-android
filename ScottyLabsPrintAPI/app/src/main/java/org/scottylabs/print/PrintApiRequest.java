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
import java.io.FileNotFoundException;
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

    public PrintApiRequest(RequestData data) {
        requestData = data;
    }

    protected RequestResult doInBackground(Void... params) {

        InputStream inputStream;
        try{
            Log.d("Print API", "Opening stream");
            inputStream = requestData.contentResolver.openInputStream(requestData.fileUri);
        }
        catch (FileNotFoundException exception){
            Log.e("ApiRequest", exception.getMessage() + exception.getClass());
            if (exception.getMessage().contains("EACCES (Permission denied)")) {
                return new RequestResult(false, "Permission Denied", true);
            }
            return new RequestResult(false, "Failed to open file");
        }

        try{
            Log.d("ApiRequest", "Starting upload");
            MultipartUtility request = new MultipartUtility(API_URL,"UTF-8");
            request.addFormField("andrew_id",requestData.andrewId);
            request.addFormField("copies", requestData.copies + "");
            request.addFormField("sides", requestData.getDuplexSettingString());
            Log.d("ApiRequest", "Adding file");
            request.addFilePart("file",inputStream,requestData.fileName, ".pdf");
            Log.d("ApiRequest", "Uploading file");
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
