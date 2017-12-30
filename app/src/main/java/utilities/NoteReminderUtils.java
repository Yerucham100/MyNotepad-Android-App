package utilities;

import android.content.Context;

import android.util.Log;

import com.example.diacious.mynotepad.NoteReminderService;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;


/**
 * Created by Akhihiero David(Yerucham) on 12/20/2017.
 */

public class NoteReminderUtils
{
    private static boolean reminderIsScheduled;//Boolean to ensure that scheduleNoteReminder only runs once
    private static final String TAG = "NoteReminderUtils.class";

    private static final int REMINDER_INTERVAL_HOURS = 4;
    private static final int REMINDER_INTERVAL_SECONDS = (int)TimeUnit.HOURS.toSeconds(REMINDER_INTERVAL_HOURS);
    private static final int SYNC_FLEX_TIME_MINUTES = 15;
    private static final int SYNC_FLEX_TIME_SECONDS = (int)TimeUnit.MINUTES.toSeconds(SYNC_FLEX_TIME_MINUTES);
    private static final String JOB_TAG = "note-reminder";

    /**
     * Method to create and schedule a job that will display notifications
     * @param context Calling activity context
     */
    synchronized public static void scheduleNoteReminder(Context context)
    {
        if (reminderIsScheduled)
            return;

        Driver driver = new GooglePlayDriver(context);

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

            Job job = dispatcher.newJobBuilder()
                    .setService(NoteReminderService.class)
                    .setLifetime(Lifetime.FOREVER)
                    .setTrigger(Trigger.executionWindow(REMINDER_INTERVAL_SECONDS, REMINDER_INTERVAL_SECONDS + SYNC_FLEX_TIME_SECONDS))
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    .setReplaceCurrent(true)
                    .setRecurring(true)
                    .setTag(JOB_TAG)
                    .build();


        dispatcher.schedule(job);
        reminderIsScheduled = true;

    }
}
