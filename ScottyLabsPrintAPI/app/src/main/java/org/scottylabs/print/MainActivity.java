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

    MainActivityController controller = null;
    MainActivityModel model = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (controller == null) {
            model = new MainActivityModel();
            controller = new MainActivityController(this, model);
        }
        configureKeyboardDoneButton();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        model.saveToBundle(savedInstanceState);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        model.restoreFromBundle(savedInstanceState);
        render();
    }

    // Saves the andrew ID when the user presses the done button on the keyboard
    private void configureKeyboardDoneButton(){
        EditText editAndrewIdView=(EditText)findViewById(R.id.editAndrewId);
        editAndrewIdView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    controller.keyboardDoneBnPress();
                    handled = true;
                }
                return handled;
            }
        });
    }

    // Updates screen based on state
    void render(){
        renderMessage();
        renderTextEdit();
        renderPrintButton();
    }

    // Updates the message on the screen based on current state
    void renderMessage(){
        TextView infoMessageView=(TextView)findViewById(R.id.infoMessage);
        String message;
        if(model.andrewIdSet){
            if(model.hasFile){
                message=getResources().getString(R.string.msg_confirm_print);
                message=String.format(message,model.fileName);
            }
            else{
                message=getResources().getString(R.string.msg_config_complete);
            }
        }
        else{
            message = getResources().getString(R.string.msg_please_enter_id);
        }
        infoMessageView.setText(message);
    }

    // Updates appearance of the andrew ID based on current state
    void renderTextEdit(){
        EditText editAndrewIdView=(EditText)findViewById(R.id.editAndrewId);
        TextView andrewIdView=(TextView)findViewById(R.id.andrewId);
        if(model.editingId){
            andrewIdView.setVisibility(View.INVISIBLE);
            editAndrewIdView.setVisibility(View.VISIBLE);
            editAndrewIdView.setEnabled(true);
        }
        else{
            andrewIdView.setText(model.andrewId);
            editAndrewIdView.setText(model.andrewId);
            editAndrewIdView.setEnabled(false);
        }
    }

    // Updates the appearance of the print button based on current state
    void renderPrintButton(){
        Button printButton=(Button)findViewById(R.id.printButton);
        if(model.hasFile){
            printButton.setVisibility(View.VISIBLE);
            if(model.andrewIdSet&&!model.editingId){
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
        controller.sendPrintRequest();
    }

    public void setAndrewIdBnPress(View view) {
        controller.setAndrewIdBnPress();
    }

    public void setFocusOnAndrewIdEditText() {
        EditText editAndrewIdView = (EditText) findViewById(R.id.editAndrewId);
        editAndrewIdView.requestFocus();
    }

    public String readAndrewIdEditText() {
        EditText editAndrewIdView=(EditText)findViewById(R.id.editAndrewId);
        return editAndrewIdView.getText().toString();
    }
}
