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
    public boolean awaitingPermissions;
    public int printStatus = STATUS_NORMAL;
    public String printError = "";
    //public int copies = 1;
    //public DuplexSetting sides = DuplexSetting.TWO_SIDED_LONG_EDGE;
    public void saveToBundle(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("EditingId", editingId);
        savedInstanceState.putInt("PrintStatus", printStatus);
        savedInstanceState.putString("PrintError", printError);
        savedInstanceState.putBoolean("AwaitingPermissions", awaitingPermissions);
        //savedInstanceState.putInt("Copies", copies);
        //savedInstanceState.putSerializable("Sides", sides);
    }
    public void restoreFromBundle(Bundle savedInstanceState) {
        editingId = savedInstanceState.getBoolean("EditingId");
        printStatus = savedInstanceState.getInt("PrintStatus");
        printError = savedInstanceState.getString("PrintError");
        awaitingPermissions = savedInstanceState.getBoolean("AwaitingPermissions");
        //copies = savedInstanceState.getInt("Copies");
        //sides = (DuplexSetting)savedInstanceState.getSerializable("Sides");
    }
}
