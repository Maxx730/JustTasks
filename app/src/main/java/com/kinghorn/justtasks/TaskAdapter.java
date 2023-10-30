package com.kinghorn.justtasks;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {
    private JSONArray Tasks;
    private StorageManager Manager;
    private TaskListInterface TaskInterface;
    private int ConfirmTask = -1;
    private static final int TASK_OPEN_SIZE = 1200;
    private static final int TASK_CLOSE_SIZE = 170;
    private ValueAnimator TaskDetailAnimator;
    private Context TaskContext;

    private static final int ACTION_SAVE = 0;
    private static final int ACTION_DELETE = 1;


    // Task Editing Values
    private int FocusedTask = -1;
    private JSONObject FocusedStartData, FocusedEndData;


    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context _context = parent.getContext();
        View _view = LayoutInflater.from(_context).inflate(R.layout.task_list_item, parent, false);
        return new TaskHolder(_view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
        try {
            JSONObject _task = Tasks.getJSONObject(position);
            SimpleDateFormat _format = new SimpleDateFormat("MMMM, dd, yyyy");
            Date _date = new Date(_task.getString("date"));
            String[] _parts = _format.format(_date).split(",");

            holder.GetTitle().setText(_task.getString("title"));
            holder.GetDate().setText(_parts[0] + " " + _parts[1].trim() + TaskUtils.GetDateSuffix(Integer.valueOf(_parts[1].trim())) + ", " + _parts[2]);
            holder.GetTaskCheckbox().setChecked(_task.getBoolean("completed"));
            holder.GetTaskCheckbox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    try {
                        _task.put("completed", b);
                        Manager.UpdateTask(_task);
                    } catch (JSONException e) {

                    }
                }
            });
            holder.GetTaskDetails().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FocusedTask = holder.getAdapterPosition();
                    if (TaskInterface != null) {
                        TaskInterface.OnTaskUpdate();
                        TaskInterface.OnTaskFocused(holder);
                    }
                }
            });


            if (holder.getAdapterPosition() == FocusedTask) {

            }
        } catch (JSONException e) {
            Log.d("JT", "ERROR: Error parsing Task #" + String.valueOf(position));
        }
    }

    @Override
    public int getItemCount() {
        return Tasks.length();
    }

    public void ToggleTaskItem(TaskHolder holder, boolean open, JSONObject _task) {
        int _convertedHeight = TaskUtils.ConvertDPtoPixel(400, TaskContext);
        int _convertedMargin = TaskUtils.ConvertDPtoPixel(8, TaskContext);
        FrameLayout.LayoutParams _params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        _params.topMargin = _convertedMargin;

        EditText[] _edits = holder.GetTaskEditFields();

        try {
            FocusedStartData = _task;
            FocusedEndData = new JSONObject(_task.toString());

            // Update edit fields.
            _edits[0].setText(_task.getString("title"));
            _edits[1].setText(_task.getString("description"));
            _edits[0].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        FocusedEndData.put("title", charSequence.toString());
                        UpdateConfirmationButtons(holder, ACTION_SAVE);
                    } catch(JSONException e) {

                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            _edits[1].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        FocusedEndData.put("description", charSequence.toString());
                        UpdateConfirmationButtons(holder, ACTION_SAVE);
                    } catch(JSONException e) {

                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        } catch(JSONException e) {
            Toast.makeText(TaskContext, "ERROR: Error opening task.", Toast.LENGTH_LONG).show();
        }

        holder.GetTaskTrash().setVisibility(View.VISIBLE);
        holder.GetTaskTrash().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TaskInterface != null) {
                    try {
                        TaskInterface.OnTaskDeleteSelected(_task.getInt("id"));
                    } catch (JSONException e) {

                    }
                }
            }
        });

        holder.GetTaskDetails().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FocusedTask = -1;
                TaskInterface.OnTaskUpdate();
            }
        });
        holder.GetTaskFrame().setLayoutParams(_params);
        holder.GetTaskDetailLayout().setVisibility(View.VISIBLE);
        ToggleTaskConfirmation(holder, true);
    }

    public void ToggleTaskConfirmation(TaskHolder holder, boolean open) {
        holder.GetConfirmLayout().setVisibility(open ? View.VISIBLE : View.GONE);
    }

    public void UpdateConfirmationButtons(TaskHolder holder, int action_type) {
        boolean _changed = TaskUtils.HasChanges(FocusedStartData, FocusedEndData);
        holder.GetTaskSave().setEnabled(_changed);
        holder.GetTaskSave().setAlpha(_changed ? 1.0F : 0.5F);

        if (_changed) {
            holder.GetTaskSave().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    
                }
            });
        }
    }

    public TaskAdapter(JSONArray tasks, StorageManager manager, TaskListInterface inter, Context context) {
        Tasks = tasks;
        Manager = manager;
        TaskInterface = inter;
        TaskContext = context;
    }

    private void ShrinkTask(TaskHolder holder) {

    }

    public static class TaskHolder extends RecyclerView.ViewHolder {
        private TextView TaskTitle, TaskDate;
        private ImageButton TaskTrash, TaskDetails, TaskSave;
        private CheckBox TaskCheckbox;
        private LinearLayout ConfirmLayout, TaskDetailsLayout;
        private FrameLayout TaskFrame;
        private EditText TaskEditTitle, TaskEditDescription;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            TaskTitle = itemView.findViewById(R.id.task_title);
            TaskDate = itemView.findViewById(R.id.task_date);
            TaskCheckbox = itemView.findViewById(R.id.task_complete);
            TaskTrash = itemView.findViewById(R.id.task_trash);
            ConfirmLayout = itemView.findViewById(R.id.task_confirm);
            TaskFrame = itemView.findViewById(R.id.task_list_item);
            TaskDetails = itemView.findViewById(R.id.task_details);
            TaskDetailsLayout = itemView.findViewById(R.id.task_edit);
            TaskEditTitle = itemView.findViewById(R.id.task_edit_title);
            TaskEditDescription = itemView.findViewById(R.id.task_edit_description);
            TaskSave = itemView.findViewById(R.id.task_save);
        }

        public TextView GetTitle() {
            return TaskTitle;
        }

        public TextView GetDate() {
            return TaskDate;
        }

        public ImageButton GetTaskTrash() {
            return TaskTrash;
        }

        public CheckBox GetTaskCheckbox() {
            return TaskCheckbox;
        }

        public LinearLayout GetConfirmLayout() {return ConfirmLayout;}

        public FrameLayout GetTaskFrame(){return TaskFrame;}

        public LinearLayout GetTaskDetailLayout(){
            return TaskDetailsLayout;
        }

        public EditText[] GetTaskEditFields() {
            return new EditText[] {TaskEditTitle, TaskEditDescription};
        }

        public ImageButton GetTaskSave() {
            return TaskSave;
        }

        public ImageButton GetTaskDetails() {
            return TaskDetails;
        }
        public void UpdateDetailButtons(boolean down) {
            if (down){
                GetTaskDetails().setImageResource(R.drawable.chevron_down);
            } else {
                GetTaskDetails().setImageResource(R.drawable.chevron_up);
            }
        }
    }
}
