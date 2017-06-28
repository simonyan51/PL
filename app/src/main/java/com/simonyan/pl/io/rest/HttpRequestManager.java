package com.simonyan.pl.io.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.simonyan.pl.util.Constant.Util.UTF_8;

/**
 * Created by simonyan51 on 6/28/17.
 */

public class HttpRequestManager {


    public static final String LOG_TAG = HttpRequestManager.class.getSimpleName().toString();

    public class RequestType {
        public static final int PRODUCT_LIST = 1;
        public static final int PRODUCT_ITEM = 2;
    }

    public class RequestMethod {
        public static final String POST = "POST";
        public static final String GET = "GET";
        public static final String PUT = "PUT";
    }

    public class Headers {
        public static final String CONTENT_TYPE = "Content-Type";
    }

    public class Payloads {
        public static final String APPLICATION_JSON = "application/json";
    }

    public static HttpURLConnection executeRequest(String apiUrl, String requestMethod, String data) {

        HttpURLConnection connection = null;

        try {

            URL ulr = new URL(apiUrl);
            connection = (HttpURLConnection) ulr.openConnection();
            connection.setRequestMethod(requestMethod);
            connection.setUseCaches(false);

            switch (requestMethod) {

                case RequestMethod.GET:
                    connection.connect();
                    break;

                case RequestMethod.PUT:
                case RequestMethod.POST:

                    connection.setRequestProperty(Headers.CONTENT_TYPE, Payloads.APPLICATION_JSON);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.connect();

                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(data != null ? data.getBytes(UTF_8) : new byte[]{0});
                    outputStream.flush();
                    outputStream.close();

                    break;

            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return connection;

    }

}
