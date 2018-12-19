package org.scottylabs.print;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
        renderPrintSettings();
    }

    // Updates the message on the screen based on current state
    void renderMessage(){
        TextView infoMessageView=(TextView)findViewById(R.id.infoMessage);
        String message;
        if(model.printStatus == MainActivityModel.STATUS_ERROR) {
            message="Print error: " + model.printError;
        }
        else if(model.printStatus == MainActivityModel.STATUS_SUCCESS) {
            message=getResources().getString(R.string.msg_print_success);
        }
        else {
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

    void renderPrintSettings() {
        RelativeLayout copiesLayout=(RelativeLayout)findViewById(R.id.copies_relative_layout);
        RadioGroup duplexGroup=(RadioGroup)findViewById(R.id.duplex_radio_group);
        if(model.hasFile) {
            duplexGroup.setVisibility(View.VISIBLE);
            copiesLayout.setVisibility(View.VISIBLE);
        }
        else {
            duplexGroup.setVisibility(View.GONE);
            copiesLayout.setVisibility(View.GONE);
        }
    }

    // Updates the appearance of the print button based on current state
    void renderPrintButton(){
        Button printButton=(Button)findViewById(R.id.printButton);
        String text = getResources().getString(R.string.print_bn_text);
        if(model.hasFile){
            printButton.setVisibility(View.VISIBLE);
            if (model.printing) {
                printButton.setEnabled(false);
                text = getResources().getString(R.string.print_bn_text_printing);
            }
            else {
                if(model.andrewIdSet&&!model.editingId){
                    printButton.setEnabled(true);
                }
                else{
                    printButton.setEnabled(false);
                }
            }
        }
        else{
            printButton.setVisibility(View.GONE);
        }
        printButton.setText(text);
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
        return editAndrewIdView.getText().toString().trim();
    }

    public int readNumberOfCopies() {
        EditText editAndrewIdView=(EditText)findViewById(R.id.num_copies_edit_text);
        String text = editAndrewIdView.getText().toString().trim();
        try {
            int res = Integer.parseInt(text);
            if (res < 1) {
                return 1;
            }
            return res;
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    public DuplexSetting readDuplexSetting() {
        RadioButton radioOneSided=(RadioButton)findViewById(R.id.radio_one_sided);
        RadioButton radioTwoSidedLong=(RadioButton)findViewById(R.id.radio_two_sided_long_edge);
        RadioButton radioTwoSidedShort=(RadioButton)findViewById(R.id.radio_two_sided_short_edge);
        if (radioOneSided.isChecked()) return DuplexSetting.ONE_SIDED;
        if (radioTwoSidedLong.isChecked()) return DuplexSetting.TWO_SIDED_LONG_EDGE;
        if (radioTwoSidedShort.isChecked()) return DuplexSetting.TWO_SIDED_SHORT_EDGE;
        return DuplexSetting.TWO_SIDED_LONG_EDGE;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        controller.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_feedback:
                Intent intent = new Intent(this, FeedbackActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.feedback_menu, menu);
        return true;
    }
}
