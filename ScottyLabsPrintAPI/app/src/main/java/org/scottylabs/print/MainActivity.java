package org.scottylabs.print;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Uri fileUri = null;
    private String fileName = "";
    private boolean hasFile=false;
    private boolean idSet=false;
    private String andrewId="";
    private boolean editingId=true;
    private boolean initialized=false;
    private static String SETTING_ANDREW_ID="ANDREW_ID";
    private static String SETTINGS_NAME="SETTINGS";
    private static String DEFAULT_FILE_NAME="file.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    // Sets initial state (only if not already set)
    private void initialize(){
        if(!initialized){
            hasFile = loadFile();
            idSet=loadId();
            editingId=!idSet;
            updateScreen();
            configureSendAction();
            initialized=true;
        }
    }

    // Saves the andrew ID when the user presses the done button on the keyboard
    private void configureSendAction(){
        EditText editAndrewIdView=(EditText)findViewById(R.id.editAndrewId);
        editAndrewIdView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(editingId){
                        setAndrewId(null);
                    }
                    handled = true;
                }
                return handled;
            }
        });
    }

    // Gets the intent and loads the file to print
    private boolean loadFile(){
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("application/pdf".equals(type)) {
                loadSentPdf(intent); // Handle text being sent
                return true;
            }
        }
        return false;
    }

    // Loads the andrew ID from settings
    private boolean loadId(){
        SharedPreferences settings;
        settings = getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        andrewId=settings.getString(SETTING_ANDREW_ID,"");
        return !andrewId.equals("");
    }

    // Retrieves file data from the intent
    void loadSentPdf(Intent intent) {
        fileUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        fileName = getFileName(fileUri, DEFAULT_FILE_NAME);
    }

    // Updates screen based on state
    void updateScreen(){
        updateMessage();
        updateTextEdit();
        updatePrintButton();
    }

    // Updates the message on the screen based on current state
    void updateMessage(){
        TextView confirmMessageView=(TextView)findViewById(R.id.infoMessage);
        String message;
        if(idSet){
            if(hasFile){
                message=getResources().getString(R.string.msg_confirm_print);
                message=String.format(message,fileName);
            }
            else{
                message=getResources().getString(R.string.msg_config_complete);
            }
        }
        else{
            message = getResources().getString(R.string.msg_please_enter_id);
        }
        confirmMessageView.setText(message);
    }

    // Updates appearance of the andrew ID based on current state
    void updateTextEdit(){
        EditText editAndrewIdView=(EditText)findViewById(R.id.editAndrewId);
        TextView andrewIdView=(TextView)findViewById(R.id.andrewId);
        if(editingId){
            andrewIdView.setVisibility(View.INVISIBLE);
            editAndrewIdView.setVisibility(View.VISIBLE);
            editAndrewIdView.setEnabled(true);
        }
        else{
            andrewIdView.setText(andrewId);
            editAndrewIdView.setText(andrewId);
            //andrewIdView.setVisibility(View.VISIBLE);
            //editAndrewIdView.setVisibility(View.INVISIBLE);
            editAndrewIdView.setEnabled(false);
        }
    }

    // Updates the appearance of the print button based on current state
    void updatePrintButton(){
        Button printButton=(Button)findViewById(R.id.printButton);
        if(hasFile){
            printButton.setVisibility(View.VISIBLE);
            if(idSet&&!editingId){
                printButton.setEnabled(true);
            }
            else{
                printButton.setEnabled(false);
            }
        }
        else{
            printButton.setVisibility(View.GONE);
        }
    }

    // Sends the print request to the API
    public void printBnPress(View view) {
        PrintApiRequest request = new PrintApiRequest(andrewId,fileUri,fileName,this);
        request.send();
    }

    // Runs when edit andre id button is clicked. Saves the andrew id or starts editing it.
    public void setAndrewId(View view) {
        if(editingId) {
            if(saveAndrewId()) {
                editingId = false;
                updateScreen();
                EditText editAndrewIdView = (EditText) findViewById(R.id.editAndrewId);
                editAndrewIdView.requestFocus();
            }
        }
        else{
            editingId=true;
            updateScreen();
        }
    }

    // Reads the andrew id from the text view and saves it if it isn't blank. Returns false if blank.
    public boolean saveAndrewId(){
        EditText editAndrewIdView=(EditText)findViewById(R.id.editAndrewId);
        if (!editAndrewIdView.getText().toString().equals("")) {
            andrewId = editAndrewIdView.getText().toString();
            SharedPreferences settings;
            settings = getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(SETTING_ANDREW_ID,andrewId);
            idSet = true;
            editor.apply();
            return true;
        }
        else{
            return !andrewId.equals("");
        }
    }

    // Gets the name of the file to print or returns the default name
    private String getFileName(Uri filePath, String defaultName){
        ContentResolver contentResolver=getContentResolver();
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
}
