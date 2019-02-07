package com.g.e.sunset;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;


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
    private int mCurrentSkyColor;
    private AnimatorSet mSunriseAnimatorSet;
    private AnimatorSet mSunsetanimatorSet;

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
                    if(mSunriseAnimatorSet!=null) mSunriseAnimatorSet.cancel();
                    startSunsetAnimation();
                    mIsNight = true;
                } else {
                    if(mSunsetanimatorSet!= null) mSunsetanimatorSet.cancel();
                    startSunriseAnimation();
                    mIsNight = false;
                }
            }
        });
        return view;
    }



    private void startSunsetAnimation(){
        if(mCurrentSkyColor==0) {
            mCurrentSkyColor = getCurrentSkyColor();
            Log.i(TAG, "current sky color " + mCurrentSkyColor + " DaySkyColor " + mDaySkyColor);
        }

        if(mSunDayPosition==0) {
            mSunDayPosition = mSunView.getTop();
            Log.i(TAG, "start sunset startPosition" + mSunDayPosition);
        }

        if(mSunNightPosition==0) {
            mSunNightPosition = mSkyView.getHeight();
            Log.i(TAG, "start sunset endPosition" + mSunNightPosition);
        }

        ObjectAnimator heightAnimator = getSunHeightAnimator(mSunNightPosition, 3000);
        ObjectAnimator sunsetSkyAnimator = getSkyColorAnimator(mCurrentSkyColor, mSunsetSkyColor, 3000);
        ObjectAnimator nightSkyAnimator = getSkyColorAnimator(mSunsetSkyColor, mNightsSkyColor, 1500);

        mSunsetanimatorSet = new AnimatorSet();
        mSunsetanimatorSet
                .play(heightAnimator)
                .with(sunsetSkyAnimator)
                .before(nightSkyAnimator);
        mSunsetanimatorSet.start();
    }

    @NonNull
    private ObjectAnimator getSunHeightAnimator(float finishSunPosition, int duration) {
        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", finishSunPosition)
                .setDuration(duration);
        heightAnimator.setInterpolator(new AccelerateInterpolator());

        return heightAnimator;
    }

    private int getCurrentSkyColor() {
        ColorDrawable currentSkyColor = (ColorDrawable) mSkyView.getBackground();
        return currentSkyColor.getColor();
    }

    private void startSunriseAnimation() {

        ObjectAnimator heightAnimator = getSunHeightAnimator(mSunDayPosition, 3000);
        ObjectAnimator sunriseSkyAnimator = getSkyColorAnimator(mCurrentSkyColor, mSunriseSkyColor, 3000);
        ObjectAnimator daySkyAnimator = getSkyColorAnimator(mSunriseSkyColor, mDaySkyColor, 1500);

       mSunriseAnimatorSet = new AnimatorSet();
        mSunriseAnimatorSet
                .play(heightAnimator)
                .with(sunriseSkyAnimator)
                .before(daySkyAnimator);
        mSunriseAnimatorSet.start();
    }

    private ObjectAnimator getSkyColorAnimator (int startSkyColour, final int finishSkyColor, int duration){
        ObjectAnimator skyColorAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor",
                        startSkyColour, finishSkyColor)
                .setDuration(duration);
        Log.i(TAG, "current sky color " + mCurrentSkyColor);
        skyColorAnimator.setEvaluator(new ArgbEvaluator());
        skyColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentSkyColor = (int)animation.getAnimatedValue();
                Log.i(TAG, "current sky color " + mCurrentSkyColor + " finishSkyColor " + finishSkyColor);
            }
        });

        return skyColorAnimator;
    }
}
