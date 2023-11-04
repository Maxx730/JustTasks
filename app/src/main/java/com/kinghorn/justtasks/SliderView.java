package com.kinghorn.justtasks;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

public class SliderView extends LinearLayout implements View.OnClickListener, ValueAnimator.AnimatorUpdateListener {

    private static final int MODAL_SIZE = 320;
    private static final int SLIDE_TOP = 0;
    private static final int SLIDE_BOTTOM = 1;

    private int slideDirection;
    private boolean isOpen = false;

    public SliderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray _values = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SliderView, 0, 0);

        slideDirection = _values.getInteger(0, SLIDE_TOP);

        inflate(context, R.layout.sliderview, this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        ViewGroup _frame = (ViewGroup) getChildAt(0);

        for(int i = 0;i < _frame.getChildCount();i++) {
            ViewGroup _layout = (ViewGroup) _frame.getChildAt(i);

            if (isOpen) {
                LinearLayout.LayoutParams _params;
                _layout.setVisibility(View.VISIBLE);
                switch(i) {
                    case 0:
                        break;
                    case 1:
                        _params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                        _layout.setPadding(0, TaskUtils.ConvertDPtoPixel(8, getContext()), 0, TaskUtils.ConvertDPtoPixel(8, getContext()));
                        break;
                    case 2:
                        _layout.setAlpha(0.5F);
                        _layout.setClickable(false);
                }
            } else {
                switch(i) {
                    case 1:
                        _layout.setVisibility(View.GONE);
                        break;
                    case 2:
                        _layout.setAlpha(1.0F);
                        _layout.setClickable(true);
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void updateTaskList(JSONArray tasks, Context context, TaskListInterface inter) {
        TaskAdapter _adapter = new TaskAdapter(tasks, inter, context);
        RecyclerView _view = (RecyclerView) findViewById(R.id.TaskRecycler);

        _view.setLayoutManager(new LinearLayoutManager(getContext()));
        _view.setAdapter(_adapter);
    }

    public void open(SliderInterface inter, View modalView){
        isOpen = true;
        invalidate();
        requestLayout();

        LinearLayout _modal = (LinearLayout) findViewById(R.id.SliderTop);
        FrameLayout _mask = (FrameLayout) findViewById(R.id.click_mask);
        _mask.setVisibility(View.VISIBLE);
        _modal.removeAllViews();
        _modal.addView(modalView);

        ValueAnimator _anim = ValueAnimator.ofInt(0, TaskUtils.ConvertDPtoPixel(MODAL_SIZE, getContext()));
        _anim.addUpdateListener(this);
        _anim.setInterpolator(new OvershootInterpolator());
        _anim.setDuration(500);
        _anim.start();

        inter.OnSliderOpened();
    }
    public void close(SliderInterface inter){
        isOpen = false;
        invalidate();
        requestLayout();

        FrameLayout _mask = (FrameLayout) findViewById(R.id.click_mask);
        _mask.setVisibility(View.GONE);

        ValueAnimator _anim = ValueAnimator.ofInt(TaskUtils.ConvertDPtoPixel(MODAL_SIZE, getContext()), 0);
        _anim.addUpdateListener(this);
        _anim.setInterpolator(new AccelerateInterpolator());
        _anim.setDuration(250);
        _anim.start();

        inter.OnSliderClosed();
    }

    public void showList() {
        LinearLayout _empty = (LinearLayout) findViewById(R.id.Empty);
        _empty.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
        LinearLayout _modal = (LinearLayout) findViewById(R.id.SliderTop);
        LinearLayout.LayoutParams _params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        _params.height = (int) valueAnimator.getAnimatedValue();
        _modal.setLayoutParams(_params);
    }
}
