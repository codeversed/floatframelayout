package com.codeversed.example.floatframe;
/*
 * Copyright (C) 2014 Steve Albright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import android.content.res.TypedArray;

public final class FloatFrameLayout extends android.widget.FrameLayout {

    private int mScreenWidth;

    private int offset;
    int slideDuration;
    int fadeDuration;

    public FloatFrameLayout(android.content.Context context) {
        this(context, null);
    }

    public FloatFrameLayout(android.content.Context context, android.util.AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatFrameLayout(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatFrameLayout);

        slideDuration = a.getInt(R.styleable.FloatFrameLayout_floatFrameSlideDuration, 1000);
        fadeDuration = a.getInt(R.styleable.FloatFrameLayout_floatFrameFadeDuration, 900);
    }

    @Override
    public final void addView(View child, int index, ViewGroup.LayoutParams params) {

        if (child.getParent() instanceof android.widget.LinearLayout) {
            // Skip animation, continue adding the View...
            super.addView(child, index, params);
        }

        child.setAlpha(0f);
        child.setTag(offset * 200);
        offset++;
        enterAnimation(child);

        // For demonstration purposes only.
        final android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                floatPeriodOver();
            }
        }, 4000);

        // Continue adding the View...
        super.addView(child, index, params);
    }

    /**
     * Animate entrance of view.
     */
    private void enterAnimation(View child) {
        child.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int childMeasuredWidth = child.getMeasuredWidth();

        float startX = (mScreenWidth/2) - (childMeasuredWidth/2);

        ObjectAnimator xAnim = ObjectAnimator.ofFloat(child, "x",
                startX + (childMeasuredWidth / 2), startX).setDuration(slideDuration);
        xAnim.setRepeatCount(0);
        xAnim.setRepeatMode(ValueAnimator.REVERSE);
        xAnim.setInterpolator(new android.view.animation.DecelerateInterpolator(2f));

        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(child, "alpha", 0f, 1f).setDuration(fadeDuration);
        AnimatorSet alphaSeq = new AnimatorSet();
        alphaSeq.play(alphaAnim);

        Animator animation = new AnimatorSet();
        animation.setStartDelay(Long.parseLong(child.getTag().toString()));
        ((AnimatorSet) animation).playTogether(alphaAnim, xAnim);
        animation.start();
    }

    /**
     * Animate exit of view.
     */
    private void exitAnimation(View child) {
        child.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int childMeasuredWidth = child.getMeasuredWidth();

        float startX = (mScreenWidth/2) - (childMeasuredWidth/2);

        ObjectAnimator xAnim = ObjectAnimator.ofFloat(child, "x",
                startX, startX - (childMeasuredWidth / 2)).setDuration(slideDuration);
        xAnim.setRepeatCount(0);
        xAnim.setRepeatMode(ValueAnimator.REVERSE);
        xAnim.setInterpolator(new android.view.animation.AccelerateInterpolator(2f));

        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(child, "alpha", 1f, 0f).setDuration(fadeDuration);
        AnimatorSet alphaSeq = new AnimatorSet();
        alphaSeq.play(alphaAnim);

        Animator animation = new AnimatorSet();
        animation.setStartDelay(Long.parseLong(child.getTag().toString()));
        ((AnimatorSet) animation).playTogether(alphaAnim, xAnim);
        animation.start();
    }

    /**
     * Animates exit for child views.
     */
    public void floatPeriodOver() {
        for (int i = 0; i < this.getChildCount(); i++) {
            exitAnimation((this).getChildAt(i));
        }
    }

}