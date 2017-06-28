package com.simonyan.pl.io.rest;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by simonyan51 on 6/28/17.
 */

public class ParsingDataHelper {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ParsingDataHelper.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================
    // ===========================================================
    // Constructors
    // ===========================================================
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    public static String parseHttpRequest(HttpURLConnection connection) throws IOException {

        InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String inputLine;
        while ((inputLine = reader.readLine()) != null) {
            stringBuilder.append(inputLine);
        }
        reader.close();
        streamReader.close();

        String result = stringBuilder.toString();

        Log.d(LOG_TAG, result);

        return result;

    }

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================
    // ===========================================================
    // Methods
    // ===========================================================
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
