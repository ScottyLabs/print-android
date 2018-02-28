package org.scottylabs.print;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import java.io.InputStream;

/**
 * Created by Tom on 2/18/2018.
 */

public class RequestData {
    public String andrewId;
    public ContentResolver contentResolver;
    public String fileName;
    public Uri fileUri;

    public RequestData(String andrewId, ContentResolver contentResolver, Uri fileUri, String fileName){
        this.andrewId = andrewId;
        this.contentResolver = contentResolver;
        this.fileUri = fileUri;
        this.fileName = fileName;

    }
}
