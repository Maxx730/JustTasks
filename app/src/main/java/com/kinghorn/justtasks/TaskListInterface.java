package com.kinghorn.justtasks;

public interface TaskListInterface {
    public void OnTaskFocused(int adapterPosition);
    public void OnTaskDeleteSelected(int id);
    public void OnTaskUpdate(int id);
}
