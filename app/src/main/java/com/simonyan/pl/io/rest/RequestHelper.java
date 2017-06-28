package com.simonyan.pl.io.rest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by simonyan51 on 6/27/17.
 */

public class RequestHelper {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = RequestHelper.class.getSimpleName();

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
    // Methods for/from SuperClass
    // ===========================================================

    public static void makeRequest(String type, String url) {
        URL ulr;
        HttpURLConnection connection = null;
        try {
            ulr = new URL(url);
            connection = (HttpURLConnection) ulr.openConnection();
            connection.setRequestMethod(type);
            switch (type) {
                case RequestMethod.GET:

                    break;
                case RequestMethod.POST:
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    break;
                case RequestMethod.PUT:
                    break;
                case RequestMethod.DELETE:
                    break;
            }
            connection.setUseCaches(false);
            connection.connect();

            String result = ParsingDataHelper.parseHttpRequest(connection);

        } catch (IOException e) {
            connection.disconnect();
            e.printStackTrace();
        }
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
