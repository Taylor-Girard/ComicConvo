package com.taylorgirard.comicconvo.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtility {

    public static int deviceToUTC(int time){
        DateFormat deviceFormat = new SimpleDateFormat("HH:mm");;
        TimeZone timeZone = TimeZone.getDefault();
        deviceFormat.setTimeZone(timeZone);

        Date thisTime = new Date();

        try {
            thisTime = deviceFormat.parse(String.valueOf(time) + ":00");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat utcFormat = new SimpleDateFormat("HH:mm");
        utcFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        String timeUTC = utcFormat.format(thisTime);
        int intTime = Integer.parseInt(timeUTC.substring(0, 2));

        return intTime;

    }

    public static int UTCtoDevice(int time){
        DateFormat deviceFormat = new SimpleDateFormat("HH:mm");;
        TimeZone timeZone = TimeZone.getDefault();
        deviceFormat.setTimeZone(timeZone);

        DateFormat utcFormat = new SimpleDateFormat("HH:mm");
        utcFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        Date thisTime = new Date();

        try {
            thisTime = utcFormat.parse(String.valueOf(time) + ":00");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String timeUTC = deviceFormat.format(thisTime);
        int intTime = Integer.parseInt(timeUTC.substring(0, 2));

        return intTime;
    }


}
