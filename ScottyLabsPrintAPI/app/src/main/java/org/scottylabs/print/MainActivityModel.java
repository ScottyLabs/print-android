package org.scottylabs.print;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by Tom on 2/18/2018.
 */

public class MainActivityModel {
    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_ERROR = -1;

    public Uri fileUri = null;
    public String fileName = "";
    public boolean hasFile=false;
    public boolean andrewIdSet=false;
    public String andrewId="";
    public boolean editingId = true;
    public boolean printing = false;
    public int printStatus = STATUS_NORMAL;
    public String printError = "";
    public void saveToBundle(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("EditingId", editingId);
        savedInstanceState.putInt("PrintStatus", printStatus);
        savedInstanceState.putString("PrintError", printError);
    }
    public void restoreFromBundle(Bundle savedInstanceState) {
        editingId = savedInstanceState.getBoolean("EditingId");
        printStatus = savedInstanceState.getInt("PrintStatus");
        printError = savedInstanceState.getString("PrintError");
    }
}
