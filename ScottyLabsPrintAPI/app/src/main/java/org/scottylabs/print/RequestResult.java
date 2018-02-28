package org.scottylabs.print;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Tom on 2/18/2018.
 */

public class RequestResult {
    public boolean success;
    public boolean permissionError;
    public String error;
    public RequestResult(boolean success, String error) {
        this.success = success;
        this.error = error;
        this.permissionError = false;
    }
    public RequestResult(boolean success, String error, boolean permissionError) {
        this(success, error);
        this.permissionError = permissionError;
    }
    public RequestResult(List<String> apiResponse) {
        StringBuilder sb = new StringBuilder();
        for (String line : apiResponse) {
            sb.append(line);
        }
        JSONObject json = null;
        try {
            json = new JSONObject(sb.toString());
            int statusCode = json.getInt("status_code");
            if (statusCode == 200) {
                success = true;
                error = null;
            }
            else {
                success = false;
                String message = json.getString("message");
                error = message;
            }
        }
        catch (JSONException e) {
            success = false;
            error = "Could not understand server response.";
        }
    }
}
