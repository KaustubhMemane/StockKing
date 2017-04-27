package com.udacity.stockhawk.history_datatype;

/**
 * Created by kmema on 4/24/2017.
 */

public class Series_DataType {

    public int S_Date;
    public float S_Close;
    public float S_High;
    public float S_Low;
    public float S_Open;
    public int S_Volume;

    public Series_DataType(int MS_Date, float MS_Close, float MS_High, float MS_Low, float MS_Open, int MS_Volume)
    {
        S_Date = MS_Date;
        S_Close = MS_Close;
        S_High = MS_High;
        S_Low = MS_Low;
        S_Open = MS_Open;
        S_Volume = MS_Volume;
    }
}
