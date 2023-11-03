package com.kinghorn.justtasks;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BusySpinner extends FrameLayout implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
    public BusySpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.busy_spinner, this);
    }

    public void show() {
        FrameLayout _frame = (FrameLayout) findViewById(R.id.busy_frame);
        _frame.setVisibility(View.VISIBLE);
        ValueAnimator _anim = ValueAnimator.ofFloat(0.0F, 1.0F);
        _anim.addUpdateListener(this);
        _anim.setInterpolator(new AccelerateInterpolator());
        _anim.setDuration(500);
        _anim.start();
    }

    public void hide() {
        FrameLayout _frame = (FrameLayout) findViewById(R.id.busy_frame);
        ValueAnimator _anim = ValueAnimator.ofFloat(1.0F, 0.0F);
        _anim.addUpdateListener(this);
        _anim.addListener(this);
        _anim.setInterpolator(new AccelerateInterpolator());
        _anim.setDuration(500);
        _anim.start();
    }

    public void setMessage(String message) {
        TextView _message = (TextView) findViewById(R.id.loading_message);
        _message.setText(message);
    }

    @Override
    public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
        FrameLayout _frame = (FrameLayout) findViewById(R.id.busy_frame);
        _frame.setAlpha((Float) valueAnimator.getAnimatedValue());
    }

    @Override
    public void onAnimationStart(@NonNull Animator animator) {

    }

    @Override
    public void onAnimationEnd(@NonNull Animator animator) {
        FrameLayout _frame = (FrameLayout) findViewById(R.id.busy_frame);
        _frame.setVisibility(_frame.getAlpha() < 0.1 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onAnimationCancel(@NonNull Animator animator) {

    }

    @Override
    public void onAnimationRepeat(@NonNull Animator animator) {

    }
}
