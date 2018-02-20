package org.scottylabs.print;

import android.content.Context;
import android.net.Uri;

import java.io.InputStream;

/**
 * Created by Tom on 2/18/2018.
 */

public class RequestData {
    public String andrewId;
    public InputStream fileStream;
    public String fileName;

    public RequestData(String andrewId, InputStream fileStream, String fileName){
        this.andrewId = andrewId;
        this.fileStream = fileStream;
        this.fileName = fileName;
    }
}
