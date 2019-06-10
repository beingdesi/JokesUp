package com.vinyentertainment.jokesup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;

import java.net.MalformedURLException;
import java.net.URL;

public class SettingsActivity extends AppCompatActivity {
    ListView settingsListView;
    private ConsentForm consentForm;
    private static final String TAG = "SettingsActivity";
    boolean isEEARegion;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        try{
            Toolbar toolbar = (Toolbar)findViewById(R.id.settings_activity_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            this.setTitle(getResources().getString(R.string.settings));
        }catch (Exception e)
        {
            //Log.i(TAG, "Error in Toolbar initialization " +e.getMessage());
        }
        try{
            isEEARegion = ConsentInformation.getInstance(this).isRequestLocationInEeaOrUnknown();
            //Log.i(TAG,"isEEA "+String.valueOf(isEEARegion));

            final String[] settingValues1;

                settingValues1 = new String[] {
                        getResources().getString(R.string.settings_about),
                        getResources().getString(R.string.settings_privacy_policy),
                        getResources().getString(R.string.settings_terms_and_conditions_web_page_title),
                        getResources().getString(R.string.settings_rate_the_app),
                        getResources().getString(R.string.settings_send_feedback),
                        getResources().getString(R.string.settings_gdpr_user_consent),
                };

            String versionName = BuildConfig.VERSION_NAME;

            final String[] settingValues2 = new String[] {
                    "Jokes up - Version: "+versionName,
                    getResources().getString(R.string.settings_privacy_policy_bottom),
                    getResources().getString(R.string.settings_terms_and_conditions),
                    getResources().getString(R.string.settings_rate_the_app_bottom),
                    getResources().getString(R.string.settings_send_feedback_bottom),
                    getResources().getString(R.string.settings_gdpr_user_consent_bottom),
            };


            /*
            //Add title to the toolbar
            this.setTitle(getResources().getString(R.string.settings));

            settingsListView = (ListView) findViewById(R.id.settings_listview);

            // Define a new Adapter
            // First parameter - Context
            // Second parameter - Layout for the row
            // Third parameter - ID of the TextView to which the data is written
            // Forth - the Array of data

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,settingValues1);

            // Assign adapter to ListView
            settingsListView.setAdapter(adapter);

            settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    if(position==0) // Privacy Policy
                    {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.privacy_web_page_url))));

                    }
                    else if (position==1) //GDPR Setting
                    {
                        getConsent();
                        // Show Alert
                        Toast.makeText(getApplicationContext(),
                                "Preparing Consent Form Please Wait" , Toast.LENGTH_SHORT)
                                .show();

                    }
                }
            });
            */




            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, android.R.id.text1, settingValues1) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                    text1.setText(settingValues1[position]);
                    text2.setText(settingValues2[position]);
                    text2.setTextColor(getResources().getColor(R.color.grey_40));
                    return view;
                }

                //IF user is not in EEA, GDPR setting will be rendered as non clickable.
                /*
                public boolean isEnabled(int position) {
                    if(isEEARegion==false && position == 5) {
                        return false;
                    }
                    return true;
                }
                */

            };

            settingsListView = (ListView) findViewById(R.id.settings_listview);
            settingsListView.setAdapter(adapter);

            settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    switch (position){
                        case 0: //About
                            break;

                        /*
                        case 1: // Other Apps
                            try {
                                Intent otherAppsIntent = new Intent(Intent.ACTION_VIEW);
                                otherAppsIntent.setData(Uri.parse(getResources().getString(R.string.settings_other_apps_url)));
                                startActivity(otherAppsIntent);
                            }catch (Exception e)
                            {
                                Log.i(TAG,"Error in Other Apps "+e.getMessage());
                            }
                            break;
                            */

                        case 1: // Privacy Policy
                            try{
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.privacy_web_page_url))));
                            }catch (Exception e)
                            {
                                //Log.i(TAG,"Error in Privacy Policy "+e.getMessage());
                            }
                            break;

                        case 2: // Terms and Conditions
                            try{
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.terms_and_conditions_web_page_url))));
                            }catch (Exception e)
                            {
                                //Log.i(TAG,"Error in Terms and Conditions "+e.getMessage());
                            }
                            break;

                        case 3: // Rate the App
                            try{
                                Intent rateTheAppIntent = new Intent(Intent.ACTION_VIEW);
                                rateTheAppIntent.setData(Uri.parse(getResources().getString(R.string.settings_rate_the_app_url)));
                                startActivity(rateTheAppIntent);
                            }catch (Exception e)
                            {
                                //Log.i(TAG,"Error in Rate the App "+e.getMessage());
                            }
                            break;

                        case 4: //Send Feedback
                            try{
                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                        "mailto", getResources().getString(R.string.settings_send_feedback_email), null));
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.settings_send_feedback_subject));
                                startActivity(Intent.createChooser(emailIntent, "Choose an email client"));
                            }
                            catch(Exception e)
                            {
                                //Log.i(TAG,"Error in Send Feedback "+e.getMessage());
                            }
                            break;

                        case 5: // GDPR Consent Form
                            try{
                                if(isEEARegion)
                                {
                                    getConsent();
                                    // Show Alert
                                    Toast.makeText(getApplicationContext(),
                                            "Preparing Consent Form Please Wait" , Toast.LENGTH_SHORT)
                                            .show();
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),
                                            "This feature is only applicable to EEA Residents" , Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }catch (Exception e)
                            {
                                //Log.i(TAG,"Error in GDPR Consent Form switch "+e.getMessage());
                            }
                            break;
                    }
                }
            });

        }catch (Exception e)
        {
        //Log.i(TAG, "Error in Settings Listview creation. "+e.getMessage());
        }
    }

    public void getConsent()
    {
        try{
            //ConsentInformation.getInstance(this).addTestDevice(this.getString(R.string.hashed_device_id_for_test));
            //ConsentInformation.getInstance(this).setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);

            String[] publisherIds = {this.getString(R.string.publisher_id)};
            ConsentInformation consentInformation = ConsentInformation.getInstance(this);
            consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
                @Override
                public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                    // User's consent status successfully updated.
                /*
                if (consentStatus == ConsentStatus.UNKNOWN) {
                    Log.i("ConsentStatus",String.valueOf(consentStatus));
                    buildConsentForm();
                }
                */
                    //Log.i(TAG,"onConsentInfoUpdated worked!!");
                    //Log.i(TAG,"ConsentStatus is "+String.valueOf(consentStatus));
                    buildConsentForm();
                }

                @Override
                public void onFailedToUpdateConsentInfo(String errorDescription) {
                    // User's consent status failed to update.
                    //Log.i(TAG,"onFailedToUpdateConse worked!!");
                    //Log.i(TAG,"Error " + errorDescription);
                    buildConsentForm();
                }
            });

        } catch (Exception e)
        {
            //Log.i(TAG, "Error in getConsent method. "+e.getMessage());
        }
    }

    public void buildConsentForm()
    {
        URL privacyUrl = null;
        try {
            privacyUrl = new URL(getResources().getString(R.string.privacy_web_page_url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            //Log.i(TAG, "Error in buildConsentForm method. "+e.getMessage());
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
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        // Consent form error.
                        //Log.i(TAG,"Error in onConsentFormError " + errorDescription);
                        isEEARegion = false;
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();

        consentForm.load();
    }
}
