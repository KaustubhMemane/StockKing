package com.udacity.stockhawk.ui;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.network.ConnectingNetwork;

import java.net.URL;

/**
 * Created by kmema on 4/22/2017.
 */

public class GetHistory extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{

    private static final String FirstHalf = "https://chartapi.finance.yahoo.com/instrument/1.0/";
    private static final String SecondHalf = "/chartdata;type=quote;range=1y/json";
    public static final String SEARCH_QUERY_URL_EXTRA = "query";
    private static final int SPECIFIED_LOADER = 10;

    public void getHistoryData(String symbol)
    {

        URL jsonResponse = ConnectingNetwork.buildUrl(FirstHalf+""+symbol+""+SecondHalf);
        Bundle bundle = new Bundle();
        bundle.putString(SEARCH_QUERY_URL_EXTRA, jsonResponse.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> resumeLoader = loaderManager.getLoader(SPECIFIED_LOADER);

    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
