package com.udacity.stockhawk.network;

import android.content.Context;

import com.udacity.stockhawk.history_datatype.Series_DataType;
import com.udacity.stockhawk.history_datatype.Stock_DataType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.country;
import static android.R.attr.name;

/**
 * Created by kmema on 4/22/2017.
 */

public class JsonToSimple {

    public static Stock_DataType[] jsonConvertString(Context context, String StockJsonString) throws JSONException {

        Stock_DataType[] parsedStockData = new Stock_DataType[1];
        String ticker;
        String companyName;
        String exchange;
        String unit;
        String timeStamp;
        String firstTrade;
        String lastTrade;
        String currency;
        String previousClosePrice;
        String dateMin;
        String dateMax;
        List<Series_DataType> series = new ArrayList<Series_DataType>(100);

        String databaseLang = "Database: ";
        String os = "Operating System: ";
        String videos = "Video: ";
        String programmingLang = "Programming Language: ";
        String webLang = "Web Services / Web Development: ";

        String profileImageLink;


        String newStockJsonString = null;
        int lenght = StockJsonString.length();
        newStockJsonString = StockJsonString.substring(30, lenght - 2);

        System.out.println(newStockJsonString);


        JSONObject stockDBJson = new JSONObject(newStockJsonString);
        JSONObject metaDBJson = stockDBJson.getJSONObject("meta");
        JSONObject dateDBJSON = stockDBJson.getJSONObject("Date");
        JSONObject rangesDBJSON = stockDBJson.getJSONObject("ranges");
        JSONArray seriesDBJsonArray = stockDBJson.getJSONArray("series");


        ticker = metaDBJson.getString("ticker");
        companyName = metaDBJson.getString("Company-Name");
        exchange = metaDBJson.getString("Exchange-Name");
        unit = metaDBJson.getString("unit");
        timeStamp = metaDBJson.getString("timestamp");
        firstTrade = metaDBJson.getString("first-trade");
        lastTrade = metaDBJson.getString("last-trade");
        currency = metaDBJson.getString("currency");
        previousClosePrice = metaDBJson.getString("previous_close_price");

        dateMin = dateDBJSON.getString("min");
        dateMax = dateDBJSON.getString("max");

        for (int i = 0; i < seriesDBJsonArray.length(); i++) {
            series.add(i, new Series_DataType(seriesDBJsonArray.getJSONObject(i).getInt("Date"), seriesDBJsonArray.getJSONObject(i).getInt("close"),
                    seriesDBJsonArray.getJSONObject(i).getInt("high"), seriesDBJsonArray.getJSONObject(i).getInt("low"),
                    seriesDBJsonArray.getJSONObject(i).getInt("open"), seriesDBJsonArray.getJSONObject(i).getInt("volume")));
        }

        parsedStockData[0] = new Stock_DataType(ticker, companyName, exchange, unit,
                timeStamp, firstTrade, lastTrade, currency, previousClosePrice, dateMin, dateMax, series);

        return parsedStockData;
    }
}
