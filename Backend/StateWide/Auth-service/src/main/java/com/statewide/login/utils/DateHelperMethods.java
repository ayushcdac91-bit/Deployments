package com.statewide.login.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelperMethods {
    private static final String DATE_FORMAT_DEFAULT = "dd-MMM-yyyy HH:mm:ss";

    public static Date getDateObject(long ntime) {
        Date objDate = null;
        try {
            objDate = new Date(ntime);
        } catch (Exception e) {
            e.printStackTrace();
            objDate = null;
        }
        return objDate;
    }

    public static String getDateString(long ntime) {
        String strDate = null;
        try {
            SimpleDateFormat sf = (SimpleDateFormat) DateFormat.getInstance();
            Date objDate = getDateObject(ntime);
            sf.applyPattern(DATE_FORMAT_DEFAULT);
            strDate = sf.format(objDate);
        } catch (Exception e) {
            e.printStackTrace();
            strDate = null;
        }
        return strDate;
    }

}
