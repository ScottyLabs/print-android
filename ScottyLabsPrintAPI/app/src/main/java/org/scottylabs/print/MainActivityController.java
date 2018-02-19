package org.scottylabs.print;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Tom on 2/18/2018.
 */

public class MainActivityController {


    private static String DEFAULT_FILE_NAME="file.pdf";

    private MainActivity view;
    private MainActivityModel model;
    private SettingsManager settings;
    private Context context;

    public MainActivityController(MainActivity mainActivity, MainActivityModel model) {
        view = mainActivity;
        this.model = model;
        this.context = view.getApplicationContext();
        this.settings = new SettingsManager(context);
        loadModel();
        view.render();
    }

    private void loadModel(){
        loadFileFromIntent();
        loadAndrewId();
    }

    // Gets the intent and loads the file to print
    private void loadFileFromIntent(){
        Intent intent = view.getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Log.d("Print API", "Looking for file");
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("application/pdf".equals(type)) {
                model.fileUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                model.fileName = getFileName(model.fileUri, DEFAULT_FILE_NAME);
                model.hasFile = true;
                return;
            }
        }
        model.hasFile = false;
    }

    // Reads the the andrew ID from settings
    private void loadAndrewId() {
        model.andrewId = settings.getAndrewId();
        model.andrewIdSet = !model.andrewId.equals("");
        model.editingId = !model.andrewIdSet;
    }

    // Gets the name of the file to print or returns the default name
    private String getFileName(Uri filePath, String defaultName){
        ContentResolver contentResolver=view.getContentResolver();
        String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
        Cursor metaCursor = contentResolver.query(filePath, projection, null, null, null);
        String fileName=defaultName;
        if (metaCursor != null) {
            try {
                if (metaCursor.moveToFirst()) {
                    fileName = metaCursor.getString(0);
                }
            } finally {
                metaCursor.close();
            }
        }
        return fileName;
    }

    public void sendPrintRequest() {
        PrintApiRequest request = new PrintApiRequest(model.andrewId,model.fileUri,model.fileName,context);
        request.send();
    }

    public void setAndrewIdBnPress() {
        if(model.editingId) {
            saveAndrewId();
            if(model.andrewIdSet) {
                model.editingId = false;
                view.render();
                view.setFocusOnAndrewIdEditText();
            }
        }
        else{
            model.editingId=true;
            view.render();
        }
    }

    public void keyboardDoneBnPress() {
        if(model.editingId){
            setAndrewIdBnPress();
        }
    }

    // Reads the andrew id from the text view and saves it if it isn't blank. Returns false if blank.
    public void saveAndrewId(){
        String newId = view.readAndrewIdEditText();
        if (!newId.equals("")) {
            settings.setAndrewId(newId);
            model.andrewId = newId;
            model.andrewIdSet = true;
        }
    }
}
