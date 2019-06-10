package com.vinyentertainment.jokesup;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class FavoriteJokesSingleView extends AppCompatActivity {

    private ShareActionProvider mShareActionProvider;

    private String ID;
    private String title;
    private String story;
    private String category;
    private static final String TAG = "FavoriteJokesSingleView";

    DbHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_jokes_single_view);

        try{
            Intent intent = getIntent();
            this.title = intent.getStringExtra("title");
            this.story = intent.getStringExtra("story");
            this.category = intent.getStringExtra("category");
            this.ID = intent.getStringExtra("id");

            TextView favoriteJokeTitle = (TextView) findViewById(R.id.favoriteJokeSingleViewTitle);
            favoriteJokeTitle.setText(title);

            TextView favoriteJokeStory = (TextView) findViewById(R.id.favoriteJokeSingleViewStory);
            favoriteJokeStory.setText(story);

        } catch (Exception e)
        {
            //Log.i(TAG,"Error in Content Creation");
        }

        try {
            Toolbar myToolbar = (Toolbar) findViewById(R.id.favorite_jokes_singleview_activity_toolbar);
            setSupportActionBar(myToolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
            //getSupportActionBar().setDisplayShowTitleEnabled(false);

            this.setTitle("Favorited:");

        }catch (Exception e)
        {
            //Log.i(TAG, "Error in Toolbar setting first time");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater;
        MenuItem shareItem = null;
        MenuItem favoriteItem = null;

        try
        {
            // Locate MenuItem with ShareActionProvider
            inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);

            shareItem = menu.findItem(R.id.menu_item_share);

            if (mShareActionProvider != null)
                mShareActionProvider.setShareIntent(getShareIntent());
            else {
                mShareActionProvider = new ShareActionProvider(this);
                mShareActionProvider.setShareIntent(getShareIntent());
                MenuItemCompat.setActionProvider(shareItem, mShareActionProvider);
            }

            favoriteItem = menu.findItem(R.id.action_favorite);

        }catch (Exception e)
        {
            //Log.i(TAG,"Error in Menu Item Inflator");
        }


        try{
            String jokeIDtobeFavorited = this.ID;
            //Log.i("jokeIDtobeFavorited is:", jokeIDtobeFavorited);

            Drawable drawable = favoriteItem.getIcon();
            dbHelper = DbHelper.getInstance(this);
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
            //Log.i(TAG,"Error in favorite drawable");
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_favorite:

                try{
                    String jokeIDtobeFavorited = this.ID;
                    String jokeCategorytobeFavorited = this.category;

                    dbHelper = DbHelper.getInstance(this);

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

                    //Log.i("onOptionsItemSelected","calisti");

                }catch (Exception e)
                {
                    //Log.i(TAG,"Error in action_favorite click");
                }
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


    private Intent getShareIntent() {

        try
        {
            final String appPackageName = this.getPackageName();
            final String link = "Check out the App at: https://play.google.com/store/apps/details?id=" + appPackageName;
            Intent intent = new Intent(Intent.ACTION_SEND);

            intent.setType("text/plain");
            //String link = "\nDownload Jokes Up App from Google Play: Link";
            // Get the string resource and bundle it as an intent extra
            intent.putExtra(Intent.EXTRA_TEXT, this.title+"\n\n"+this.story+"\n\n"+link);
            return intent;

        }catch (Exception e)
        {
            //Log.i(TAG, "Error in getShareIntent method");
        }
        //Return a default intent value if try block fail to work
        return new Intent(Intent.ACTION_SEND);
    }
}
