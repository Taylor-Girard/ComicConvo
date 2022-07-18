package com.taylorgirard.comicconvo.tools;

import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**Utility file for all things related to converting between timezones or checking times*/

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

    public static boolean inDNDTime(ParseUser match){
        Boolean inDNDTime;
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int startDND = match.getInt("StartDND");
        int endDND = match.getInt("EndDND");
        if (startDND == 0 && endDND == 0){
            inDNDTime = false;
        } else{
            int startDNDLocal = TimeUtility.UTCtoDevice(startDND);
            int endDNDLocal = TimeUtility.UTCtoDevice(endDND);
            if (endDNDLocal > startDNDLocal){
                inDNDTime = (currentHour >= startDNDLocal && currentHour < endDNDLocal) ? true : false;
            } else{
                if (currentHour >= startDNDLocal || currentHour < endDNDLocal){
                    inDNDTime = true;
                } else{
                    inDNDTime = false;
                }
            }
        }
        return inDNDTime;
    }


}
