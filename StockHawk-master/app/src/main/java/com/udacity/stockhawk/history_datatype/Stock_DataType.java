package com.udacity.stockhawk.history_datatype;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmema on 4/22/2017.
 */

public class Stock_DataType {
    public String ticker;
    public String companyName;
    public String exchangeName;
    public String unit;
    public String timeStamp;
    public String firstTrade;
    public String lastTrade;
    public String currency;
    public String previousClosePrice;
    public String dateMin;
    public String dateMax;
    List<String> series;

    public Stock_DataType(String Mticker, String MCompanyName, String MExchange, String MUnit,
                          String MTimeStamp, String MFirstTrade, String MLastTrade, String MCurrency,
                          String MPreviousClosePrice, String MDateMin, String MDateMax, List<String> MSeries)
    {
        this.ticker = Mticker;
        this.companyName = MCompanyName;
        this.exchangeName = MExchange;
        this.unit = MUnit;
        this.timeStamp = MTimeStamp;
        this.firstTrade = MFirstTrade;
        this.lastTrade = MLastTrade;
        this.currency = MCurrency;
        this.previousClosePrice = MPreviousClosePrice;
        this.dateMin = MDateMin;
        this.dateMax = MDateMax;
        this.series = MSeries;
    }
}
