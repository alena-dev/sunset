package com.g.e.sunset;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
    private View mSunReflectionView;
    private View mSeaView;
    private View mSunshine;

    private int mDaySkyColor;
    private int mSunsetSkyColor;
    private int mNightsSkyColor;
    private int mSunriseSkyColor;
    private int mCurrentSkyColor;

    private float mSunDayPosition;
    private float mSunNightPosition;

    private AnimatorSet mSunriseAnimatorSet;
    private AnimatorSet mSunsetAnimatorSet;
    private AnimatorSet mSunshineAnimatorSet;

    private boolean mIsNight = false;
    private boolean mIsSunshineCanseled;

    private float mSunReflectionDayPosition;
    private float mSunReflectionNightPosition;


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
        mSunReflectionView = view.findViewById(R.id.sun_reflection);
        mSeaView = view.findViewById(R.id.sea);
        mSunshine = view.findViewById(R.id.sunshine);

        Resources resources = getResources();
        mDaySkyColor = resources.getColor(R.color.blue_sky);
        mSunsetSkyColor = resources.getColor(R.color.sunset_sky);
        mNightsSkyColor = resources.getColor(R.color.night_sky);
        mSunriseSkyColor = resources.getColor(R.color.sunrise_sky);


        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mSunshineAnimatorSet == null) startSunshineAnimation();

                if (!mIsNight) {
                    if (mSunriseAnimatorSet != null) mSunriseAnimatorSet.cancel();
                    startSunsetAnimation();
                    mIsNight = true;
                } else {
                    if (mSunsetAnimatorSet != null) mSunsetAnimatorSet.cancel();
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
            mSunReflectionDayPosition = mSunReflectionView.getTop();
            Log.i(TAG, "start sunset startPosition" + mSunDayPosition + " reflectionPosition " + mSunReflectionDayPosition);
        }

        if(mSunNightPosition==0) {
            mSunNightPosition = mSkyView.getHeight();
            mSunReflectionNightPosition = mSeaView.getTop()- mSunReflectionView.getHeight();
            Log.i(TAG, "start sunset endPosition" + mSunNightPosition + " reflectionPosition " + mSunReflectionNightPosition);
        }

        int durationSunset = 3000;
        ObjectAnimator sunHeightAnimator = getObjectHeightAnimator(mSunView, mSunNightPosition, durationSunset);
        ObjectAnimator sunsetSkyAnimator = getSkyColorAnimator(mCurrentSkyColor, mSunsetSkyColor, durationSunset);
        ObjectAnimator sunReflectionHeightAnimator = getObjectHeightAnimator (mSunReflectionView, mSunReflectionNightPosition, durationSunset);
        ObjectAnimator nightSkyAnimator = getSkyColorAnimator(mSunsetSkyColor, mNightsSkyColor, durationSunset/2);

        mSunsetAnimatorSet = new AnimatorSet();
        mSunsetAnimatorSet
                .play(sunHeightAnimator)
                .with(sunsetSkyAnimator)
                .with(sunReflectionHeightAnimator)
                .before(nightSkyAnimator);
        mSunsetAnimatorSet.start();
    }

    @NonNull
    private ObjectAnimator getObjectHeightAnimator(View object, float finishSunPosition, int duration) {
        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(object, "y", finishSunPosition)
                .setDuration(duration);
        heightAnimator.setInterpolator(new AccelerateInterpolator());

        return heightAnimator;
    }

    private int getCurrentSkyColor() {
        ColorDrawable currentSkyColor = (ColorDrawable) mSkyView.getBackground();
        return currentSkyColor.getColor();
    }

    private void startSunriseAnimation() {

        int durationSunrise = 3000;
        ObjectAnimator sunHeightAnimator = getObjectHeightAnimator(mSunView, mSunDayPosition, durationSunrise);
        ObjectAnimator sunriseSkyAnimator = getSkyColorAnimator(mCurrentSkyColor, mSunriseSkyColor, durationSunrise);
        ObjectAnimator sunReflectionHeightAnimator = getObjectHeightAnimator (mSunReflectionView, mSunReflectionDayPosition, durationSunrise);
        ObjectAnimator daySkyAnimator = getSkyColorAnimator(mSunriseSkyColor, mDaySkyColor, durationSunrise/2);

       mSunriseAnimatorSet = new AnimatorSet();
        mSunriseAnimatorSet
                .play(sunHeightAnimator)
                .with(sunriseSkyAnimator)
                .with(sunReflectionHeightAnimator)
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

    private void startSunshineAnimation() {
        int durationSunshine = 1000;
        int scale = 2;
        ObjectAnimator sunshineSizeYIncreaseAnimator = getSunshineRepeatingSizeAnimator("scaleY", durationSunshine, scale);
        ObjectAnimator sunshineSizeXIncreaseAnimator = getSunshineRepeatingSizeAnimator("scaleX", durationSunshine, scale);
        ObjectAnimator sunshineSizeYReduceAnimator = getSunshineRepeatingSizeAnimator("scaleY", durationSunshine, 1 / scale);
        ObjectAnimator sunshineSizeXReduceAnimator = getSunshineRepeatingSizeAnimator("scaleX", durationSunshine, 1 / scale);

        mSunshineAnimatorSet = new AnimatorSet();
        mSunshineAnimatorSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                mIsSunshineCanseled = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mIsSunshineCanseled = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!mIsSunshineCanseled) animation.start();
            }
        });

        mSunshineAnimatorSet
                .play(sunshineSizeYIncreaseAnimator)
                .with(sunshineSizeXIncreaseAnimator)
                .before(sunshineSizeYReduceAnimator);
        mSunshineAnimatorSet
                .play(sunshineSizeYReduceAnimator)
                .with(sunshineSizeXReduceAnimator);
        mSunshineAnimatorSet.start();
    }

    @NonNull
    private ObjectAnimator getSunshineRepeatingSizeAnimator(String propertyName, int durationSunshine, int scale) {
        ObjectAnimator animator =  ObjectAnimator
                    .ofFloat(mSunshine, propertyName, scale)
                    .setDuration(durationSunshine);
        return animator;
    }
}
