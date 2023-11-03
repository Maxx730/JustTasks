package com.kinghorn.justtasks;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, ModalInterface, StorageInterface {

    private BusySpinner BusyView;
    private StorageManager Storage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        BusyView = (BusySpinner) findViewById(R.id.busy);
        Storage = new StorageManager(getApplicationContext(), this);

        Button _clear = (Button) findViewById(R.id.clear_data);
        _clear.setOnClickListener(this);

        ImageButton _back = (ImageButton) findViewById(R.id.back);
        _back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.clear_data) {
            TaskModal _modal = new TaskModal(getApplicationContext(), findViewById(R.id.settings_frame));
            _modal.show("Clear Data", "Are you sure you want to clear saved data? This cannot be undone.", this);
        }

        if (view.getId() == R.id.back) {
            finish();
        }
    }

    @Override
    public void OnCancel() {

    }

    @Override
    public void OnConfirm() {
        BusyView.setMessage("Deleting data...");
        BusyView.show();

        Handler _handle = new Handler();
        _handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Storage.ClearData();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "ERROR: Error deleting task data.", Toast.LENGTH_SHORT).show();
                }
                BusyView.hide();
            }
        },1000);
    }

    @Override
    public void OnTaskSaved() {

    }

    @Override
    public void OnTaskDeleted() {

    }

    @Override
    public void OnTasksCleared() {
        Toast.makeText(getApplicationContext(), "SUCCESS: Data cleared.", Toast.LENGTH_SHORT).show();
    }
}