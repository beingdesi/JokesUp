package com.vinyentertainment.jokesup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.ads.consent.ConsentInformation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import io.fabric.sdk.android.Fabric;


public class JokeDownloader2 {

    private static final String TAG = "JokeDownloader2";

    private Long latestPatchFromFirebase;
    private File localFile = null;
    private boolean isCrashlyticsEnabled = false;

    private Context myContext;

    public JokeDownloader2(Context context) {
        myContext = context;
    }


    public void newJokePatchControl(){

        enableOrDisableCrashlytics();
        //Log.i(TAG,"Inside newJokePatchControl");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("version");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                latestPatchFromFirebase = dataSnapshot.getValue(Long.class);
                //Log.i(TAG, "latestPatchFromFirebase Value is: " + latestPatchFromFirebase);

                if(getLatestInstalledPatchNumber()<latestPatchFromFirebase)
                {
                    downloadNewJokeFromFirebase();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value from Firebase", error.toException());
            }
        });
    }

    public void enableOrDisableCrashlytics()
    {
        try{
            boolean isEEARegion = ConsentInformation.getInstance(myContext).isRequestLocationInEeaOrUnknown();
            //Log.i(TAG,"isEEA "+String.valueOf(isEEARegion));

            if(isEEARegion)
            {
                //Crashlytics remains disabled
                //Log.i(TAG,"Firebase Crashlytics remains disabled.");
            }
            else
            {
                //Enable Crashlytics, EEA checks acts weird, it may change in second check.
                //So we check it here again after Entry Activity.
                Fabric.with(myContext, new Crashlytics());
                isCrashlyticsEnabled = true;
                //Log.i(TAG,"Firebase Crashlytics enabled.");
            }

        }catch (Exception e)
        {
            //Log.i(TAG, "Error in EEA check to disable Crashlytics. "+e.getMessage());
        }
    }

    public void downloadNewJokeFromFirebase(){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        /*
        fileName to be downloaded from Firebase will be getLatestInstalledPatchNumber()+1
        because there might be many patches a user have not installed before.
        */

        String filename = String.valueOf(getLatestInstalledPatchNumber()+1+".json");
        //String filename = String.valueOf("wocka.json");

        StorageReference jokeRef = null;
        try {
            jokeRef = storageRef.child(filename);
        }catch (Exception e){
            if(isCrashlyticsEnabled)
                Crashlytics.logException(e);
            //Log.i(TAG,"Couldn't Get StorageRef From Firebase ");
        }

        try{
            localFile = File.createTempFile("jokesdownloaded",null);
            //Log.i("onSuccess","Local temp file has been created");
            //Log.i("LocalFile",localFile.getPath());
        }catch (IOException e)
        {
            if(isCrashlyticsEnabled)
                Crashlytics.logException(e);
            //Log.i(TAG,"Local file cannot be created");
        }

        try{
            jokeRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //Log.i("onSuccess","Local temp file for Firebase has been created");
                    //Log.i("LocalFile",localFile.getPath());
                    new JsonToDBAsyncTask().execute(localFile.getPath());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(isCrashlyticsEnabled)
                        Crashlytics.logException(e);
                    //Log.i("onFailure","Local temp file error");
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    //double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    //displaying percentage in progress dialog
                    //textView.setText("Downloaded " + ((int) progress) + "%...");
                }
            });
        } catch(Exception e) {
            //Log.i(TAG,"IOEXCEPTION at Firebase Download");
        }

    }
    //To Read Json from a file

    public static String JSONFileReader (String path) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        try{
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String buffer;

            while ((buffer = bufferedReader.readLine()) != null) {
                stringBuilder.append(buffer);
            }
        }catch (IOException e)
        {
            //Log.i(TAG,"JSONFileReader IOException");
        }
        return stringBuilder.toString();
    }

    private class JsonToDBAsyncTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {

            String jsonFileName = strings[0];

            try
            {
                //Log.i(TAG,"Reading from JSON Started");
                String jsonLocation = JSONFileReader(jsonFileName);
                JSONObject jsonobject = new JSONObject(jsonLocation);
                JSONArray jarray = (JSONArray) jsonobject.getJSONArray("jokes");
                for(int i=0;i<jarray.length();i++)
                {
                    JSONObject jb =(JSONObject) jarray.get(i);
                    String id = jb.getString("id");
                    String title = jb.getString("title");
                    String story = jb.getString("body");
                    String category = jb.getString("category");

                    DbHelper dbHelper = DbHelper.getInstance(myContext);
                    dbHelper.insertIntoAllJokes(id,title,story,category);
                }
                //Log.i(TAG,"Reading from JSON Finished");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            //Log.i(TAG,"JsonToDBAsyncTask onPostExecute");
            setLatestInstalledPatchNumber(getLatestInstalledPatchNumber()+1);

            super.onPostExecute(s);
        }
    }

    public void setLatestInstalledPatchNumber(int latestInstalledPatchNumber) {

        try{
            SharedPreferences sharedPref = myContext.getSharedPreferences(myContext.getString(R.string.joke_patches), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(myContext.getString(R.string.latest_installed_patch_number),latestInstalledPatchNumber);
            editor.apply();
        } catch(Exception e)
        {
            //Log.i(TAG,e.getMessage());
        }

    }

    public int getLatestInstalledPatchNumber() {

        try {
            SharedPreferences sharedPref = myContext.getSharedPreferences(myContext.getString(R.string.joke_patches),Context.MODE_PRIVATE);
            int latestInstalledPatchNumber = sharedPref.getInt(myContext.getString(R.string.latest_installed_patch_number),9999);

            //Log.i("Shared pref value:",String.valueOf(latestInstalledPatchNumber));
            return latestInstalledPatchNumber;
        } catch (Exception e)
        {
            //Log.i(TAG,e.getMessage());
        }
        //If this method fails to work in try block return default value for latestInstalledPatchNumber;
        return 100;
    }
}
