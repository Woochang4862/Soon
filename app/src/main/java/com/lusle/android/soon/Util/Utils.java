package com.lusle.android.soon.Util;

import java.util.Calendar;

public class Utils {

    public static int calDDay(Calendar calendar) {
        try {
            Calendar today = Calendar.getInstance();
            Calendar dday = calendar;
            long day = dday.getTimeInMillis() / 86400000;
            long tday = today.getTimeInMillis() / 86400000;
            long count = day - tday;
            return (int) count + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
