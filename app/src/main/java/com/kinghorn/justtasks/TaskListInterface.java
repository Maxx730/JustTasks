package com.kinghorn.justtasks;

import android.view.View;

public interface TaskListInterface {
    public void OnTaskFocused(TaskAdapter.TaskHolder view);
    public void OnTaskDeleteSelected(int id);
    public void OnTaskUpdate();
}
