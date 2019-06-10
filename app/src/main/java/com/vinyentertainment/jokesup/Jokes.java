package com.vinyentertainment.jokesup;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Jokes {

    private static final String TAG = "Jokes.java";

    /*
    private int mID;
    private String mTitle;
    private String mStory;
    private String mCategory;


    Jokes(int ID, String title, String story, String category) {
        this.mID = ID;
        this.mTitle = title;
        this.mStory = story;
        this.mCategory = category;
    }



    public int getID() { return mID;}

    public String getTitle() {
        return mTitle;
    }

    public String getStory() {
        return mStory;
    }

    public String getCategory() { return mCategory; }

    */

    public String id;
    public String title;
    public String story;
    public String category;
    public int isfavorited;
    public int isshown;
    public int rating;

    /*
    Jokes(String ID, String title, String story, String category,int isfavorited,int isshown, int rating) {
        this.id = ID;
        this.title = title;
        this.story = story;
        this.category = category;
        this.isfavorited = isfavorited;
        this.isshown = isshown;
        this.rating = rating;
    }
    */

    public String getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getStory() {
        return story;
    }

    public String getCategory() {
        return category;
    }

    public int getIsfavorited(){
        return isfavorited;
    }

    public int getIsshown() {
        return isshown;
    }

    public int getRating() {
        return rating;
    }

    public void setId(String ID) {
        this.id = ID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setIsfavorited(int isfavorited) {
        this.isfavorited = isfavorited;
    }

    public void setIsshown(int isshown) {
        this.isshown = isshown;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    /*
    public Intent getShareIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        String link = "\nDownload Funny Jokes App from Google Play: Link";
        // Get the string resource and bundle it as an intent extra
        intent.putExtra(Intent.EXTRA_TEXT, title+"\n"+"\n"+story+"\n"+link);
        return intent;
    }
    */

    public Intent getShareIntent() {
        try
        {
            final String appPackageName = "com.vinyentertainment.jokesup";
            final String link = "Check out the App at: https://play.google.com/store/apps/details?id=" + appPackageName;
            Intent intent = new Intent(Intent.ACTION_SEND);

            intent.setType("text/plain");
            //String link = "\nDownload Jokes Up App from Google Play: Link";
            // Get the string resource and bundle it as an intent extra
            intent.putExtra(Intent.EXTRA_TEXT, this.title+"\n"+"----------\n"+this.story+"\n\n"+link);
            return intent;

        }catch (Exception e)
        {
            //Log.i(TAG, "Error in getShareIntent method" +e.getMessage());
        }
        //Return a default intent value if try block fail to work
        return new Intent(Intent.ACTION_SEND);
    }

}
