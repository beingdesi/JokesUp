package com.vinyentertainment.jokesup;

import android.content.Context;
import android.util.Log;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;

import java.net.MalformedURLException;
import java.net.URL;

public class ConsentHelper {

    private Context myContext;
    private ConsentForm consentForm;
    private ConsentStatus latestConsentStatus;

    public ConsentHelper(Context context)
    {
        myContext = context;
    }

    public void getConsent()
    {
        ConsentInformation.getInstance(myContext).addTestDevice("A63F8EE70ADCD6142250D29D82CD249B");
        ConsentInformation.getInstance(myContext).setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);

        ConsentInformation consentInformation = ConsentInformation.getInstance(myContext);

        String[] publisherIds = {"pub-9462376850046939"};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated.
                if (consentStatus == ConsentStatus.UNKNOWN) {
                    //Log.i("ConsentHelper","ConsentHelper in ConsentHelper"+String.valueOf(consentStatus));
                    latestConsentStatus = consentStatus;
                    buildConsentForm();
                }
                //Log.i("ConsentHelper","ConsentStatus in CHel "+String.valueOf(consentStatus));
                //Log.i("ConsentHelper","onConsentInfoUpda inCHe"+"calisti!!");
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
                //Log.i("onFailedToUpdConsen CH","worked!!");
                //Log.i("ERROR",errorDescription);
            }
        });
    }

    public ConsentStatus getLatestConsentStatus()
    {
        getConsent();
        return latestConsentStatus;
    }

    public void buildConsentForm()
    {
        URL privacyUrl = null;
        try {
            // TODO: Replace with your app's privacy policy URL.
            privacyUrl = new URL(myContext.getString(R.string.privacy_web_page_url));
        } catch (MalformedURLException e) {
            //e.printStackTrace();
            // Handle error.
        }


        consentForm = new ConsentForm.Builder(myContext, privacyUrl)
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
                        //Log.i("Error",errorDescription);
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();

        consentForm.load();
    }
}