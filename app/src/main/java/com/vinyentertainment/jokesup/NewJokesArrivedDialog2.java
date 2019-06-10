package com.vinyentertainment.jokesup;

//import android.app.Dialog;
import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;


/** Code from:
 https://stackoverflow.com/questions/14514579/how-to-implement-rate-it-feature-in-android-app
 */

public class NewJokesArrivedDialog2 extends DialogFragment {
    private static final int LAUNCHES_UNTIL_PROMPT = 10;
    private static final int DAYS_UNTIL_PROMPT = 0;
    private static final int MILLIS_UNTIL_PROMPT = DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000;
    private static final String PREF_NAME = "com.vinyentertainment.jokesup.jokeDownload";
    private static final String LAST_PROMPT = "LAST_PROMPT";
    private static final String LAUNCHES = "LAUNCHES";
    private static final String DISABLED = "DISABLED";

    public static void show(Context context, FragmentManager fragmentManager) {
        boolean shouldShow = false;
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long currentTime = System.currentTimeMillis();
        long lastPromptTime = sharedPreferences.getLong(LAST_PROMPT, 0);
        if (lastPromptTime == 0) {
            lastPromptTime = currentTime;
            editor.putLong(LAST_PROMPT, lastPromptTime);
        }

        if (!sharedPreferences.getBoolean(DISABLED, false)) {
            int launches = sharedPreferences.getInt(LAUNCHES, 0) + 1;
            if (launches > LAUNCHES_UNTIL_PROMPT) {
                if (currentTime > lastPromptTime + MILLIS_UNTIL_PROMPT) {
                    shouldShow = true;
                    Log.i("NewJoke","calisti!!!");
                }
            }
            editor.putInt(LAUNCHES, launches);
        }

        if (shouldShow) {
            editor.putInt(LAUNCHES, 0).putLong(LAST_PROMPT, System.currentTimeMillis()).commit();
            new NewJokesArrivedDialog2().show(fragmentManager, null);
        } else {
            editor.commit();
        }
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, 0);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.new_joke_title)
                .setMessage(R.string.new_joke_message)
                .setPositiveButton(R.string.new_joke_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Intent intentDownload = new Intent(getContext(),JokeDownloader.class);
                        //getContext().startActivity(intentDownload);
                        //getSharedPreferences(getActivity()).edit().putBoolean(DISABLED, true).commit();
                        dismiss();
                    }
                })
                .setNeutralButton(R.string.new_joke_remind_later, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.new_joke_never, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getSharedPreferences(getActivity()).edit().putBoolean(DISABLED, true).commit();
                        dismiss();
                    }
                }).create();
    }
}
