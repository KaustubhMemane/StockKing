package com.udacity.stockhawk.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.*;

import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.history_datatype.Stock_DataType;
import com.udacity.stockhawk.network.ConnectingNetwork;
import com.udacity.stockhawk.network.JsonToSimple;
import com.udacity.stockhawk.sync.QuoteSyncJob;


import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.color;
import static android.R.attr.enabled;
import static android.R.attr.entries;


public class StockDetailViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String FirstHalf = "https://chartapi.finance.yahoo.com/instrument/1.0/";
    private static final String SecondHalf = "/chartdata;type=quote;range=1y/json";

    Intent intentThatStartTheActivity;
    String symbol = null;
    private static final int STOCK_LOADER = 0;
    String price = null;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvSymbolName)
    TextView tvSymbol;

    @BindView(R.id.chart)
    LineChart chart;

    @BindView(R.id.chartYear)
    LineChart chartYear;
    @BindView(R.id.companyName)
    TextView companyName;
    @BindView(R.id.ExchangeName)
    TextView exchangeName;
    @BindView(R.id.unitName)
    TextView unit;
    @BindView(R.id.firstTrade)
    TextView firstTrade;
    @BindView(R.id.lastTrade)
    TextView lastTrade;
    @BindView(R.id.Currency)
    TextView currency;
    @BindView(R.id.PreviousClosed)
    TextView previousClosingDate;


    Stock_DataType[] stockHistoryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail_view);

        ButterKnife.bind(this);
        intentThatStartTheActivity = getIntent();
        if (intentThatStartTheActivity.hasExtra("SYMBOL")) {
            symbol = intentThatStartTheActivity.getStringExtra("SYMBOL");

            tvSymbol.setText(symbol);
        } else {
            symbol = "NO SYMBOL";
            tvSymbol.setText(symbol);
        }

        Cursor c = accessData(symbol);
        if (c.getCount() <= 0 || c.equals(null)) {
            Toast.makeText(this, "0+++++++++", Toast.LENGTH_SHORT).show();
        } else {
            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    price = c.getString(c.getColumnIndex("price"));
                    c.moveToNext();
                }
            }
            Toast.makeText(this, price, Toast.LENGTH_SHORT).show();
            Context context = StockDetailViewActivity.this.getApplicationContext();
            URL jsonResponse = ConnectingNetwork.buildUrl(FirstHalf + symbol + SecondHalf);
            new getHistory().execute(jsonResponse);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

        Cursor cursor =
                sqLiteDatabase.query(
                        Contract.Quote.TABLE_NAME,
                        new String[]{
                                Contract.Quote.COLUMN_SYMBOL,
                                Contract.Quote.COLUMN_PRICE,
                                Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
                                Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
                                Contract.Quote.COLUMN_HISTORY},
                        "symbol = ?",
                        new String[]{symbol},
                        null,
                        null,
                        null);
        if (cursor != null && cursor.getCount() > 0) {
            return (Loader<Cursor>) cursor;
        }
        Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show();
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.getCount() <= 0) {
            Toast.makeText(this, "0+++++++++", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, data.getCount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public Cursor accessData(String symbol) {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

        Cursor cursor =
                sqLiteDatabase.query(
                        Contract.Quote.TABLE_NAME,
                        new String[]{
                                Contract.Quote.COLUMN_SYMBOL,
                                Contract.Quote.COLUMN_PRICE,
                                Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
                                Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
                                Contract.Quote.COLUMN_HISTORY},
                        "symbol = ?",
                        new String[]{symbol},
                        null,
                        null,
                        null);
        if (cursor != null && cursor.getCount() > 0) {
            return cursor;
        }
        Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show();
        return null;
    }


    public class getHistory extends AsyncTask<URL, Stock_DataType[], Stock_DataType[]> {

        Stock_DataType[] forReturnPurpose = null;
        Context context = StockDetailViewActivity.this;

        @Override
        protected Stock_DataType[] doInBackground(URL... params) {
            URL searchURL = null;

            if (params[0] != null) {
                searchURL = params[0];
                System.out.println(searchURL);
            } else {
                return null;
            }

            String jsonResponse;
            try {

                final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    jsonResponse = ConnectingNetwork.getResponseFromHttpUrl(searchURL);

                    return JsonToSimple.jsonConvertString(context, jsonResponse);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Stock_DataType[] stock_dataType) {
            super.onPostExecute(stock_dataType);

            firstGraph(stock_dataType);
            secondGraph(stock_dataType);

            companyName.setText(stock_dataType[0].companyName);
            exchangeName.setText(stock_dataType[0].exchangeName);
            unit.setText(stock_dataType[0].unit);
            firstTrade.setText(stock_dataType[0].firstTrade);
            lastTrade.setText(stock_dataType[0].lastTrade);
            currency.setText(stock_dataType[0].currency);
            previousClosingDate.setText(stock_dataType[0].previousClosePrice);
        }
    }

    public void firstGraph(Stock_DataType[] stock_dataType) {
        System.out.println(stock_dataType[0].unit);
        List<Entry> entriesHigh = new ArrayList<Entry>();
        List<Entry> entriesClose = new ArrayList<Entry>();

        int start = (stock_dataType[0].series.size() - 30);
        if (start < 0) {
            start = 0;
        }
        for (int i = start; i < stock_dataType[0].series.size(); i++) {
            int x = stock_dataType[0].series.get(i).S_Date;
            float y_High = stock_dataType[0].series.get(i).S_High;
            float y_Close = stock_dataType[0].series.get(i).S_Close;

            entriesHigh.add(new Entry(x, y_High));
            entriesClose.add(new Entry(x, y_Close));
        }


        LineDataSet dataSet1 = new LineDataSet(entriesHigh, "Highest"); // add entries to dataset
        dataSet1.setColor(Color.GREEN);
        dataSet1.setDrawCircles(true);
        dataSet1.setCircleColor(Color.GREEN);
        dataSet1.setCircleColorHole(Color.BLACK);
        dataSet1.setDrawValues(true);
        dataSet1.setValueTextColor(Color.BLACK);

        LineDataSet dataSet2 = new LineDataSet(entriesClose, "Close"); // add entries to dataset
        dataSet2.setColor(Color.RED);
        dataSet2.setDrawCircles(true);
        dataSet2.setCircleColor(Color.RED);
        dataSet2.setCircleColorHole(Color.BLUE);
        dataSet2.setDrawValues(true);
        dataSet2.setValueTextColor(Color.BLUE);

        ArrayList<ILineDataSet> graphLineDataSets = new ArrayList<>();
        graphLineDataSets.add(dataSet1);
        graphLineDataSets.add(dataSet2);

        chart.valuesToHighlight();
        chart.setData(new LineData(graphLineDataSets));
        chart.invalidate();
        chart.getLineData();
        chart.setHighlightPerTapEnabled(true);
        chart.setNoDataTextColor(Color.RED);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setAvoidFirstLastClipping(true);
        chart.setVisibleXRangeMaximum(20);

        Description description = new Description();
        description.setText("Stock: " + stock_dataType[0].companyName);
        description.setTextSize(20);
        chart.setDescription(description);
    }

    public void secondGraph(Stock_DataType[] stock_dataType) {
        System.out.println(stock_dataType[0].unit);
        List<Entry> entriesHigh = new ArrayList<Entry>();
        List<Entry> entriesClose = new ArrayList<Entry>();

        int start = (stock_dataType[0].series.size() / 2);
        if (start < 0) {
            start = 0;
        }
        for (int i = start; i < stock_dataType[0].series.size(); i++) {
            int x = stock_dataType[0].series.get(i).S_Date;
            float y_High = stock_dataType[0].series.get(i).S_High;
            float y_Close = stock_dataType[0].series.get(i).S_Close;

            entriesHigh.add(new Entry(x, y_High));
            entriesClose.add(new Entry(x, y_Close));
        }


        LineDataSet dataSet1 = new LineDataSet(entriesHigh, "Highest"); // add entries to dataset
        dataSet1.setColor(Color.GREEN);
        dataSet1.setDrawCircles(true);
        dataSet1.setCircleColor(Color.GREEN);
        dataSet1.setCircleColorHole(Color.BLACK);
        dataSet1.setDrawValues(true);
        dataSet1.setValueTextColor(Color.BLACK);

        LineDataSet dataSet2 = new LineDataSet(entriesClose, "Close"); // add entries to dataset
        dataSet2.setColor(Color.RED);
        dataSet2.setDrawCircles(true);
        dataSet2.setCircleColor(Color.RED);
        dataSet2.setCircleColorHole(Color.BLUE);
        dataSet2.setDrawValues(true);
        dataSet2.setValueTextColor(Color.BLUE);

        ArrayList<ILineDataSet> graphLineDataSets = new ArrayList<>();
        graphLineDataSets.add(dataSet1);
        graphLineDataSets.add(dataSet2);

        chartYear.valuesToHighlight();
        chartYear.setData(new LineData(graphLineDataSets));
        chartYear.invalidate();
        chartYear.getLineData();
        chartYear.setHighlightPerTapEnabled(true);
        chartYear.setNoDataTextColor(Color.RED);
        chartYear.getXAxis().setDrawGridLines(false);
        chartYear.getXAxis().setAvoidFirstLastClipping(true);
        chartYear.setVisibleXRangeMaximum(50);

        Description description = new Description();
        description.setText("Yearly Stock: " + stock_dataType[0].companyName);
        description.setTextSize(20);
        chartYear.setDescription(description);
    }
}
