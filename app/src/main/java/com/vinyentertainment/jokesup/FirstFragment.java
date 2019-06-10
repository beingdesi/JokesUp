package com.vinyentertainment.jokesup;

//import android.app.Fragment;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.TextView;


public class FirstFragment extends Fragment {

    private String title,story,category;
    private int id;
    public FragmentCommunicator fComm;
    private static final String TAG = "FirstFragment";

    boolean clickUpvote = false;
    boolean clickDownvote = false;


    // newInstance constructor for creating fragment with arguments
    public static FirstFragment newInstance(int id, String title, String story)
    {
        FirstFragment fragmentFirst = new FirstFragment();
        Bundle args = new Bundle();

        args.putInt("id",id);
        args.putString("title",title);
        args.putString("story",story);
        //args.putString("category",category);

        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            fComm = (FragmentCommunicator) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
            "must implement FragmentCommunicator");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            id = getArguments().getInt("id",0);
            title = getArguments().getString("title");
            story = getArguments().getString("story");
            //category = getArguments().getString("category");
        }
        catch (Exception e)
        {
            //Log.i(TAG,"Error in FirstFragment OnCreate");
        }
    }



    // Inflate the view for the fragment based on layout XML

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first,container,false);

        try {
            TextView jokeTitle = (TextView) view.findViewById(R.id.jokeTitle);
            jokeTitle.setText(title);

            TextView jokeStory = (TextView) view.findViewById(R.id.jokeStory);
            jokeStory.setText(story);

            //TextView jokeCategory = (TextView) view.findViewById(R.id.jokeCategory);
            //jokeCategory.setText(category);

            final ImageButton buttonUpvote = (ImageButton) view.findViewById(R.id.buttonUpvote);
            final ImageButton buttonDownvote = (ImageButton) view.findViewById(R.id.buttonDownvote);

            buttonUpvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //buttonEffect(v);
                    if(clickUpvote==true)
                    {
                        clickUpvote=false;
                        //Log.i("Button Upvote","Click was true now false");
                    }
                    else
                    {
                        clickUpvote=true;
                        //Log.i("Button Upvote","Click was false now true");
                    }
                    //Log.i("boolean",String.valueOf(click));
                    if(clickUpvote)
                    {
                        v.setBackgroundResource(R.drawable.btn_rounded_orange);
                        buttonDownvote.setBackgroundResource(R.drawable.btn_rounded_accent);

                        fComm.fragmentContactActivity("upvoted");
                        //Log.i("Button Upvote","Click True");
                        clickDownvote=false;
                    }
                    else
                    {
                        v.setBackgroundResource(R.drawable.btn_rounded_accent);
                        buttonDownvote.setBackgroundResource(R.drawable.btn_rounded_accent);

                        fComm.fragmentContactActivity("neutral");
                        //Log.i("Button Upvote","Click False");
                        clickDownvote =false;
                    }
                }
            });


            buttonDownvote.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //buttonEffect(v);
                    if(clickDownvote==true)
                    {
                        //Log.i("Button Downvote","Click was true now false");
                        clickDownvote=false;
                    }
                    else
                    {
                        //Log.i("Button Downvote","Click was false now true");
                        clickDownvote=true;
                    }
                    //Log.i("boolean",String.valueOf(click));
                    if(clickDownvote)
                    {
                        v.setBackgroundResource(R.drawable.btn_rounded_orange);
                        buttonUpvote.setBackgroundResource(R.drawable.btn_rounded_accent);

                        fComm.fragmentContactActivity("downvoted");
                        //Log.i("Button Downvote","Click True");


                        clickUpvote=false;
                    }
                    else
                    {
                        v.setBackgroundResource(R.drawable.btn_rounded_accent);
                        buttonUpvote.setBackgroundResource(R.drawable.btn_rounded_accent);

                        fComm.fragmentContactActivity("neutral");
                        //Log.i("Button Downvote","Click False");

                        clickUpvote=false;
                    }
                    //Log.i("aaa","bbb");
                }
            });

            //Fragment puts some actions in the action bar, it should call setHasOptionsMenu(true).
            setHasOptionsMenu(true);

        } catch (Exception e)
        {
            //Log.i(TAG, "Error in onCreateView");
        }

        return view;
    }
}
