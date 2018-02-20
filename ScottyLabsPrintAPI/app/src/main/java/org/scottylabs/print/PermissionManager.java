package org.scottylabs.print;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

/**
 * Created by Tom on 2/19/2018.
 */

public class PermissionManager {
    public static final int PROMPT_ACCEPTED = 1;
    public static final int PROMPT_REJECTED = -1;
    public static final int PROMPT_OPEN = 0;

    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 883197;
    public boolean checkFilePermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }
    public boolean requestFilePermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }
        if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
            return true;
        }
        return false;
    }
    public int checkRequestGranted(int requestCode, @NonNull String[] perms,
                                       @NonNull int[] grantResults) {
        if (requestCode != MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE) {
            return PROMPT_OPEN;
        }
        for (int i = 0; i < perms.length; i++) {
            if (perms[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    return PROMPT_ACCEPTED;
                }
                return PROMPT_REJECTED;
            }
        }
        return PROMPT_OPEN;
    }
}
