package com.g.e.sunset;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;


public class SunsetFragment extends Fragment {

    private final String TAG = "com.g.e.sunset";

    private View mSceneView;
    private View mSunView;
    private View mSkyView;

    private int mDaySkyColor;
    private int mSunsetSkyColor;
    private int mNightsSkyColor;
    private int mSunriseSkyColor;

    private float mSunDayPosition;
    private float mSunNightPosition;

    private boolean mIsNight = false;

    public static SunsetFragment createInstance(){
        return new SunsetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sunset,
                container, false);

        mSceneView = view;
        mSunView = view.findViewById(R.id.sun);
        mSkyView = view.findViewById(R.id.sky);

        Resources resources = getResources();
        mDaySkyColor = resources.getColor(R.color.blue_sky);
        mSunsetSkyColor = resources.getColor(R.color.sunset_sky);
        mNightsSkyColor = resources.getColor(R.color.night_sky);
        mSunriseSkyColor = resources.getColor(R.color.sunrise_sky);


        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mIsNight) {
                    startSunsetAnimation();
                    mIsNight = true;
                } else {
                    startSunriseAnimation();
                    mIsNight = false;
                }
            }
        });
        return view;
    }



    private void startSunsetAnimation(){

        if(mSunDayPosition==0) {
            mSunDayPosition = mSunView.getTop();
            Log.i(TAG, "start sunset startPosition" + mSunDayPosition);
        }

        if(mSunNightPosition==0) {
            mSunNightPosition = mSkyView.getHeight();
            Log.i(TAG, "start sunset endPosition" + mSunNightPosition);
        }

        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", mSunDayPosition, mSunNightPosition)
                .setDuration(3000);
        heightAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator sunsetSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor",
                        mDaySkyColor, mSunsetSkyColor)
                .setDuration(3000);
        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator nightSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor",
                        mSunsetSkyColor, mNightsSkyColor)
                .setDuration(1500);
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet
                .play(heightAnimator)
                .with(sunsetSkyAnimator)
                .before(nightSkyAnimator);
        animatorSet.start();
    }

    private void startSunriseAnimation() {
       ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", mSunDayPosition)
                .setDuration(3000);
        heightAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator sunriseSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor",
                        mNightsSkyColor, mSunriseSkyColor)
                .setDuration(3000);
        sunriseSkyAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator daySkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor",
                        mSunriseSkyColor, mDaySkyColor)
                .setDuration(1500);
        daySkyAnimator.setEvaluator(new ArgbEvaluator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet
                .play(heightAnimator)
                .with(sunriseSkyAnimator)
                .before(daySkyAnimator);
        animatorSet.start();
    }
}
