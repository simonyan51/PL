package com.simonyan.pl.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.simonyan.pl.R;
import com.simonyan.pl.util.Constant;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class AboutFragment extends BaseFragment implements View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = AboutFragment.class.getSimpleName();
    private static final String LINKEDIN_URL = "https://www.linkedin.com/in/gnel-simonyan-326965144/";

    // ===========================================================
    // Fields
    // ===========================================================

    private Bundle mArgumentData;
    private WebView mWvAbout;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // ===========================================================
    // Constructors
    // ===========================================================

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    public static AboutFragment newInstance(Bundle args) {
        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        findViews(view);
        setListeners();
        getData();
        customizeActionBar();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configWebView();
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    private void setListeners() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    private void configWebView() {

        mWvAbout.loadUrl(LINKEDIN_URL);

        mWvAbout.setWebChromeClient(new CustomWebChromeClient());
        mWvAbout.setWebViewClient(new CustomWebViewClient());
        WebSettings webSettings = mWvAbout.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
    }

    private void findViews(View view) {
        mWvAbout = (WebView) view.findViewById(R.id.wv_about_site);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sr_about_swipe);

    }

    public void getData() {
        if (getArguments() != null) {
            mArgumentData = getArguments().getBundle(Constant.Argument.ARGUMENT_DATA);
        }
    }

    private void customizeActionBar() {

    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================


    private class CustomWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){

            if (!view.getUrl().contains(URL)) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(view.getUrl()));
                startActivity(i);
            }

            return true;
        }
    }

    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }
        @Override
        public void onProgressChanged(WebView view, int progress) {
            if (isAdded()) {
                Log.d(LOG_TAG, String.valueOf(progress));
                if (progress >= 90) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }
    }

}