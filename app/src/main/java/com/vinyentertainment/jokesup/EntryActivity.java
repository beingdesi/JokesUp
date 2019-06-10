package com.vinyentertainment.jokesup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import io.fabric.sdk.android.Fabric;


public class EntryActivity extends AppCompatActivity implements View.OnClickListener {

    private String originalJokeFileName = "myJsonFile.json";
    private static final String TAG = "EntryActivity";
    private static boolean isCrashlyticsEnabled = false;

    /*
     Added -> android:configChanges="orientation|keyboardHidden|screenSize"
     to AndroidManifest.xml, in those cases onCreate method will not be called
     again.
     */
    boolean isEEARegion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        //Stetho.initializeWithDefaults(this);



        try{
            isEEARegion = ConsentInformation.getInstance(this).isRequestLocationInEeaOrUnknown();
            //Log.i(TAG,"isEEA "+String.valueOf(isEEARegion));

            if(isEEARegion)
            {
                //Crashlytics remains disabled
                //Log.i(TAG,"Firebase Crashlytics remains disabled.");
            }
            else
            {
                //Enable Crashlytics
                Fabric.with(this, new Crashlytics());
                isCrashlyticsEnabled = true;
                //Log.i(TAG,"Firebase Crashlytics enabled.");
            }

        }catch (Exception e)
        {
            //Log.i(TAG, "Error in EEA check to disable Crashlytics. "+e.getMessage());
        }

            setContentView(R.layout.entry_screen);

        Button buttonGoRandom = (Button) findViewById(R.id.button_gorandom);
        //LinearLayout categoryMoney = (LinearLayout) findViewById(R.id.category_money);
        LinearLayout categoryMixed = (LinearLayout) findViewById(R.id.category_mixed);
        LinearLayout categoryPun = (LinearLayout) findViewById(R.id.category_pun);
        LinearLayout categoryOneLiner = (LinearLayout) findViewById(R.id.category_oneliner);
        LinearLayout categoryMedical = (LinearLayout) findViewById(R.id.category_medical);
        LinearLayout categoryNerd = (LinearLayout) findViewById(R.id.category_nerd);
        LinearLayout categorySchool = (LinearLayout) findViewById(R.id.category_school);
        //LinearLayout categorySports = (LinearLayout) findViewById(R.id.category_sports);
        LinearLayout categoryRelations = (LinearLayout) findViewById(R.id.category_relations);
        LinearLayout categoryAnimal = (LinearLayout) findViewById(R.id.category_animal);
        LinearLayout categoryAtWork = (LinearLayout) findViewById(R.id.category_atwork);


        Button buttonMyFavorites = (Button) findViewById(R.id.button_myfavorites);
        Button buttonSetting = (Button) findViewById(R.id.button_settings);


        buttonGoRandom.setOnClickListener(this);
        buttonMyFavorites.setOnClickListener(this);
        buttonSetting.setOnClickListener(this);


        //categoryMoney.setOnClickListener(this);
        categoryMixed.setOnClickListener(this);
        categoryPun.setOnClickListener(this);
        categoryOneLiner.setOnClickListener(this);
        categoryMedical.setOnClickListener(this);
        categoryNerd.setOnClickListener(this);
        categorySchool.setOnClickListener(this);
        //categorySports.setOnClickListener(this);
        categoryRelations.setOnClickListener(this);
        categoryAnimal.setOnClickListener(this);
        categoryAtWork.setOnClickListener(this);

        /* Make it asyncronous*/
        //fromJSONToDB();
        DbHelper dbHelper = DbHelper.getInstance(this);

        if(dbHelper!= null && dbHelper.getNumberOfAllJokes()==0)
        {
            //Means that SQLite is empty. Inflate it with available JSON from asset folder.
            new JsonToDBAsyncTask().execute(originalJokeFileName);

            //Set these two values when this if statement works i.e. first time run
            setLatestInstalledPatchNumber(100);
            setToastToShowSwipe(0);
        }

        if(isNetworkAvailable(this))
        {
            //Log.i("Entry: Network","available");

            if(Math.random() < 0.5) {
                //Log.i("Entry: Random","RatingUploader");

                RatingUploader jokeRatingUploader = new RatingUploader(this);
                jokeRatingUploader.uploadRating();
            }
            else
            {
                //Log.i("Entry: Random","JokeDownloader2");

                JokeDownloader2 jd2 = new JokeDownloader2(this);
                jd2.newJokePatchControl();
            }
        }
        else
        {
            //Log.i("Entry: Network","not available");
        }
    }

    public static String AssetJSONFile (String filename, Context context) throws IOException {

        try{
            AssetManager manager = context.getAssets();
            InputStream file = manager.open(filename);
            byte[] formArray = new byte[file.available()];
            file.read(formArray);
            file.close();
            return new String(formArray);
        }catch(IOException e)
        {
            if(isCrashlyticsEnabled)
                Crashlytics.logException(e);
            //Log.i(TAG, "Error!"+  e.getMessage());
        }
        return "Error Occurred in AssetJSONFile";
    }

    public void setLatestInstalledPatchNumber(int latestInstalledPatchNumber) {
        try {
            SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.joke_patches), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.latest_installed_patch_number), latestInstalledPatchNumber);
            editor.apply();
        }  catch(Exception e)
        {
            if(isCrashlyticsEnabled)
                Crashlytics.logException(e);
            //Log.i(TAG, "Error!"+e.getMessage());
        }
    }

    public void setToastToShowSwipe(int toastNumber) {
        try {
            SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.toast_to_show_swipe_pref_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.toast_to_show_swipe_counter), toastNumber);
            editor.apply();
        }  catch(Exception e)
        {
            //Log.i(TAG, "Error!"+e.getMessage());
        }
    }

    public static boolean isNetworkAvailable(Context con) {
        try {
            ConnectivityManager cm = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            //Log.i(TAG,e.getMessage());
        }
        return false;
    }


    private class JsonToDBAsyncTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {

            String jsonFileName = strings[0];

            try
            {
                //Log.i(TAG,"Reading Started");
                String jsonLocation = AssetJSONFile(jsonFileName, EntryActivity.this);
                JSONObject jsonobject = new JSONObject(jsonLocation);
                JSONArray jarray = (JSONArray) jsonobject.getJSONArray("jokes");
                for(int i=0;i<jarray.length();i++)
                {
                    JSONObject jb =(JSONObject) jarray.get(i);
                    String id = jb.getString("id");
                    String title = jb.getString("title");
                    String story = jb.getString("story");
                    String category = jb.getString("category");

                    /*
                    Add only selected category to jokes array. Otherwise
                    currentViewPagerItemofActivity and jokes.get(currentViewPagerItemofActivity)
                    will give different/wrong joke elements for favorites.
                    */
                    DbHelper dbHelper = DbHelper.getInstance(EntryActivity.this);
                    dbHelper.insertIntoAllJokes(id,title,story,category);
                }
                //Log.i(TAG,"Reading Finished");
            } catch (IOException e) {
                if(isCrashlyticsEnabled)
                    Crashlytics.logException(e);
                //e.printStackTrace();
                //Log.i(TAG,"Error!"+ e.getMessage());
            } catch (JSONException e) {
                if(isCrashlyticsEnabled)
                    Crashlytics.logException(e);
                //e.printStackTrace();
                //Log.i(TAG,"Error!"+e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }



    @Override
    public void onClick(View view) {
        Intent intentChosen = new Intent(this,MainActivity.class);
        Intent favoriteJokesActivity = new Intent(this,FavoriteJokesActivity.class);
        switch (view.getId()) {
            case R.id.button_gorandom:
                intentChosen.putExtra("category","random");
                //intentChosen.putExtra("jokeslist",(Serializable) jokes);
                startActivity(intentChosen);
                break;
            case R.id.button_myfavorites:
                startActivity(favoriteJokesActivity);
                break;
            case R.id.button_settings:
                Intent intentSettings = new Intent(this,SettingsActivity.class);
                startActivity(intentSettings);
                break;
            case R.id.category_mixed:
                intentChosen.putExtra("category","other");
                startActivity(intentChosen);
                break;
            case R.id.category_pun:
                intentChosen.putExtra("category","pun");
                startActivity(intentChosen);
                break;
            case R.id.category_oneliner:
                intentChosen.putExtra("category","oneliner");
                startActivity(intentChosen);
                break;
            case R.id.category_medical:
                intentChosen.putExtra("category","medical");
                startActivity(intentChosen);
                break;
            case R.id.category_nerd:
                intentChosen.putExtra("category","nerd");
                startActivity(intentChosen);
                break;
            case R.id.category_school:
                intentChosen.putExtra("category","school");
                startActivity(intentChosen);
                break;
            case R.id.category_relations:
                intentChosen.putExtra("category","relationship");
                startActivity(intentChosen);
                break;
            case R.id.category_animal:
                intentChosen.putExtra("category","animal");
                startActivity(intentChosen);
                break;
            case R.id.category_atwork:
                intentChosen.putExtra("category","atwork");
                startActivity(intentChosen);
                break;
            default:
                break;
        }
    }



}
