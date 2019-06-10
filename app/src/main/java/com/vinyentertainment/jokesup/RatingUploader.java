package com.vinyentertainment.jokesup;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class RatingUploader {
    private static final String TAG = "RatingUploader";
    private Context myContext;

    public RatingUploader(Context context) {
        myContext = context;
    }

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String loggedInUID;
    private FirebaseUser currentUser;
    private DbHelper dbHelper;

    private static List<JokesRatings> jokesRatings;

    public void uploadRating() {
        dbHelper = DbHelper.getInstance(myContext);
        long numberOfUnsentRatings = dbHelper.numberOfUnsentRatings();

        //Log.i("RatingUploader","numberOfUnsentRatings "+numberOfUnsentRatings);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        if(isNetworkAvailable(myContext) && numberOfUnsentRatings>30)
        {
            //sendRatingsToFirebase();
            jokesRatings = dbHelper.getRatingsForFirebase();

            for(int i=0;i<jokesRatings.size();i++)
            {
                //Log.i("Joke Ratings: ",String.valueOf(jokesRatings.get(i).id) + "  "+String.valueOf(jokesRatings.get(i).rating));
            }

            mAuth.signInAnonymously()
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "signInAnonymously:success");
                                currentUser = mAuth.getCurrentUser();
                                loggedInUID = currentUser.getUid();
                                //Log.i(TAG, loggedInUID);
                                DatabaseReference pushedKey = mDatabase.child("ratings").child(loggedInUID).push();
                                //mDatabase.child("ratings").child(loggedInUID).setValue(jokesRatings);
                                pushedKey.setValue(jokesRatings);

                                dbHelper.markRatingsAsSent();
                            } else {
                                // If sign in fails, display a message to the user.
                                //Log.w(TAG, "signInAnonymously:failure", task.getException());
                                Toast.makeText(myContext, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            //Log.i("Rating Uploader","Network not available or unsent ratings <30");
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
            e.printStackTrace();
        }
        return false;
    }
}


