package org.scottylabs.print;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by Tom on 2/18/2018.
 */

public class MainActivityModel {
    public Uri fileUri = null;
    public String fileName = "";
    public boolean hasFile=false;
    public boolean andrewIdSet=false;
    public String andrewId="";
    public boolean editingId=true;
    public void saveToBundle(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("EditingId", editingId);
    }
    public void restoreFromBundle(Bundle savedInstanceState) {
        editingId = savedInstanceState.getBoolean("EditingId");
    }
}
