package utilities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.diacious.mynotepad.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Akhihiero David(Yerucham) on 12/16/2017.
 */

public final class DateUtils
{
    private DateUtils(){}//Constructor private as this class is never to be instantiated

    /**
     * Method to format a date string to MMM dd, yyyy hh:mm
     * @param dateAndTime The date string to be formatted
     * @return The formated date string
     */
    public static String setDateAndTime(String dateAndTime){
        //Date Sample: Sun Dec 17 14:40:04 GMT+01:00 2017
        //             0123456789012345678901234567890123   //Note: 10 is 0, 11 is 1, 20 is 0
        //             0         10        20        30

        String date = dateAndTime.substring(4, 10) + ", " + dateAndTime.substring(30);//Dec 17, 2017
        String time = dateAndTime.substring(11, 16);//13:47

        return date + " " + time;//Dec 17, 2017 13:47
    }

    /**
     * Method takes the result of setDateAndTime
     * @param dateAndTimeFromSetDateAndTimeMethod From setDateAndTime method
     * @return The time part of dateAndTimeFromSetDateAndTimeMethod
     */
    public static String getTime(String dateAndTimeFromSetDateAndTimeMethod){
        Log.d("DateUtils.class", dateAndTimeFromSetDateAndTimeMethod);
        return dateAndTimeFromSetDateAndTimeMethod.substring(13);// 13:47
    }

    /**
     * Method to format a date oblect to a date string of format yyyy-MMM-dd HH:mm:ss
     * Had trouble saving a date object to the database
     * @param dateAndTime The date object to be formatted
     * @return The formatted date object
     */
    public static String setDateAndTimeForDatabase(Date dateAndTime){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        return formatter.format(dateAndTime);
    }

    /**
     * Method to send a time stamp for the main UI indicating the time the note was formed or last edited
     * @param dateAndTimeWhenNoteWasFormed Date string of when the note was formed or last edited
     * @return The timestamp
     */
    public static String formatNoteTime(String dateAndTimeWhenNoteWasFormed, Context context){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        Date currentDate = new Date();
        Date dateWhenNoteWasFormed;

        String noteTime;
        try
        {
            dateWhenNoteWasFormed = formatter.parse(dateAndTimeWhenNoteWasFormed);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }

        long timeDifference = (currentDate.getTime() - dateWhenNoteWasFormed.getTime()) / 1000;

        long timeDiffInMins = toMinutes(timeDifference);
        long timeDiffInHours = toHours(timeDifference);
        long timeDiffInDays = toDays(timeDifference);

        if (wasYesterday(dateWhenNoteWasFormed))
        {
            noteTime = context.getString(R.string.yesterday_at, getTime(setDateAndTime(dateWhenNoteWasFormed.toString())));
        }
        else if (timeDiffInDays > 0)
        {
            if (timeDiffInDays > 1 && timeDiffInDays <= 7)
            {
                noteTime = context.getString(R.string.days_ago, timeDiffInDays);
            }
            else
                noteTime = setDateAndTime(dateWhenNoteWasFormed.toString());
        }

        else if (timeDiffInHours > 0)
        {
            if (timeDiffInHours > 1)
                noteTime = context.getString(R.string.hours_ago, timeDiffInHours);
            else
                noteTime = context.getString(R.string.hour_ago, timeDiffInHours);
        }

        else if (timeDiffInMins > 0 )
        {
            if (timeDiffInMins > 1)
                noteTime = context.getString(R.string.minutes_ago, timeDiffInMins);
            else
                noteTime = context.getString(R.string.minute_ago, timeDiffInMins);
        }
        else
        {
            noteTime = context.getString(R.string.just_now);
        }

        return noteTime;
    }

    /**
     * Converts time in seconds to minutes
     * @param timeInSeconds
     * @return Time in minutes
     */
    private static long toMinutes(long timeInSeconds) {
        double minutes = (double)timeInSeconds / 60;

        if (minutes > 1)
            return Math.round(minutes);
        else
            return (long) minutes;
    }

    /**
     * Converts time in seconds to hours
     * @param timeInSeconds
     * @return Time in hours
     */
    private static long toHours(long timeInSeconds) {
        double hours = (double)timeInSeconds / 3600;

        if (hours > 1)
            return Math.round(hours);
        else
            return (long) hours;
    }

    /**
     * Converts time in seconds to days
     * @param timeInSeconds
     * @return Time in days
     */
    private static long toDays(long timeInSeconds) {
        double days = (double)timeInSeconds / (3600 * 24);

        if (days > 1)
            return Math.round(days);
        else
            return (long) days;
    }

    /**
     * Method to determine if the note was last saved or edited less than 24 hours from the current time
     * Also the day when note was saved and current day must differ by 1
     * @param dateWhenNoteWasSaved Date object of when the note was saved
     * @return True if time difference is less than 24 hours else false
     */
    private static boolean wasYesterday(Date dateWhenNoteWasSaved) {

        SimpleDateFormat formatter = new SimpleDateFormat("HH");
        String date = formatter.format(dateWhenNoteWasSaved);
        long hoursInDateTillMidnight = -1;

        try
        {
            hoursInDateTillMidnight = 24 - Long.parseLong(date);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        long hoursDifferenceBetweenNoteDateAndCurrentDate = toHours(new Date().getTime() / 1000) -
                toHours(dateWhenNoteWasSaved
                        .getTime() / 1000);

        if (hoursInDateTillMidnight != -1)
            return hoursDifferenceBetweenNoteDateAndCurrentDate > hoursInDateTillMidnight &&
                    hoursDifferenceBetweenNoteDateAndCurrentDate <= 24 + hoursInDateTillMidnight;
        else
            return false;
    }

    /**
     * Method to determine if current time is morning
     * @return true if it is morning else false
     */
    public static boolean isMorning()
    {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH");
        String dateString = formatter.format(date);

        return Integer.parseInt(dateString) < 12;
    }

    /**
     * Method to determine if current time is afternoon
     * @return true if it is afternoon else false
     */
    public static boolean isAfternoon()
    {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH");
        String dateString = formatter.format(date);

        return Integer.parseInt(dateString) >= 12 && Integer.parseInt(dateString) < 18;
    }

    /**
     * Method to determine if current time is evening
     * @return true if it is evening else false
     */
    public static boolean isEvening()
    {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH");
        String dateString = formatter.format(date);

        return Integer.parseInt(dateString) > 18;
    }
}
