package com.kinghorn.justtasks;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
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
    private int FocusedTask = -1;
    private int ConfirmTask = -1;
    private static final int TASK_OPEN_SIZE = 1200;
    private static final int TASK_CLOSE_SIZE = 170;

    private ValueAnimator TaskDetailAnimator;


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
            holder.GetTaskCheckbox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {

                    } else {

                    }
                }
            });
            holder.GetTaskDetails().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FocusedTask = holder.getAdapterPosition();
                    if (TaskInterface != null) {
                        TaskInterface.OnTaskUpdate();
                    }
                }
            });
            ImageButton[] _actions = holder.GetConfirmButtons();
            _actions[0].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            _actions[1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            if (holder.getAdapterPosition() == FocusedTask) {
                holder.UpdateDetailButtons(false);
                ToggleTaskItem(holder, true);
            }
        } catch (JSONException e) {
            Log.d("JT", "ERROR: Error parsing Task #" + String.valueOf(position));
        }
    }

    @Override
    public int getItemCount() {
        return Tasks.length();
    }

    public void ToggleTaskItem(TaskHolder holder, boolean open) {
        TaskDetailAnimator = open ? ValueAnimator.ofInt(TASK_CLOSE_SIZE, TASK_OPEN_SIZE) : ValueAnimator.ofInt(TASK_OPEN_SIZE, TASK_CLOSE_SIZE);
        TaskDetailAnimator.setDuration(250);
        TaskDetailAnimator.setInterpolator(new OvershootInterpolator());
        TaskDetailAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
                holder.GetTaskFrame().getLayoutParams().height = (int) TaskDetailAnimator.getAnimatedValue();
                holder.GetTaskFrame().requestLayout();
            }
        });
        TaskDetailAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                TaskDetailAnimator = null;
                holder.GetTaskDetailLayout().setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        TaskDetailAnimator.start();
    }

    public TaskAdapter(JSONArray tasks, StorageManager manager, TaskListInterface inter) {
        Tasks = tasks;
        Manager = manager;
        TaskInterface = inter;
    }

    public static class TaskHolder extends RecyclerView.ViewHolder {
        private TextView TaskTitle, TaskDate;
        private ImageButton TaskTrash, ConfirmCancel, ConfirmConfirm, TaskDetails;
        private CheckBox TaskCheckbox;
        private LinearLayout ConfirmDelete, TaskDetailsLayout;
        private FrameLayout TaskFrame;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            TaskTitle = itemView.findViewById(R.id.task_title);
            TaskDate = itemView.findViewById(R.id.task_date);
            TaskCheckbox = itemView.findViewById(R.id.task_complete);
            TaskTrash = itemView.findViewById(R.id.task_trash);
            ConfirmDelete = itemView.findViewById(R.id.confirm_task_delete);
            TaskFrame = itemView.findViewById(R.id.task_list_item);
            ConfirmCancel = itemView.findViewById(R.id.item_confirm_cancel);
            ConfirmConfirm = itemView.findViewById(R.id.item_confirm_confirm);
            TaskDetails = itemView.findViewById(R.id.task_details);
            TaskDetailsLayout = itemView.findViewById(R.id.task_edit);
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

        public LinearLayout GetConfirmDelete() {return ConfirmDelete;}

        public FrameLayout GetTaskFrame(){return TaskFrame;}

        public LinearLayout GetTaskDetailLayout(){
            return TaskDetailsLayout;
        }

        public ImageButton[] GetConfirmButtons() {
            return new ImageButton[] {ConfirmCancel, ConfirmConfirm};
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
