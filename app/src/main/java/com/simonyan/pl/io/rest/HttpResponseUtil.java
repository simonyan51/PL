package com.simonyan.pl.io.rest;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by simonyan51 on 6/28/17.
 */

public class HttpResponseUtil {

    private static final String LOG_TAG = HttpResponseUtil.class.getSimpleName().toString();

    public static String parseResponse(HttpURLConnection connection) {


        InputStreamReader streamReader = null;
        BufferedReader reader = null;
        String result = null;

        try {

            streamReader = new InputStreamReader(connection.getInputStream());
            reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }

            reader.close();
            streamReader.close();
            connection.disconnect();

            result = stringBuilder.toString();
            Log.d(LOG_TAG, result);


        } catch (IOException e) {

            e.printStackTrace();

        } finally {
            try {

                if (connection != null) {
                    connection.disconnect();
                }

                if (reader != null) {
                    reader.close();
                }

                if (streamReader != null) {
                    streamReader.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return result;

    }

}
