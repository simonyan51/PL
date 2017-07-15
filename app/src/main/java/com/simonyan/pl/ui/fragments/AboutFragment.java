package com.simonyan.pl.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.simonyan.pl.R;
import com.simonyan.pl.util.Constant;

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
        getActivity().setTitle(getString(R.string.text_simonyan51_about));
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

    }

    private void configWebView() {

        mWvAbout.loadUrl(LINKEDIN_URL);
        WebSettings webSettings = mWvAbout.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    private void findViews(View view) {
        mWvAbout = (WebView) view.findViewById(R.id.wv_about_site);

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

}