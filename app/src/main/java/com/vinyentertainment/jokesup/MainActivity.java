package com.vinyentertainment.jokesup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.tools.jsc.Main;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;



public class MainActivity extends AppCompatActivity implements FragmentCommunicator{

    private ShareActionProvider mShareActionProvider;
    private int currentPositionOfActivity;
    private static List<Jokes> jokes;
    private MyPagerAdapter adapterViewPager;
    DbHelper dbHelper;
    String intentValue;
    private AdView mAdview;
    Bundle adContentExtras = new Bundle();
    private ConsentForm consentForm;
    private Toolbar myToolbar;
    private boolean isCrashlyticsEnabled = false;
    boolean isEEARegion = false;
    private static final String TAG = "MainActivity";

    List<Integer> markedAsShownPositions = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableOrDisableCrashlytics();

        /*
        ConsentHelper consentHelper = new ConsentHelper(this);
        consentHelper.getConsent();
        latestConsentStatus = consentHelper.getLatestConsentStatus();
        Log.i("MainActivity","Latest ConsentStatus "+latestConsentStatus);

        if(latestConsentStatus.equals(ConsentStatus.NON_PERSONALIZED))
        {
            Log.i("latestConsentStatus",String.valueOf(latestConsentStatus));
            adContentExtras.putString("npa", "1");
        }
        */

        getConsent();

        ConsentInformation consentInformation = ConsentInformation.getInstance(MainActivity.this);

        try{
            isEEARegion = ConsentInformation.getInstance(this).isRequestLocationInEeaOrUnknown();
            //Log.i(TAG,"isEEA "+String.valueOf(isEEARegion));

            if(isEEARegion && consentInformation.getConsentStatus().equals(ConsentStatus.NON_PERSONALIZED))
            {
                //Log.i(TAG,"ConsentStatus.NON_PERSONALIZED");
                adContentExtras.putString("npa","1");
            }
        }catch (Exception e)
        {
            //Log.i(TAG, "Error in EEA check to NPA ad. "+e.getMessage());
        }

        try {
            mAdview = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class,adContentExtras)
                    .addTestDevice(this.getString(R.string.hashed_device_id_for_test))
                    .build();
            mAdview.loadAd(adRequest);
        }
        catch (Exception e)
        {
            if(isCrashlyticsEnabled)
                Crashlytics.logException(e);
            //Log.i(TAG,"Error setting adView "+e.getMessage());
        }

        try {
            Intent intent = getIntent();
            this.intentValue = intent.getStringExtra("category");
            //this.intentValue = "Blond";
        }
        catch (Exception e)
        {
            //Log.i(TAG,"Error in getting Intent Category "+e.getMessage());
        }

        dbHelper = DbHelper.getInstance(this);

        jokes = dbHelper.getAllJokesInACategory(intentValue);

        invalidateOptionsMenu();
        //supportInvalidateOptionsMenu();


        try {
            //Creating and assigning the toolbar:
            myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

            String pageTitle = "Random";
            switch (intentValue) {
                case "other":
                    pageTitle = "Mixed";
                    break;
                case "oneliner":
                    pageTitle = "One Liner";
                    break;
                case "atwork":
                    pageTitle = "At Work";
                    break;
                case "relationship":
                    pageTitle = "Relationships";
                    break;
                case "pun":
                    pageTitle = "Pun";
                    break;
                case "medical":
                    pageTitle = "Medical";
                    break;
                case "nerd":
                    pageTitle = "Nerd";
                    break;
                case "school":
                    pageTitle = "School";
                    break;
                case "animal":
                    pageTitle = "Animal";
                    break;
            }
            //this.setTitle(intentValue.substring(0,1).toUpperCase()+intentValue.substring(1).toLowerCase());
            this.setTitle(pageTitle);

        }catch (Exception e)
        {
            //Log.e(TAG,"Substring Out of bound error or myToolbar initialization error "+ e.getMessage());
        }


        final ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);

        //System will caches 3 page instances on both sides of current page
        vpPager.setOffscreenPageLimit(2);

        //To get info when page changes
        vpPager.addOnPageChangeListener(mOnPageChangeListener);

        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        //vpPager.setPageTransformer(true,new CubeOutTransformer());


        if(getToastToShowSwipe()<3)
        {
            toastToShowSwipe();
            setToastToShowSwipe(getToastToShowSwipe()+1);
        }

        //Log.i(TAG,"onCreate worked");
    }

    /*
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
    */

    public void toastToShowSwipe()
    {
        Toast toast = Toast.makeText(this, R.string.toast_to_show_swipe_text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.btn_orange);
        TextView text = (TextView) view.findViewById(android.R.id.message);
/*Here you can do anything with above textview like text.setTextColor(Color.parseColor("#000000"));*/
        toast.show();

    }
    public void enableOrDisableCrashlytics()
    {
        try{
            boolean isEEARegion = ConsentInformation.getInstance(this).isRequestLocationInEeaOrUnknown();
            //Log.i(TAG,"isEEA "+String.valueOf(isEEARegion));

            if(isEEARegion)
            {
                //Crashlytics remains disabled
                Log.i(TAG,"Firebase Crashlytics remains disabled.");
            }
            else
            {
                //Enable Crashlytics, EEA checks acts weird, it may change in second check.
                //So we check it here again after Entry Activity.
                Fabric.with(this, new Crashlytics());
                isCrashlyticsEnabled = true;
                //Log.i(TAG,"Firebase Crashlytics enabled.");
            }

        }catch (Exception e)
        {
            //Log.i(TAG, "Error in EEA check to disable Crashlytics. "+e.getMessage());
        }
    }

    public void getConsent()
    {
        //ConsentInformation.getInstance(this).addTestDevice(this.getString(R.string.hashed_device_id_for_test));
        //ConsentInformation.getInstance(this).setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);

        ConsentInformation consentInformation = ConsentInformation.getInstance(this);

        String[] publisherIds = {this.getString(R.string.publisher_id)};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated.
                if (consentStatus == ConsentStatus.UNKNOWN) {
                    //Log.i("ConsentStatus",String.valueOf(consentStatus));
                    buildConsentForm();
                }
                //Log.i(TAG,"onConsentInfoUpdated worked!!");
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
                //Log.i(TAG,"onFailedToUpdateConsent worked!!");
                //Log.i(TAG,"Error in onFailedToUpdateConsentInfo" + errorDescription);
                buildConsentForm();
            }
        });
    }

    public void buildConsentForm()
    {
        URL privacyUrl = null;
        try {
            privacyUrl = new URL(getResources().getString(R.string.privacy_web_page_url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            //Log.i(TAG,"Error in buildConsentForm"+ e.getMessage());
        }

        consentForm = new ConsentForm.Builder(this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        // Consent form loaded successfully.
                        consentForm.show();
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                    }

                    @Override
                    public void onConsentFormClosed(
                            ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        // Consent form was closed.
                        //Log.i("Consent","Status" + consentStatus);
                        //When Consent Form is shown toastToShowSwipe() may be unvisible to user, so show it again.
                        toastToShowSwipe();
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        // Consent form error.
                        //Log.i(TAG,"Error in onConsentFormError" + errorDescription);
                        isEEARegion = false;
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();

        consentForm.load();
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdview != null) {
            mAdview.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdview != null) {
            mAdview.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdview != null) {
            mAdview.destroy();
        }
        super.onDestroy();
    }

    public void fragmentContactActivity(String ratingChoice)
    {
        String jokeIDtobeRated = jokes.get(currentPositionOfActivity).getID();
        String jokeCategorytobeRated = jokes.get(currentPositionOfActivity).getCategory();
        //Log.i("fragmentContact",jokeIDtobeRated);
        //Log.i("fragmentContact",jokeCategorytobeRated);

        if(ratingChoice.equals("upvoted"))
        {
            try {
                dbHelper.giveRating(jokes.get(currentPositionOfActivity).getID(),1);
                //Log.i(TAG,"rating:upvoted");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(ratingChoice.equals("downvoted"))
        {
            try {
                dbHelper.giveRating(jokes.get(currentPositionOfActivity).getID(),-1);
                //Log.i(TAG,"rating:downvoted");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(ratingChoice.equals("neutral"))
        {
            try {
                dbHelper.giveRating(jokes.get(currentPositionOfActivity).getID(),0);
                //Log.i(TAG,"rating:neutral");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            //Log.i(TAG,"Error in ratingChoice Parameter");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        try {
            // Locate MenuItem with ShareActionProvider
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);

            MenuItem shareItem = menu.findItem(R.id.menu_item_share);
            if (mShareActionProvider != null)
                mShareActionProvider.setShareIntent(jokes.get(currentPositionOfActivity).getShareIntent());
            else {
                mShareActionProvider = new ShareActionProvider(this);
                mShareActionProvider.setShareIntent(jokes.get(currentPositionOfActivity).getShareIntent());
                MenuItemCompat.setActionProvider(shareItem, mShareActionProvider);
            }

        }catch (Exception e)
        {
         //Log.i(TAG, "Error in onCreateOptionsMenu - ShareItem" + e.getMessage());
        }

        try {
            //To color favorite item red if jokeid is in favorites:
            MenuItem favoriteItem = menu.findItem(R.id.action_favorite);

            String jokeIDtobeFavorited = jokes.get(currentPositionOfActivity).getID();

            //Log.i("jokeIDtobeFavorited is:", jokeIDtobeFavorited);

            Drawable drawable = favoriteItem.getIcon();
            boolean isJokeIDExistsInFavorites = dbHelper.isJokeIDExistsInFavorites(jokeIDtobeFavorited);
            //Log.i("isJokeIDExistsInFavori:", String.valueOf(isJokeIDExistsInFavorites));

            if(isJokeIDExistsInFavorites)
            {
                if(drawable != null) {
                    drawable.mutate();
                    drawable.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                }
            }
            else
            {
                if(drawable != null) {
                    drawable.mutate();
                    drawable.setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);

                }
            }
        }catch (Exception e)
        {
            //Log.i(TAG, "Error in onCreateOptionsMenu - FavoriteItem" + e.getMessage());
        }

        //Log.i(TAG,"onCreateOptionsMenu worked");
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        //Log.i("onAttachFragment","calisti");
    }



    /**
     * A OnPageChangeListener used to update the ShareActionProvider's share intent when a new item
     * is selected in the ViewPager.
     */

    private final ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        //declare key to mock call onPageSelected for first page.
        Boolean first = true;
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            if (first && positionOffset == 0 && positionOffsetPixels == 0) {
                onPageSelected(0);
                first = false;
            }
        }



        @Override
        public void onPageSelected(int position) {
            //Log.i(TAG,"OnPage Selected:" + jokes.get(position).getTitle()+"---"+jokes.get(position).getID()+"position:"+position);
            //Important! It's used to pass current position for setActionProvider and Favorite mechanisms.
            currentPositionOfActivity = position;

            try{
                if(position!=0 && position%50==49)
                {
                    if(markedAsShownPositions!=null && markedAsShownPositions.contains(position))
                    {
                        //Log.i(TAG,"Position %50 = 49 Already in the list");
                    }
                    else
                    {
                        markedAsShownPositions.add(position);

                        //Log.i(TAG,"Position = %50 = 49 Data set Changed!!!!");
                        List<Jokes> jokes2;
                        //dbHelper = DbHelper.getInstance(MainActivity.this);

                        //Mark the last 2 entries in the jokes array as shown, so that jokes2 will not reiterate them.
                        //Because markAsShown is called at onPageSelected when that specific joke is presented to the user.
                        for(int i=1;i<2;i++)
                        {
                            dbHelper.markAsShown(jokes.get(jokes.size()-i).id);
                            //Log.i("MArked as shown:",jokes.get(jokes.size()-i).id +"---"+ jokes.get(jokes.size()-i).title);
                        }

                        jokes2 = dbHelper.getAllJokesInACategory(intentValue);
                        jokes.addAll(jokes2);
                        adapterViewPager.notifyDataSetChanged();
                    }
                }
            }catch (Exception e)
            {
                //Log.i(TAG, "Error in onPageSelected - position modulo"+ e.getMessage());
            }

            /*User read this joke. Make its mark as shown true(1 in SQLite) */
            try {
                dbHelper.markAsShown(jokes.get(position).getID());
                //Log.i("OnPage Selected:","dbHelper.markAsShown");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onPageScrollStateChanged(int state) {
            //NO-OP
        }
    };

    public int getToastToShowSwipe() {

        try {
            SharedPreferences sharedPref = this.getSharedPreferences(this.getString(R.string.toast_to_show_swipe_pref_name),Context.MODE_PRIVATE);
            int toastNumber = sharedPref.getInt(this.getString(R.string.toast_to_show_swipe_counter),2);

            //Log.i("ToastShared pref value:",String.valueOf(toastNumber));
            return toastNumber;
        } catch (Exception e)
        {
            //Log.i(TAG,e.getMessage());
        }
        //If this method fails to work in try block return default value for latestInstalledPatchNumber;
        return 3;
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_favorite:

                try {
                    String jokeIDtobeFavorited = jokes.get(currentPositionOfActivity).getID();
                    String jokeCategorytobeFavorited = jokes.get(currentPositionOfActivity).getCategory();

                    boolean isJokeIDExistsInFavorites = dbHelper.isJokeIDExistsInFavorites(jokeIDtobeFavorited);

                    if(isJokeIDExistsInFavorites)
                    {
                        dbHelper.deleteJokeFromFavorites(String.valueOf(jokeIDtobeFavorited));
                        Toast.makeText(this,"Joke is Unfavorited",Toast.LENGTH_SHORT).show();
                        invalidateOptionsMenu();
                        //supportInvalidateOptionsMenu();
                    }
                    else
                    {
                        boolean isFavoriteSuccessful = dbHelper.insertJokeIntoFavorites(String.valueOf(jokeIDtobeFavorited),jokeCategorytobeFavorited);

                        if(isFavoriteSuccessful)
                        {
                            Toast.makeText(this,"Added to Favorites",Toast.LENGTH_SHORT).show();
                            invalidateOptionsMenu();
                            //supportInvalidateOptionsMenu();
                        }
                        else
                        {
                            Toast.makeText(this,"Problem with Adding to Favorites",Toast.LENGTH_SHORT).show();
                        }
                    }

                }catch (Exception e)
                {
                    //Log.i(TAG, "Error in onOptionsItemSelected"+e.getMessage());
                }


                //Log.i(TAG,"onOptionsItemSelected worked");

                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.menu_item_share:
               // onShareAction();
            default:
                break;
        }
     return true;
    }

    public static class MyPagerAdapter extends SmartFragmentStatePagerAdapter
    {
        //private ArrayList<Jokes> jokes = new ArrayList<Jokes>();

        public MyPagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {

            int NUM_ITEMS = jokes.size();
            //Log.i("joke num in Get Count",String.valueOf(NUM_ITEMS));

            return NUM_ITEMS;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return "Page" + position;
        }

        //String title,story,category;
        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(final int position) {

            //String title,story,category;

            /*
            new Thread(new Runnable() {
                @Override
                public void run() {

                    title = jokes.get(position).getTitle();
                    story = jokes.get(position).getStory();
                    category = jokes.get(position).getCategory();


                }
            }).start();
            return FirstFragment.newInstance(position,title,story,category);

                    */

            String title,story,category;

            title = jokes.get(position).getTitle();
            story = jokes.get(position).getStory();
            //category = jokes.get(position).getCategory();

            return FirstFragment.newInstance(position,title,story);

            //DbHelper dbHelper = DbHelper.getInstance(MainActivity.this);
        }
    }
}
