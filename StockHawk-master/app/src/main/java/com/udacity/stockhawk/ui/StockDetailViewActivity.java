package com.udacity.stockhawk.ui;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;


import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.*;

import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.sync.QuoteSyncJob;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockDetailViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    Intent intentThatStartTheActivity;
    String symbol=null;
    private static final int STOCK_LOADER = 0;
    String price=null;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvSymbolName) TextView tvSymbol;
    @BindView(R.id.chart)
    LineChart chart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail_view);

        ButterKnife.bind(this);

        intentThatStartTheActivity = getIntent();

        if (intentThatStartTheActivity.hasExtra("SYMBOL")) {
            symbol= intentThatStartTheActivity.getStringExtra("SYMBOL");
            //Toast.makeText(this, symbol+"+++++++++++++++++", Toast.LENGTH_SHORT).show();
            tvSymbol.setText(symbol);
        }
        else
        {
            symbol="NO SYMBOL";
            //Toast.makeText(this, symbol+"__________________", Toast.LENGTH_SHORT).show();
            tvSymbol.setText(symbol);
        }
        Cursor c = accessData(symbol);
        if (c.getCount()<=0 || c.equals(null))
        {
            Toast.makeText(this, "0+++++++++", Toast.LENGTH_SHORT).show();
        }
        else
        {

            if (c.moveToFirst()) {
                while (!c.isAfterLast()) {
                   price = c.getString(c.getColumnIndex("price"));
                    c.moveToNext();
                }
            }
            Toast.makeText(this, price, Toast.LENGTH_SHORT).show();

            getHistoryData(symbol);
        }
//        getSupportLoaderManager().initLoader(STOCK_LOADER, null);


        List<Entry> entries = new ArrayList<Entry>();
         entries.add(new Entry(3,2));
        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(3);
        dataSet.setValueTextColor(5);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
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

        if (data.getCount()<=0)
        {
            Toast.makeText(this, "0+++++++++", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, data.getCount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

public Cursor accessData(String symbol)
{
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
}
