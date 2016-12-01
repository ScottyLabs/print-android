package org.scottylabs.print;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
    private boolean hasFile=false;
    private boolean idSet=false;
    private String andrewId="";
    private boolean editingId=true;
    private boolean initialized=false;
    private int printStatus=0; //0 means not printed, 1 means printing, 2 is done, -1 is error.
    private static String SETTING_ANDREW_ID="ANDREW_ID";
    private static String SETTINGS_NAME="SETTINGS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }
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
    private boolean loadId(){
        SharedPreferences settings;
        settings = getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        andrewId=settings.getString(SETTING_ANDREW_ID,"");
        return !andrewId.equals("");
    }
    void loadSentPdf(Intent intent) {
        fileUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
    }
    void updateScreen(){
        updateMessage();
        updateTextEdit();
        updatePrintButton();
    }
    void updateMessage(){
        TextView confirmMessageView=(TextView)findViewById(R.id.infoMessage);
        String message;
        if(idSet){
            if(hasFile){
                message=getResources().getString(R.string.msg_confirm_print);
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
    void updateMessage2(){
        TextView confirmMessageView=(TextView)findViewById(R.id.infoMessage);
        TextView andrewIdView=(TextView)findViewById(R.id.andrewId);
        EditText editAndrewIdView=(EditText)findViewById(R.id.editAndrewId);
        Button printButton=(Button)findViewById(R.id.printButton);
        String message;
        andrewIdView.setText(andrewId);
        editAndrewIdView.setText(andrewId);
        if(idSet){
            andrewIdView.setVisibility(View.VISIBLE);
            editAndrewIdView.setVisibility(View.INVISIBLE);
            if(hasFile){
                message=getResources().getString(R.string.msg_confirm_print);
                printButton.setEnabled(true);
                printButton.setVisibility(View.VISIBLE);
            }
            else{
                message=getResources().getString(R.string.msg_config_complete);
                printButton.setVisibility(View.INVISIBLE);
            }
        }
        else {
            andrewIdView.setVisibility(View.INVISIBLE);
            editAndrewIdView.setVisibility(View.VISIBLE);
            message = getResources().getString(R.string.msg_please_enter_id);
            if(hasFile){
                printButton.setEnabled(false);
                printButton.setVisibility(View.VISIBLE);
            }
            else{
                printButton.setVisibility(View.INVISIBLE);
            }
        }
        confirmMessageView.setText(message);
    }
    public void printBnPress(View view) {
        PrintApiRequest request = new PrintApiRequest(andrewId,fileUri,this);
        request.send();
    }
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
}
