package com.kinghorn.justtasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Random;

public class StorageManager {
    private static final String JT_PREFS = "Kinghorn.JT.Prefs";
    private static final String TASKS_INITIALIZED_PREF = "initialized";
    private static final String TASK_PREF = "tasks";
    private SharedPreferences Prefs;
    private StorageInterface Interface;
    public StorageManager(Context context, StorageInterface _interface) {
        this.Prefs = context.getSharedPreferences(JT_PREFS, Context.MODE_PRIVATE);
        this.Interface = _interface;

        if (!this.Prefs.getBoolean(TASKS_INITIALIZED_PREF, false)) {
            try {
                Log.d("JT", "Creating Initial Tasks");
                this.CreateInitialTaskList();
            } catch (JSONException e) {
                Toast.makeText(context, "ERROR: Error initializing task pref.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void CreateInitialTaskList() throws JSONException {
        JSONObject _tasks = new JSONObject();
        JSONArray _list = new JSONArray();

        _tasks.put("list", _list);

        if (this.Prefs != null) {
            SharedPreferences.Editor _edit = this.Prefs.edit();
            _edit.putBoolean(TASKS_INITIALIZED_PREF, true);
            _edit.putString(TASK_PREF, _tasks.toString());
            _edit.apply();
        }
    }

    private boolean IDExists(int id) {
        boolean _exists = false;
        try {
            JSONArray _tasks = LoadTaskList().getJSONArray("list");
            for (int i = 0;i < _tasks.length();i++) {
                JSONObject _task = _tasks.getJSONObject(i);
                if (_task.getInt("id") == id) {
                    _exists = true;
                }
            }
        } catch(JSONException e) {
            Log.d("JT", "ERROR: Error loading task list.");
        }
        return _exists;
    }

    public JSONObject LoadTaskList() throws JSONException {
        if (this.Prefs != null) {
            return new JSONObject(this.Prefs.getString(TASK_PREF, "{list:[]}"));
        } else {
            return new JSONObject("{error:\"storage prefs not defined\"}");
        }
    }
    public void SaveTaskList(JSONObject tasks) {
        if(this.Prefs != null) {
            SharedPreferences.Editor _edit = this.Prefs.edit();
            _edit.putString(TASK_PREF, tasks.toString());
            _edit.apply();
        } else {
            Log.d("JT", "ERROR: Unable to save tasks, preferences object does not exist.");
        }
    }

    public void AddNewTask(String title, String description) {
        try {
            JSONObject _task = new JSONObject();
            JSONObject _tasks = LoadTaskList();
            int _id = new Random().nextInt(1000);

            while(IDExists(_id)) {
                _id = new Random().nextInt(1000);
            }

            _task.put("title", title);
            _task.put("description", description);
            _task.put("date", new Date());
            _task.put("completed", false);
            _task.put("id", _id);

            _tasks.getJSONArray("list").put(_task);
            SaveTaskList(_tasks);
            if (this.Interface != null) {
                this.Interface.OnTaskSaved();
            }
        } catch(JSONException e) {
            Log.d("JT", "ERROR: Error adding new task to storage.");
        }
    }

    public void DeleteTask(int id) {
        try {
            JSONArray _tasks = LoadTaskList().getJSONArray("list");
            for(int i = 0;i < _tasks.length();i++) {
                JSONObject _task = _tasks.getJSONObject(i);
                if (_task.getInt("id") == id) {
                    _tasks.remove(i);
                    SaveTaskList(new JSONObject("{list:" + _tasks.toString() + "}"));
                }
            }
            if (this.Interface != null) {
                this.Interface.OnTaskDeleted();
            }
        } catch(JSONException e) {
            Log.e("JT", "ERROR: Error deleting task with id:" + String.valueOf(id));
        }
    }
}
