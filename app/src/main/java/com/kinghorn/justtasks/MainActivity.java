package com.kinghorn.justtasks;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements StorageInterface, TaskListInterface, View.OnClickListener, SliderInterface, TextWatcher, ModalInterface {

    private JSONArray TaskList;
    private StorageManager TaskStorage;
    private SliderView SlideView;
    private AddTask AddView;
    private DetailView DetView;
    private BusySpinner BusyView;
    private static final int ADD_TASK = 0, TASK_DETAILS = 1;

    private int ModalContext;

    /* Overrides */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        TaskStorage = new StorageManager(getApplicationContext(), this);
        SlideView = (SliderView) findViewById(R.id.slide_view);

        AddView = (AddTask) getLayoutInflater().inflate(R.layout.modal_add, null, false);
        DetView = (DetailView) getLayoutInflater().inflate(R.layout.modal_details, null, false);
        ImageButton _add = (ImageButton) findViewById(R.id.add_action);
        _add.setOnClickListener(this);

        ImageButton _settings = (ImageButton) findViewById(R.id.settings_action);
        _settings.setOnClickListener(this);

        LoadTaskList();
        BusyView = (BusySpinner) findViewById(R.id.busy);
        BusyView.setMessage("Loading Tasks...");
        BusyView.show();
    }

    @Override
    public void OnTaskSaved() {
        BusyView.hide();
        SlideView.close(this);
        LoadTaskList();
    }

    @Override
    public void OnTaskDeleted() {
        ReloadTasks();
    }

    @Override
    public void OnTasksCleared() {

    }

    @Override
    public void OnTaskFocused(int id) {
        try {
            JSONObject _task = TaskStorage.LoadTask(id);
            ImageButton _cancel = (ImageButton) DetView.findViewById(R.id.add_cancel);
            _cancel.setOnClickListener(this);
            LinearLayout.LayoutParams _params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            DetView.setLayoutParams(_params);

            Button _close = (Button) DetView.findViewById(R.id.close_details);
            _close.setOnClickListener(this);
            ImageButton _delete = (ImageButton) DetView.findViewById(R.id.details_delete);
            _delete.setOnClickListener(this);
            CheckBox _box = (CheckBox) DetView.findViewById(R.id.task_complete);
            _box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    try {
                        _task.put("completed", b);
                    } catch (JSONException e) {

                    }
                }
            });

            Date _d = new Date(_task.getString("date"));

            TextView _title = (TextView) DetView.findViewById(R.id.details_title);
            TextView _desc = (TextView) DetView.findViewById(R.id.details_description);
            TextView _date = (TextView) DetView.findViewById(R.id.details_date);

            _title.setText(_task.getString("title"));
            _desc.setText(_task.getString("description"));
            _date.setText(TaskUtils.FormatDate(_d.getTime()));

            SlideView.open(this, DetView);
        } catch (JSONException e) {
            Log.d("JT", e.toString());
            Toast.makeText(getApplicationContext(), "ERROR: Unable to open task with id " + String.valueOf(id), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnTaskDeleteSelected(int id) {

    }

    @Override
    public void OnTaskUpdate(int id) {
    }
    /* End Overrides */


    /* TASK ACTIONS */
    private void LoadTaskList() {
        try {
            TaskList = TaskStorage.LoadTaskList().getJSONArray("list");
            SlideView.updateTaskList(TaskList, getApplicationContext(), this);
            Handler _handle = new Handler();
            _handle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    BusyView.hide();
                    if(TaskList.length() > 0) {
                        SlideView.showList();
                    }
                }
            }, 1000);
        } catch(JSONException e) {
            Toast.makeText(getApplicationContext(), "ERROR: Unable to load task list.", Toast.LENGTH_SHORT).show();
        }
    }

    private void ReloadTasks() {

    }

    @Override
    public void onClick(View view) {
       if (view.getId() == R.id.add_action) {
           ImageButton _cancel = AddView.findViewById(R.id.add_cancel);
           ImageButton _confirm = AddView.findViewById(R.id.add_confirm);
           EditText _title = AddView.findViewById(R.id.add_title);

           _title.addTextChangedListener(this);
           _cancel.setOnClickListener(this);
           _confirm.setOnClickListener(this);

           LinearLayout.LayoutParams _params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
           AddView.setLayoutParams(_params);
           ModalContext = ADD_TASK;
           SlideView.open(this, AddView);
       }

       if(view.getId() == R.id.add_cancel) {
           EditText _title = AddView.findViewById(R.id.add_title);
           EditText _desc = AddView.findViewById(R.id.add_desc);
           _title.setText(null);
           _desc.setText(null);
           SlideView.close(this);
       }

       if (view.getId() == R.id.add_confirm) {
           EditText _title = AddView.findViewById(R.id.add_title);
           EditText _desc = AddView.findViewById(R.id.add_desc);
            BusyView.setMessage("Adding new task...");
            BusyView.show();
            TaskStorage.AddNewTask(String.valueOf(_title.getText()), String.valueOf(_desc.getText()));
           _title.setText(null);
           _desc.setText(null);
       }

       if (view.getId() == R.id.settings_action) {
           Intent _intent = new Intent(this, SettingsActivity.class);
           startActivity(_intent);
       }

       if(view.getId() == R.id.close_details) {
           SlideView.close(this);
       }

       if(view.getId() == R.id.details_delete) {
           TaskModal _modal = new TaskModal(getApplicationContext(), findViewById(R.id.main_layout));
           _modal.show("Delete Task?", "Are you sure you want to delete this task? This will erase the task and cannot be undone.", this);
       }
    }

    @Override
    public void OnSliderOpened() {
        switch (ModalContext) {
            case ADD_TASK:
                ImageButton _add = (ImageButton) findViewById(R.id.add_action);
                _add.setEnabled(false);
                _add.setAlpha(0.5F);
        }
    }

    @Override
    public void OnSliderClosed() {
        ImageButton _add = (ImageButton) findViewById(R.id.add_action);
        _add.setEnabled(true);
        _add.setAlpha(1.0F);

        ModalContext = -1;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        EditText _title = AddView.findViewById(R.id.add_title);
        EditText _desc = AddView.findViewById(R.id.add_desc);
        ImageButton _confirm = AddView.findViewById(R.id.add_confirm);

        boolean _allowed = _title.getText().toString().trim().length() > 0;
        _confirm.setAlpha(_allowed ? 1.0F : 0.5F);
        _confirm.setEnabled(_allowed);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void OnCancel() {

    }

    @Override
    public void OnConfirm() {

    }
    /* END TASK ACTIONS */
}