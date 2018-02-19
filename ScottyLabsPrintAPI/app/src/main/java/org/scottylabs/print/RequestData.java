package org.scottylabs.print;

import android.content.Context;
import android.net.Uri;

/**
 * Created by Tom on 2/18/2018.
 */

public class RequestData {
    public String andrewId;
    public Uri filePath;
    public String fileName;

    public RequestData(String andrewId, Uri filePath, String fileName){
        this.andrewId = andrewId;
        this.filePath = filePath;
        this.fileName = fileName;
    }
}
