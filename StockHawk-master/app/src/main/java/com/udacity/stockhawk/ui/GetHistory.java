package com.udacity.stockhawk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.udacity.stockhawk.history_datatype.Stock_DataType;
import com.udacity.stockhawk.network.ConnectingNetwork;
import com.udacity.stockhawk.network.JsonToSimple;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
/**
 * Created by kmema on 4/22/2017.
 */

public class GetHistory extends  AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{

    private static final String FirstHalf = "https://chartapi.finance.yahoo.com/instrument/1.0/";
    private static final String SecondHalf = "/chartdata;type=quote;range=1y/json";
    public static final String SEARCH_QUERY_URL_EXTRA = "query";
    private static final int SPECIFIED_LOADER = 10;
    Stock_DataType[] stockDataFromJSON;
    Context c;

    public Stock_DataType[] getHistoryData(Context context,String symbol)
    {
        c = context;

        URL jsonResponse = ConnectingNetwork.buildUrl(FirstHalf+symbol+SecondHalf);
        Bundle bundle = new Bundle();
        bundle.putString(SEARCH_QUERY_URL_EXTRA, jsonResponse.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> resumeLoader = loaderManager.getLoader(SPECIFIED_LOADER);
        if(resumeLoader == null)
        {
            loaderManager.initLoader(SPECIFIED_LOADER, bundle,this).forceLoad();
        }
        else
        {
            loaderManager.restartLoader(SPECIFIED_LOADER, bundle,this).forceLoad();
        }
    return stockDataFromJSON;
    }

    @Override
    public Loader<String> onCreateLoader(int id,final Bundle args) {

        return new AsyncTaskLoader<String>(c) {

            @Override
            protected void onStartLoading() {
                if(args == null)
                {
                    Toast.makeText(c, "Nothing in Search Query", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(c, "args present", Toast.LENGTH_SHORT).show();
                forceLoad();
                super.onStartLoading();
            }

            @Override
            public String loadInBackground() {

                String SearchQueryURLString = args.getString(SEARCH_QUERY_URL_EXTRA);
                if(SearchQueryURLString == null || TextUtils.isEmpty(SearchQueryURLString))
                {
                    return  null;
                }


                String jsonResponse;
                try {

                    URL searchURL = new URL(SearchQueryURLString);
                    final ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
                    final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    if (networkInfo != null && networkInfo.isConnected()) {
                        jsonResponse = ConnectingNetwork.getResponseFromHttpUrl(searchURL);

                        System.out.print(jsonResponse);
                        return jsonResponse;
                        //return JsonToSimple.jsonConvertString(MainActivity.this, jsonResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                    return null;
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        if (data != null) {
            //gridView(s);
            try {
                stockDataFromJSON = JsonToSimple.jsonConvertString(c,data);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
