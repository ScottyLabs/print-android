package org.scottylabs.print;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Tom on 2/18/2018.
 */

public class MainActivityController {


    private MainActivity view;
    private MainActivityModel model;
    private SettingsManager settings;
    private Context context;
    private PermissionManager permissions;

    public MainActivityController(MainActivity mainActivity, MainActivityModel model) {
        view = mainActivity;
        this.model = model;
        this.context = view.getApplicationContext();
        this.settings = new SettingsManager(context);
        this.permissions = new PermissionManager();
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
                model.fileName = getFileName(model.fileUri);
                Log.d("Print API", "Received file: " + model.fileUri.getPath());
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
    private String getFileName(Uri filePath){
        String result = null;
        if (filePath.getScheme().equals("content")) {
            Cursor cursor = view.getContentResolver().query(filePath, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = filePath.getLastPathSegment();
        }
        if (!result.contains(".")) {
            result = "file.pdf";
        }
        return result;
    }

    private void setOpenFileError() {
        model.printStatus = MainActivityModel.STATUS_ERROR;
        model.printError = "Failed to open file.";
    }

    public void sendPrintRequest() {
        Log.d("Print API", "Starting print request");

        RequestData data = new RequestData(model.andrewId, view.getContentResolver(), model.fileUri, model.fileName);
        model.printing = true;
        Log.d("Print API", "Starting request");
        new PrintApiRequest(data) {
            @Override
            protected void onPostExecute(RequestResult requestResult) {
                super.onPostExecute(requestResult);
                if (requestResult.success) {
                    model.printStatus = MainActivityModel.STATUS_SUCCESS;
                    model.printError = "";
                }
                else {
                    if (requestResult.permissionError) {
                        if (permissions.requestFilePermission(view)) {
                            Log.d("Print API", "Requesting permission");
                            model.awaitingPermissions = true;
                        }
                        else {
                            setOpenFileError();
                        }
                    }
                    else {
                        model.printStatus = MainActivityModel.STATUS_ERROR;
                        model.printError = requestResult.error;
                    }
                }
                model.printing = false;
                view.render();
            }
        }.execute();
        view.render();
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

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] perms, @NonNull int[] grantResults) {
        if (!model.awaitingPermissions) return;
        int status = permissions.checkRequestGranted(requestCode, perms, grantResults);
        if (status == PermissionManager.PROMPT_REJECTED) {
            Log.d("Print API", "Permission denied");
            model.awaitingPermissions = false;
            setOpenFileError();
        }
        else if (status == PermissionManager.PROMPT_ACCEPTED) {
            Log.d("Print API", "Permission granted");
            model.awaitingPermissions = false;
            sendPrintRequest(); // Let's try again!
        }
        view.render();
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
