package com.kinghorn.justtasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import kotlinx.coroutines.scheduling.Task;

public class MainActivity extends AppCompatActivity implements StorageInterface, TaskListInterface {

    private StorageManager Storage;
    private JSONObject Tasks;
    private ImageButton ActionLeft, ActionRight, TopAdd, TopSettings, ClearSearch;
    private EditText TaskTitle, TaskDescription, TopSearch;
    private Animation YScaleIn, YScaleOut, FadeIn, FadeOut, ClearButtonOut, SlideRight;
    private FrameLayout ModalFrame, ModalShade, LoadingFrame;
    private LinearLayout ActionsLayout, TaskListLayout, EmptyLayout;
    private RecyclerView TaskRecycleList;
    private TaskAdapter TaskListAdapter;

    /* Overrides */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        this.YScaleIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_in);
        this.YScaleOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.modal_out);
        this.FadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shade_in);
        this.SlideRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right);
        this.FadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        this.ClearButtonOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);

        this.Storage = new StorageManager(getApplicationContext(), this);
        this.TopSearch = (EditText) findViewById(R.id.search_text);
        this.ActionsLayout = (LinearLayout) findViewById(R.id.top_actions);
        this.ClearSearch = (ImageButton) findViewById(R.id.clear_search);
        this.TopAdd = (ImageButton) findViewById(R.id.add_action);
        this.ActionLeft = (ImageButton) findViewById(R.id.cancel_add);
        this.ActionRight = (ImageButton) findViewById(R.id.confirm_add);
        this.TaskTitle = (EditText) findViewById(R.id.new_task_title);
        this.TaskDescription = (EditText) findViewById(R.id.new_task_description);
        this.TaskListLayout = (LinearLayout) findViewById(R.id.task_list_layout);
        this.TaskRecycleList = (RecyclerView) findViewById(R.id.task_recycler);
        this.EmptyLayout = (LinearLayout) findViewById(R.id.empty_notice);
        this.LoadingFrame = (FrameLayout) findViewById(R.id.loading_frame);
        this.TaskListLayout = (LinearLayout) findViewById(R.id.tasks);
        this.TopSettings = (ImageButton) findViewById(R.id.settings_action);

        /* Top Action Listeners */
        this.TopAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimateAddTask(false);
                TopAdd.setEnabled(false);
                TopAdd.setAlpha(0.5F);
            }
        });
        this.TopSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(_intent);
            }
        });
        /* End Top Action Listeners */

        /* Add Task Listeners */
        this.ActionLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimateAddTask(true);
                TopAdd.setEnabled(true);
                TopAdd.setAlpha(1.0F);
                TaskTitle.setText(null);
                TaskDescription.setText(null);
                HideKeyboard(findViewById(R.id.main_content));
            }
        });
        this.ActionRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Storage.AddNewTask(String.valueOf(TaskTitle.getText()), String.valueOf(TaskDescription.getText()));
            }
        });
        this.TaskTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean _can = CanAddTask();
                UpdateRightAction(_can);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        /* End Add Task Listeners */


        /* Animation Listeners */
        this.YScaleIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        this.YScaleOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ModalFrame.setVisibility(View.GONE);
                ModalShade.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        this.FadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        this.ClearButtonOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ClearSearch.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        /* End Animation Listeners */

        /* Search Listeners */
        this.TopSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ActionsLayout.startAnimation(FadeIn);
                    ClearSearch.setVisibility(View.VISIBLE);
                    AnimateSearchBar(false);
                }
            }
        });

        this.TopSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        this.ClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TopSearch.setText(null);
                TopSearch.clearFocus();
                HideKeyboard(view);
                AnimateSearchBar(true);
            }
        });
        /* End Search Listeners */

        UpdateRightAction(false);
        LoadTaskList();
    }

    @Override
    public void OnTaskSaved() {
        ReloadTasks();
    }

    @Override
    public void OnTaskDeleted() {
        ReloadTasks();
    }

    @Override
    public void OnTaskDeleteSelected() {
        TaskRecycleList.setAdapter(this.TaskListAdapter);

        LinearLayout _layout = (LinearLayout) findViewById(R.id.task_list_layout);
        LinearLayout.LayoutParams _params = (LinearLayout.LayoutParams) _layout.getLayoutParams();
        if (_params.weight > 0) {
            AnimateAddTask(true);
            TopAdd.setAlpha(1.0F);
            TopAdd.setEnabled(true);
        }

    }
    /* End Overrides */

    private boolean CanAddTask() {
        return this.TaskTitle.getText().length() > 0;
    }

    private void HideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void ShowKeyboard(View view) {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void AnimateSearchBar(boolean in) {
        LinearLayout _search = (LinearLayout) findViewById(R.id.searchbox);
        LinearLayout _actions = (LinearLayout) findViewById(R.id.top_actions);
        _actions.setAlpha(0.0F);
        ValueAnimator _weight;
        if (in) {
            _weight = ValueAnimator.ofFloat(20.0f, 0.0f);
        } else {
            _weight = ValueAnimator.ofFloat(0.0f, 20.0f);
        }
        _weight.setDuration(250);
        _weight.setInterpolator(new LinearInterpolator());
        _weight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ((LinearLayout.LayoutParams) _actions.getLayoutParams()).weight = (float) animation.getAnimatedValue();
                _actions.requestLayout();
            }

        });
        _weight.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (in) {
                    _actions.setAlpha(1.0F);
                    _actions.startAnimation(FadeIn);
                    ClearSearch.startAnimation(ClearButtonOut);
                    ClearButtonOut.start();
                    FadeIn.setDuration(500);
                    FadeIn.start();
                }
            }
        });
        _weight.start();
    }
    private void AnimateAddTask(boolean in) {
        LinearLayout _add = (LinearLayout) findViewById(R.id.add_task_modal);
        LinearLayout _layout = (LinearLayout) findViewById(R.id.task_list_layout);
        ValueAnimator _weight;
        if (in) {
            _weight = ValueAnimator.ofFloat(1.0f, 0.0f);
        } else {
            _weight = ValueAnimator.ofFloat(0.0f, 1.0f);
        }
        _weight.setDuration(in ? 550 : 250);
        _weight.setInterpolator(in ? new AnticipateInterpolator() : new OvershootInterpolator());
        _weight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ((LinearLayout.LayoutParams) _layout.getLayoutParams()).weight = (float) animation.getAnimatedValue();
                _layout.requestLayout();
                _add.setAlpha((float) animation.getAnimatedValue());
            }

        });
        _weight.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });
        _weight.start();
    }

    private void UpdateRightAction(boolean _enabled) {
        ImageButton _button = (ImageButton) findViewById(R.id.confirm_add);
        _button.setEnabled(_enabled);
        if (_enabled) {
            _button.setAlpha(1.0F);
            return;
        }
        _button.setAlpha(0.5F);
    }

    private void LoadTaskList() {
        try {

            this.Tasks = this.Storage.LoadTaskList();

            if (this.Tasks.getJSONArray("list").length() > 0) {
                this.TaskListAdapter = new TaskAdapter(this.Tasks.getJSONArray("list"), Storage, this);
                this.TaskRecycleList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                this.TaskRecycleList.setAdapter(this.TaskListAdapter);
                this.LoadingFrame.setVisibility(View.GONE);
                this.TaskListLayout.setVisibility(View.VISIBLE);

                this.TaskRecycleList.getRecycledViewPool().clear();
            } else {
                EmptyLayout.setVisibility(View.VISIBLE);
                this.LoadingFrame.setVisibility(View.GONE);
                this.TaskListLayout.setVisibility(View.GONE);
            }


        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "ERROR: Task list unable to load.", Toast.LENGTH_LONG).show();
        }
    }

    private void ReloadTasks() {
        this.TaskListLayout.setVisibility(View.GONE);
        this.LoadingFrame.setVisibility(View.VISIBLE);

        if (((LinearLayout.LayoutParams) findViewById(R.id.task_list_layout).getLayoutParams()).weight == 1.0) {
            this.AnimateAddTask(true);
            this.HideKeyboard(findViewById(R.id.main_content));
            TaskTitle.setText(null);
            TaskDescription.setText(null);
            this.TopAdd.setEnabled(true);
            this.TopAdd.setAlpha(1.0F);
        }
        LoadTaskList();
    }
}