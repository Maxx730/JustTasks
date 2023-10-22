package com.kinghorn.justtasks;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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

    private ValueAnimator ConfirmAnimator;


    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View _view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item, parent, false);
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

            holder.GetTaskFrame().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        ConfirmTask = holder.getAdapterPosition();
                        if (TaskInterface != null) {
                            TaskInterface.OnTaskDeleteSelected();
                        }
                        return true;
                    }
                    return false;
                }
            });
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

            ImageButton[] _actions = holder.GetConfirmButtons();
            _actions[0].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CloseTaskItemConfirm(holder);
                    ConfirmTask = -1;
                }
            });
            _actions[1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Manager.DeleteTask(_task.getInt("id"));
                        ConfirmTask = -1;
                        CloseTaskItemConfirm(holder);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            if (position == ConfirmTask && ConfirmAnimator == null) {
                OpenTaskItemConfirm(holder);
            }

            holder.GetConfirmDelete().setVisibility(position == ConfirmTask ? View.VISIBLE : View.GONE);
            holder.GetTaskTrash().setEnabled(position != ConfirmTask);
            holder.GetTaskTrash().setAlpha(position == ConfirmTask ? 0.5F : 1.0F);
        } catch (JSONException e) {
            Log.d("JT", "ERROR: Error parsing Task #" + String.valueOf(position));
        }
    }

    @Override
    public int getItemCount() {
        return Tasks.length();
    }

    public void OpenTaskItemConfirm(TaskHolder holder) {
        ConfirmAnimator = ValueAnimator.ofInt(170, 540);
        ConfirmAnimator.setDuration(250);
        ConfirmAnimator.setInterpolator(new OvershootInterpolator());
        ConfirmAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
                holder.GetTaskFrame().getLayoutParams().height = (int) ConfirmAnimator.getAnimatedValue();
                holder.GetTaskFrame().requestLayout();
            }
        });
        ConfirmAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                ConfirmAnimator = null;
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        ConfirmAnimator.start();
    }

    public void CloseTaskItemConfirm(TaskHolder holder) {
        ConfirmAnimator = ValueAnimator.ofInt(540, 170);
        ConfirmAnimator.setDuration(250);
        ConfirmAnimator.setInterpolator(new AnticipateInterpolator());
        ConfirmAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
                holder.GetTaskFrame().getLayoutParams().height = (int) ConfirmAnimator.getAnimatedValue();
                holder.GetTaskFrame().requestLayout();
            }
        });
        ConfirmAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                ConfirmAnimator = null;
                if (TaskInterface != null) {
                    TaskInterface.OnTaskDeleteSelected();
                }
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        ConfirmAnimator.start();
    }

    public TaskAdapter(JSONArray tasks, StorageManager manager, TaskListInterface inter) {
        Tasks = tasks;
        Manager = manager;
        TaskInterface = inter;
    }

    public static class TaskHolder extends RecyclerView.ViewHolder {
        private TextView TaskTitle, TaskDate;
        private ImageButton TaskTrash, ConfirmCancel, ConfirmConfirm;
        private CheckBox TaskCheckbox;
        private LinearLayout ConfirmDelete;
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

        public ImageButton[] GetConfirmButtons() {
            return new ImageButton[] {ConfirmCancel, ConfirmConfirm};
        }
    }
}
