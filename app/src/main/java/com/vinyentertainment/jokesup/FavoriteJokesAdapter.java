package com.vinyentertainment.jokesup;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;


public class FavoriteJokesAdapter extends ArrayAdapter<Jokes> {

    static class ViewHolder{
        TextView jokeTitle;
        TextView jokeCategory;
        TextView jokeStory;
    }

    private Jokes joke;

    //Constructor
    public FavoriteJokesAdapter(Activity context, List<Jokes>joke) {
        super(context,0,joke);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        try {
            joke = getItem(position);

            //If convertview not already defined use this to inflate it.
            //If it's defined it is already inflated and now is in a scrap situation,
            //For recycling reasons
            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.favorite_jokes_list_item,parent,false);
            }

            ViewHolder holder = new ViewHolder();
            holder.jokeTitle = (TextView) convertView.findViewById(R.id.jokeTitle);
            holder.jokeCategory = (TextView) convertView.findViewById(R.id.jokeCategory);
            holder.jokeStory = (TextView) convertView.findViewById(R.id.jokeStory);
            convertView.setTag(holder);

            String pageTitle = "Random";
            pageTitle = joke.getCategory();
            switch (pageTitle) {
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
                case "sport":
                    pageTitle = "Sport";
                    break;
                case "money":
                    pageTitle = "Money";
                    break;
            }

            holder.jokeTitle.setText(joke.getTitle());
            holder.jokeCategory.setText("Category: " + pageTitle);
            holder.jokeStory.setText(joke.getStory().substring(0,40)+"...");
        }
        catch (Exception e) {
            //Log.e("FavJokeAdapter","Substring Out of bound error");
            //Log.i("FavJokeAdapter Error",joke.getStory());
        }
        return convertView;
    }
}