package com.simonyan.pl.io.rest;

import android.content.Context;
import android.os.Bundle;

import com.simonyan.pl.io.rest.entity.HttpConnection;
import com.simonyan.pl.io.rest.util.HttpErrorUtil;
import com.simonyan.pl.util.Constant;
import com.simonyan.pl.util.Logger;
import com.simonyan.pl.util.NetworkUtil;

import java.net.HttpURLConnection;
import java.net.URL;

public class RestHttpClient {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final String LOG_TAG = RestHttpClient.class.getSimpleName();
    private static final String UTF_8 = "UTF-8";
    private static final String TOKEN_VALUE = "Token ";
    private class Header {
        private static final String AUTHORIZATION = "Authorization";
        private static final String CONTENT_TYPE = "Content-Type";
        private static final String ACCEPT_ENCODING = "Accept-Encoding";
        private static final String X_HTTP_METHOD_OVERRIDE = "X-HTTP-Method-Override";
    }
    private class PayloadType {
        private static final String APPLICATION_JSON = "application/json";
    }
    public class RequestMethod {
        public static final String POST = "POST";
        public static final String GET = "GET";
        public static final String PUT = "PUT";
        public static final String HEAD = "HEAD";
        public static final String DELETE = "DELETE";
        public static final String PATCH = "PATCH";
    }
    /* All extra data - headers, tokens, json post values will be put to bundle */
    public class BundleData {
        public static final String TOKEN = "token";
        public static final String JSON_ENTITY = "entity";
    }
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
    // Methods
    // ===========================================================
    /**
     * GET Request
     */
    public static HttpConnection executeGetRequest(final Context context, String url, Bundle bundle) {
        Object token = null;
        if (bundle != null) {
            token = bundle.get(BundleData.TOKEN);
        }
        HttpConnection httpConnection = new HttpConnection();
        HttpURLConnection httpURLConnection = null;
        try {
            Logger.i(LOG_TAG, "Calling URL: " + url);
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            if (token != null) {
                Logger.i(LOG_TAG, "Token: " + String.valueOf(token));
                httpURLConnection.setRequestProperty(Header.AUTHORIZATION, TOKEN_VALUE + token);
            }

            httpURLConnection.setRequestMethod(RequestMethod.GET);

            httpURLConnection.setRequestProperty(Header.CONTENT_TYPE, PayloadType.APPLICATION_JSON);

            httpURLConnection.setRequestProperty(Header.ACCEPT_ENCODING, Constant.Symbol.NULL);

            httpURLConnection.setUseCaches(false);

            httpURLConnection.connect();

            Logger.i(LOG_TAG, "Server response: " + String.valueOf(httpURLConnection.getResponseCode())
                    + Constant.Symbol.SPACE + httpURLConnection.getResponseMessage());

            httpConnection.setHttpURLConnection(httpURLConnection);

            httpConnection.setHttpConnectionSucceeded(true);

            httpConnection.setHttpConnectionCode(httpURLConnection.getResponseCode());

            httpConnection.setHttpConnectionMessage(httpURLConnection.getResponseMessage());

        } catch (Exception e) {
            e.printStackTrace();
            handleFailedConnection(context, url, httpConnection, httpURLConnection, e);
        }

        return httpConnection;

    }


    private static void handleFailedConnection(Context context, String url, HttpConnection httpConnection,
                                               HttpURLConnection httpURLConnection, Exception e) {
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }

        if (!NetworkUtil.getInstance().isConnected(context)) {
            Logger.e(LOG_TAG, "Internet connection error ");
            httpConnection.setHttpConnectionSucceeded(false);
            httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_NO_NETWORK);
            httpConnection.setHttpConnectionMessage(e.getMessage());

        } else if (e.getMessage() != null && e.getMessage().contains
                (HttpErrorUtil.NonNumericStatusCode.UNABLE_TO_RESOLVE_HOST)) {
            Logger.e(LOG_TAG, "Connection time out " + url);
            httpConnection.setHttpConnectionSucceeded(false);
            httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.UNABLE_TO_RESOLVE_HOST);
            httpConnection.setHttpConnectionMessage(e.getMessage());

        } else if (e.getMessage() != null && e.getMessage().contains
                (HttpErrorUtil.NonNumericStatusCode.HTTP_SERVER_TIMEOUT)) {
            Logger.e(LOG_TAG, "Connection time out " + url);
            httpConnection.setHttpConnectionSucceeded(false);
            httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_SERVER_TIMEOUT);
            httpConnection.setHttpConnectionMessage(e.getMessage());

        } else if (e.getMessage() != null && e.getMessage().contains
                (HttpErrorUtil.NonNumericStatusCode.HTTP_CONNECTION_REFUSED)) {
            Logger.e(LOG_TAG, "Connection refused by server " + url);
            httpConnection.setHttpConnectionSucceeded(false);
            httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_CONNECTION_REFUSED);
            httpConnection.setHttpConnectionMessage(e.getMessage());

        } else {

            if (e.getMessage() != null) {
                Logger.e(LOG_TAG, e.getMessage());
                httpConnection.setHttpConnectionSucceeded(false);
                httpConnection.setHttpConnectionCode(HttpErrorUtil.NumericStatusCode.HTTP_UNKNOWN_SERVER_ERROR);
                httpConnection.setHttpConnectionMessage(e.getMessage());
            }
        }
    }

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

}