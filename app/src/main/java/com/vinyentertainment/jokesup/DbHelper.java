package com.vinyentertainment.jokesup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;



public class DbHelper extends SQLiteOpenHelper {

    private static DbHelper mInstance = null;
    private static final String TAG = "DbHelper";

    public static final String DATABASE_NAME = "jokesapp.db";
    private static final int DATABASE_VERSION = 2;

    public static final String FAVORITE_JOKES_TABLE_NAME = "favoritejokes";
    public static final String FAVORITE_JOKES_COLUMN_ID = "id";
    public static final String FAVORITE_JOKES_COLUMN_CATEGORY = "category";

    public static final String ALLJOKES_TABLE_NAME = "jokes";
    public static final String ALLJOKES_COLUMN_ID= "id";
    public static final String ALLJOKES_COLUMN_TITLE= "title";
    public static final String ALLJOKES_COLUMN_STORY= "story";
    public static final String ALLJOKES_COLUMN_CATEGORY= "category";
    public static final String ALLJOKES_COLUMN_IS_FAVORITED = "isfavorited";
    public static final String ALLJOKES_COLUMN_IS_SHOWN = "isshown";
    public static final String ALLJOKES_COLUMN_IS_SENT_TO_FIREBASE = "senttofirebase";
    public static final String ALLJOKES_COLUMN_RATING = "rating";

    //To make it Singleton
    public static DbHelper getInstance(Context ctx) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if(mInstance==null){
            mInstance = new DbHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static factory method "getInstance()" instead.
     */

    private DbHelper(Context ctx) {
        super(ctx,DATABASE_NAME,null,DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + FAVORITE_JOKES_TABLE_NAME +
                        "(" + FAVORITE_JOKES_COLUMN_ID + " TEXT PRIMARY KEY, " +
                        FAVORITE_JOKES_COLUMN_CATEGORY + " TEXT)"
        );

        db.execSQL(
                "CREATE TABLE " + ALLJOKES_TABLE_NAME +
                        "(" + ALLJOKES_COLUMN_ID + " TEXT PRIMARY KEY, " +
                        ALLJOKES_COLUMN_TITLE + " TEXT NOT NULL, " +
                        ALLJOKES_COLUMN_STORY + " TEXT NOT NULL, " +
                        ALLJOKES_COLUMN_CATEGORY + " TEXT NOT NULL, " +
                        ALLJOKES_COLUMN_IS_SHOWN + " INTEGER DEFAULT 0, " +
                        ALLJOKES_COLUMN_IS_FAVORITED + " INTEGER DEFAULT 0, " +
                        ALLJOKES_COLUMN_IS_SENT_TO_FIREBASE + " INTEGER DEFAULT 0, " +
                        ALLJOKES_COLUMN_RATING + " INTEGER DEFAULT 0)"
        );

        //Log.i("DbHelper onCreate--","worked");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            db.execSQL("DROP TABLE IF EXISTS " + FAVORITE_JOKES_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ALLJOKES_TABLE_NAME);
            onCreate(db);

        }catch(Exception e)
        {
            //Log.i(TAG, "Error in onUpgrade"+ e.getMessage());
        }
    }

    public boolean insertJokeIntoFavorites(String ID, String category) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(FAVORITE_JOKES_COLUMN_ID,ID);
            contentValues.put(FAVORITE_JOKES_COLUMN_CATEGORY,category);

            long newRowID = db.insert(FAVORITE_JOKES_TABLE_NAME,null,contentValues);

            if(newRowID==-1)
                return false;
            else
                return true;

        }catch (Exception e)
        {
            //Log.i(TAG, "Error in insertJokeIntoFavorites "+e.getMessage());
        }
        //Return false boolean if code in try block fails.
        return false;
    }

    public boolean insertIntoAllJokes(String ID, String title, String story, String category )
    {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put(ALLJOKES_COLUMN_ID,ID);
            contentValues.put(ALLJOKES_COLUMN_TITLE,title);
            contentValues.put(ALLJOKES_COLUMN_STORY,story);
            contentValues.put(ALLJOKES_COLUMN_CATEGORY,category);

            long newRowID = db.insert(ALLJOKES_TABLE_NAME,null,contentValues);

            if(newRowID==1)
                return false;
            else
                return true;
        }catch (Exception e)
        {
            //Log.i(TAG, "Error in insertIntoAllJokes "+e.getMessage());
        }
        //Return false boolean if code in try block fails.
        return false;
    }

    public int numberOfRowsofFavorites() {
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            int numRows = (int) DatabaseUtils.queryNumEntries(db,FAVORITE_JOKES_TABLE_NAME);
            return numRows;

        }catch (Exception e)
        {
            //Log.i(TAG, "Error in insertIntoAllJokes "+e.getMessage());
        }
        //Return false boolean if code in try block fails.
        return 0;
    }

    //Update method not necessary for this use case:
    /*
    public boolean updateJoke(String id, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(FAVORITE_JOKES_COLUMN_ID,id);
        contentValues.put(FAVORITE_JOKES_COLUMN_CATEGORY,category);
        //db.update();
    }
    */

    public Integer deleteJokeFromFavorites(String ID) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(FAVORITE_JOKES_TABLE_NAME,FAVORITE_JOKES_COLUMN_ID + " = ? ", new String[] {ID});
        }
        catch (Exception e)
        {
            //Log.i(TAG, "Error in deleteJokeFromFavorites "+e.getMessage());
        }
        //Return false boolean if code in try block fails.
        return 0;
    }


    /*
    public List getAllJokesFromFavorites() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + FAVORITE_JOKES_TABLE_NAME,null);

        List itemIDs = new ArrayList<>();
        while(res.moveToNext()) {
            String itemID = res.getString(
                    res.getColumnIndexOrThrow(FAVORITE_JOKES_COLUMN_ID));
            itemIDs.add(itemID);
        }

        //return res;
        res.close();
        return  itemIDs;
    }
    */

    public boolean giveRating(String ID, Integer rating) {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ALLJOKES_COLUMN_RATING,rating);
            int affectedRows = db.update(ALLJOKES_TABLE_NAME, contentValues, ALLJOKES_COLUMN_ID + " = ? ", new String[] {ID});
            return affectedRows >0;

        }catch(Exception e)
        {
            //Log.i(TAG, "Error in giveRating "+e.getMessage());
        }
        //Return false boolean if code in try block fails.
        return false;
    }

    public long numberOfUnsentRatings()
    {
    try {
        String  JOKE_SELECT_QUERY =
                String.format("SELECT COUNT(*) FROM %s WHERE %s != 0 AND %s = 0",
                        ALLJOKES_TABLE_NAME,
                        ALLJOKES_COLUMN_RATING,
                        ALLJOKES_COLUMN_IS_SENT_TO_FIREBASE);

        SQLiteDatabase db = getReadableDatabase();

        long taskCount = DatabaseUtils.longForQuery(db,JOKE_SELECT_QUERY,null);

        return taskCount;

    }catch (Exception e)
    {
        //Log.i(TAG, "Error in numberOfUnsentRatings "+e.getMessage());
    }
        //Return false boolean if code in try block fails.
        return 0;
    }

    public List<JokesRatings> getRatingsForFirebase() {
        List<JokesRatings> jokesRatings = new ArrayList<>();

        try{
            String JOKE_SELECT_QUERY = String.format("SELECT %s,%s FROM %s WHERE %s != 0 AND %s = 0 ",
                    ALLJOKES_COLUMN_ID,
                    ALLJOKES_COLUMN_RATING,
                    ALLJOKES_TABLE_NAME,
                    ALLJOKES_COLUMN_RATING,
                    ALLJOKES_COLUMN_IS_SENT_TO_FIREBASE);

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor res = db.rawQuery(JOKE_SELECT_QUERY,null);

            try {
                if(res.moveToFirst()) {
                    do {
                        JokesRatings joke = new JokesRatings();
                        joke.id = res.getString(res.getColumnIndex(ALLJOKES_COLUMN_ID));
                        joke.rating = res.getInt(res.getColumnIndex(ALLJOKES_COLUMN_RATING));
                        jokesRatings.add(joke);
                    }while(res.moveToNext());
                }
            } catch (Exception e) {
                //Log.d("Getting Joke Data","Error while trying to get jokes ratings from database");
            }finally {
                if(res !=null && !res.isClosed()) {
                    res.close();
                }
            }
            //Log.i("Dbhelper'da",String.valueOf(jokes.size()));

            return jokesRatings;

        }catch(Exception e)
        {
            //Log.i(TAG, "Error in getRatingsForFirebase "+e.getMessage());
        }

        //Return default value if code in try block fails.
        return jokesRatings;
    }



    public boolean markRatingsAsSent() {
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ALLJOKES_COLUMN_IS_SENT_TO_FIREBASE,1);
            String JOKE_WHERE_CLAUSE = String.format("%s != 0 AND %s = 0 ",
                    ALLJOKES_COLUMN_RATING,
                    ALLJOKES_COLUMN_IS_SENT_TO_FIREBASE);

            int affectedRows = db.update(ALLJOKES_TABLE_NAME,contentValues,ALLJOKES_COLUMN_RATING + " != ? AND " + ALLJOKES_COLUMN_IS_SENT_TO_FIREBASE + " = ? ",new  String[] {"0","0"});
            return affectedRows>0;

        }catch (Exception e)
        {
            //Log.i(TAG, "Error in markRatingsAsSent "+e.getMessage());
        }
        //Return default value if code in try block fails.
        return false;
    }




    //TODO: Put an index on your tag field. If you do not, a query for a non-existent tag will do a full table scan.

    public boolean isJokeIDExistsInFavorites(String ID) {

        try{
            SQLiteDatabase db = this.getReadableDatabase();

            String[] columns = {FAVORITE_JOKES_COLUMN_ID};
            String selection = FAVORITE_JOKES_COLUMN_ID + " =?";
            String[] selectionArgs = {ID};
            String limit = "1";

            Cursor res = db.query(FAVORITE_JOKES_TABLE_NAME,columns,selection,selectionArgs,null,null,null);
            boolean exists = (res.getCount()>0);
            res.close();
            return exists;
        }catch (Exception e)
        {
            //Log.i(TAG, "Error in isJokeIDExistsInFavorites "+e.getMessage());
        }

        //Return default value if code in try block fails.
        return false;

        /*
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + FAVORITE_JOKES_TABLE_NAME + " WHERE " + FAVORITE_JOKES_COLUMN_ID + " = " + ID +" LIMIT 1)";
        Cursor res = db.rawQuery(query,null);
        if(res.getCount()<=0) {
            res.close();
            return false;
        }
        res.close();
        return true;


        */
    }

    /*
    public Jokes getNextJoke(String intentCategory) {

        Jokes nextJoke = new Jokes();
        String JOKE_SELECT_QUERY;

        if(intentCategory.equals("random"))
        {
            JOKE_SELECT_QUERY =
                    String.format("SELECT * FROM %s WHERE %s = 0 LIMIT 1",
                            ALLJOKES_TABLE_NAME,
                            ALLJOKES_COLUMN_IS_SHOWN);
        }
        else
        {
            JOKE_SELECT_QUERY =
                    String.format("SELECT * FROM %s WHERE %s = '%s' AND %s = 0 LIMIT 1",
                            ALLJOKES_TABLE_NAME,
                            ALLJOKES_COLUMN_CATEGORY,
                            intentCategory,
                            ALLJOKES_COLUMN_IS_SHOWN);
        }

        Log.i("query: ",JOKE_SELECT_QUERY);

        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery(JOKE_SELECT_QUERY,null);
        try {
            if(res.moveToFirst()) {
                nextJoke.id = res.getString(res.getColumnIndex(ALLJOKES_COLUMN_ID));
                nextJoke.title = res.getString(res.getColumnIndex(ALLJOKES_COLUMN_TITLE));
                nextJoke.story = res.getString(res.getColumnIndex(ALLJOKES_COLUMN_STORY));
                nextJoke.category = res.getString(res.getColumnIndex(ALLJOKES_COLUMN_CATEGORY));
                nextJoke.isfavorited = res.getInt(res.getColumnIndex(ALLJOKES_COLUMN_IS_FAVORITED));
                nextJoke.isshown = res.getInt(res.getColumnIndex(ALLJOKES_COLUMN_IS_SHOWN));
                nextJoke.rating = res.getInt(res.getColumnIndex(ALLJOKES_COLUMN_RATING));
            }
        } catch (Exception e) {
            Log.d("Dbhelper","Error while trying to get posts from database");
        } finally {
            if(res != null && !res.isClosed()) {
                res.close();
            }
        }

        //markAsShown(nextJoke.id);

        return nextJoke;
    }
    */

    public long getNumberOfNotShownJokes (String intentCategory) {

        try{
            String JOKE_SELECT_QUERY;

            if(intentCategory.equals("random"))
            {
                JOKE_SELECT_QUERY =
                        String.format("SELECT COUNT(*) FROM %s WHERE %s = 0",
                                ALLJOKES_TABLE_NAME,
                                ALLJOKES_COLUMN_IS_SHOWN);
            }
            else
            {
                JOKE_SELECT_QUERY =
                        String.format("SELECT COUNT(*) FROM %s WHERE %s = '%s' AND %s = 0",
                                ALLJOKES_TABLE_NAME,
                                ALLJOKES_COLUMN_CATEGORY,
                                intentCategory,
                                ALLJOKES_COLUMN_IS_SHOWN);
            }

            SQLiteDatabase db = getReadableDatabase();

            long taskCount = DatabaseUtils.longForQuery(db,JOKE_SELECT_QUERY,null);

            return taskCount;

        }catch (Exception e)
        {
            //Log.i(TAG, "Error in getNumberOfNotShownJokes "+e.getMessage());
        }

        //Return default value if code in try block fails.
        return 0;

    }

    public boolean markAsShown(String ID) {

        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ALLJOKES_COLUMN_IS_SHOWN,1);
            int affectedRows = db.update(ALLJOKES_TABLE_NAME,contentValues,ALLJOKES_COLUMN_ID + " = ? ",new  String[] {ID});
            return affectedRows>0;
        }catch (Exception e)
        {
            //Log.i(TAG, "Error in markAsShown" + e.getMessage());
        }
        //Return default value if code in try block fails.
        return false;
    }

    public void markAsNotShown() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            String JOKE_SELECT_QUERY =
                    String.format("UPDATE %s SET %s = 0",
                            ALLJOKES_TABLE_NAME,
                            ALLJOKES_COLUMN_IS_SHOWN);
            Cursor res = db.rawQuery(JOKE_SELECT_QUERY,null);
            res.close();
        }catch (Exception e)
        {
            //Log.i(TAG, "Error in markAsSNotShown" + e.getMessage());
        }
    }


    // Get all jokes in the database wrt a category
    public List<Jokes> getAllJokesInACategory(String category) {
            List<Jokes> jokes = new ArrayList<>();

            try {
                String JOKE_SELECT_QUERY;

                //If there are less than 5 jokes remaining, change the selection query to
                //Include shown jokes as well. Otherwise, vievPager may crash when if it tries
                //to reach empty objects. getCount() method will prevent it but, for users to use
                //the app, do it.
                if(getNumberOfNotShownJokes(category)<=5)
                {
                    if(category.equals("random"))
                    {
                        JOKE_SELECT_QUERY =
                                String.format("SELECT * FROM %s WHERE %s !='nerd' LIMIT 50",
                                        ALLJOKES_TABLE_NAME,
                                        ALLJOKES_COLUMN_CATEGORY);

                    }
                    else
                    {
                        JOKE_SELECT_QUERY =
                                String.format("SELECT * FROM %s WHERE %s = '%s' LIMIT 50",
                                        ALLJOKES_TABLE_NAME,
                                        ALLJOKES_COLUMN_CATEGORY,
                                        category);
                    }
                }
                else
                {
                    if(category.equals("random"))
                    {
                        JOKE_SELECT_QUERY =
                                String.format("SELECT * FROM %s WHERE %s = 0 AND %s !='nerd' LIMIT 50",
                                        ALLJOKES_TABLE_NAME,
                                        ALLJOKES_COLUMN_IS_SHOWN,
                                        ALLJOKES_COLUMN_CATEGORY);

                    }
                    else
                    {
                        JOKE_SELECT_QUERY =
                                String.format("SELECT * FROM %s WHERE %s = '%s' AND %s = 0 LIMIT 50",
                                        ALLJOKES_TABLE_NAME,
                                        ALLJOKES_COLUMN_CATEGORY,
                                        category,
                                        ALLJOKES_COLUMN_IS_SHOWN);
                    }
                }

                SQLiteDatabase db = this.getReadableDatabase();

                Cursor res = db.rawQuery(JOKE_SELECT_QUERY,null);

                try {
                    if(res.moveToFirst()) {
                        do {
                            Jokes joke = new Jokes();
                            joke.id = res.getString(res.getColumnIndex(ALLJOKES_COLUMN_ID));
                            joke.title = res.getString(res.getColumnIndex(ALLJOKES_COLUMN_TITLE));
                            joke.story = res.getString(res.getColumnIndex(ALLJOKES_COLUMN_STORY));
                            joke.category = res.getString(res.getColumnIndex(ALLJOKES_COLUMN_CATEGORY));
                            joke.rating = res.getInt(res.getColumnIndex(ALLJOKES_COLUMN_RATING));
                            joke.isshown = res.getInt(res.getColumnIndex(ALLJOKES_COLUMN_IS_SHOWN));
                            joke.isfavorited = res.getInt(res.getColumnIndex(ALLJOKES_COLUMN_IS_FAVORITED));
                            jokes.add(joke);
                        }while(res.moveToNext());
                    }
                } catch (Exception e) {
                    //Log.d("Getting Joke Data","Error while trying to get jokes from database");
                }finally {
                    if(res !=null && !res.isClosed()) {
                        res.close();
                    }
                }
                //Log.i("Dbhelper'da",String.valueOf(jokes.size()));

                return jokes;
            }catch (Exception e){
                //Log.i(TAG, "Error in getAllJokesInACategory" + e.getMessage());
            }
        //Return default value if code in try block fails.
        return jokes;
    }

    public List<Jokes> getAllJokesFromFavorites() {
        List<Jokes> jokes = new ArrayList<>();

        try {
            String JOKE_SELECT_QUERY;

            JOKE_SELECT_QUERY =
                    String.format("SELECT A.%s,A.%s,A.%s,A.%s,A.%s FROM %s AS A, %s AS B WHERE A.%s = B.%s ",
                            ALLJOKES_COLUMN_ID,
                            ALLJOKES_COLUMN_TITLE,
                            ALLJOKES_COLUMN_STORY,
                            ALLJOKES_COLUMN_CATEGORY,
                            ALLJOKES_COLUMN_RATING,
                            ALLJOKES_TABLE_NAME,
                            FAVORITE_JOKES_TABLE_NAME,
                            ALLJOKES_COLUMN_ID,
                            FAVORITE_JOKES_COLUMN_ID);

            //Log.i("JOKE_SELECT_QUERY",JOKE_SELECT_QUERY);

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor res = db.rawQuery(JOKE_SELECT_QUERY,null);

            try {
                if(res.moveToFirst()) {
                    do {
                        Jokes joke = new Jokes();
                        joke.id = res.getString(res.getColumnIndex(ALLJOKES_COLUMN_ID));
                        joke.title = res.getString(res.getColumnIndex(ALLJOKES_COLUMN_TITLE));
                        joke.story = res.getString(res.getColumnIndex(ALLJOKES_COLUMN_STORY));
                        joke.category = res.getString(res.getColumnIndex(ALLJOKES_COLUMN_CATEGORY));
                        joke.rating = res.getInt(res.getColumnIndex(ALLJOKES_COLUMN_RATING));
                        jokes.add(joke);
                    }while(res.moveToNext());
                }
            } catch (Exception e) {
                //Log.d("Getting Joke Data","Error while trying to get favorite jokes from database");
            }finally {
                if(res !=null && !res.isClosed()) {
                    res.close();
                }
            }
            //Log.i("Dbhelper favorijokes",String.valueOf(jokes.size()));

            return jokes;

        }catch (Exception e)
        {
            //Log.i(TAG, "Error in getAllJokesFromFavorites" + e.getMessage());
        }
        //Return default value if code in try block fails.
        return jokes;
    }

    public int getNumberOfAllJokes() {

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            int numRows = (int) DatabaseUtils.queryNumEntries(db,ALLJOKES_TABLE_NAME);
            return numRows;

        }catch (Exception e)
        {
            //Log.i(TAG, "Error in getAllJokesFromFavorites" + e.getMessage());
        }

        //Return default value if code in try block fails.
        return 0;
    }
}
