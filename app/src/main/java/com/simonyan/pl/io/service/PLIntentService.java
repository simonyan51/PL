package com.simonyan.pl.io.service;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.simonyan.pl.io.rest.RequestHelper;
import com.simonyan.pl.util.Constant;
import com.simonyan.pl.util.Logger;

public class PLIntentService extends IntentService {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = PLIntentService.class.getSimpleName();

    private class Extra {
        static final String URL = "URL";
        static final String POST_ENTITY = "POST_ENTITY";
        static final String SUBSCRIBER = "SUBSCRIBER";
        static final String REQUEST_TYPE = "REQUEST_TYPE";
    }

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    public PLIntentService() {
        super(PLIntentService.class.getName());
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Start/stop commands
    // ===========================================================

    /**
     * @param url         - calling api url
     * @param requestType - string constant that helps us to distinguish what request it is
     * @param postEntity  - POST request entity (json string that must be sent on server)
     * @param subscriber  - object(class) that started service
     */

    public static void start(Context context, String subscriber, String url, String postEntity,
                             int requestType) {
        Intent intent = new Intent(context, PLIntentService.class);
        intent.putExtra(Extra.SUBSCRIBER, subscriber);
        intent.putExtra(Extra.URL, url);
        intent.putExtra(Extra.REQUEST_TYPE, requestType);
        intent.putExtra(Extra.POST_ENTITY, postEntity);
        context.startService(intent);
    }

    public static void start(Context context, String subscriber, String url,
                             int requestType) {
        Intent intent = new Intent(context, PLIntentService.class);
        intent.putExtra(Extra.SUBSCRIBER, subscriber);
        intent.putExtra(Extra.URL, url);
        intent.putExtra(Extra.REQUEST_TYPE, requestType);
        context.startService(intent);
    }

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getExtras().getString(Extra.URL);
        String data = intent.getExtras().getString(Extra.POST_ENTITY);
        String subscriber = intent.getExtras().getString(Extra.SUBSCRIBER);
        int requestType = intent.getExtras().getInt(Extra.REQUEST_TYPE);
        Logger.i(LOG_TAG, requestType + Constant.Symbol.SPACE + url);

        RequestHelper.makeRequest(RequestHelper.RequestMethod.POST, "https://s3-eu-west-1.amazonaws.com/developer-application-test/cart/5/detail");

    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Util
    // ===========================================================

}