package com.vinyentertainment.jokesup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class FavoriteJokesActivity extends AppCompatActivity {

    /*
    Added -> android:configChanges="orientation|keyboardHidden|screenSize"
    to AndroidManifest.xml, in those cases onCreate method will not be called
    again.
    */

    DbHelper dbHelper;
    List<Jokes> jokes;
    private static final String TAG = "FavoriteJokesActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_activity);

        try
        {
            dbHelper = DbHelper.getInstance(this);
            jokes = dbHelper.getAllJokesFromFavorites();

            FavoriteJokesAdapter favoriteJokesAdapter = new FavoriteJokesAdapter(this,jokes);

            ListView listView = (ListView) findViewById(R.id.favorite_jokes);
            listView.setAdapter(favoriteJokesAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(),FavoriteJokesSingleView.class);
                    intent.putExtra("title",jokes.get(position).title);
                    intent.putExtra("story",jokes.get(position).story);
                    intent.putExtra("category",jokes.get(position).category);
                    intent.putExtra("id",jokes.get(position).id);
                    startActivity(intent);
                }
            });
        } catch (Exception e)
        {
            //Log.i(TAG,"Dbhelper getAllJokesFromFavorites error");
        }


        Toolbar myToolbar = (Toolbar) findViewById(R.id.favorite_activity_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        this.setTitle(getResources().getString(R.string.toolbar_title_favorite));
    }


}
