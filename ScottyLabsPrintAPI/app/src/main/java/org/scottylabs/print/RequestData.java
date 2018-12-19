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
    public int copies;
    public DuplexSetting sides;

    public RequestData(String andrewId, ContentResolver contentResolver, Uri fileUri,
                       String fileName, int copies, DuplexSetting sides){
        this.andrewId = andrewId;
        this.contentResolver = contentResolver;
        this.fileUri = fileUri;
        this.fileName = fileName;
        this.copies = copies;
        this.sides = sides;
    }

    public String getDuplexSettingString() {
        switch (sides) {
            case ONE_SIDED: {
                return "one-sided";
            }
            case TWO_SIDED_LONG_EDGE: {
                return "two-sided-long-edge";
            }
            case TWO_SIDED_SHORT_EDGE: {
                return "two-sided-short-edge";
            }
        }
        return "";
    }
}
