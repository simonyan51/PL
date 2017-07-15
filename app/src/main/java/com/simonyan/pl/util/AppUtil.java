package com.simonyan.pl.util;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by simonyan51 on 7/13/17.
 */

public class AppUtil {

    public static int booleanToInt(boolean b) {
        if (b) {
            return 1;
        } else {
            return 0;
        }
    }

    public static boolean intToBoolean(int b) {
        if (b == 1) {
            return true;
        } else {
            return false;
        }
    }
    public static void closeKeyboard(Activity activity) {
        if (activity != null) {
            if (activity.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity
                        .getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
                        .getWindowToken(), 0);
            }
        }
    }
}
