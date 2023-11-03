package com.kinghorn.justtasks;

import org.json.JSONObject;

public interface StorageInterface {
    public void OnTaskSaved();
    public void OnTaskDeleted();

    public void OnTasksCleared();
}
