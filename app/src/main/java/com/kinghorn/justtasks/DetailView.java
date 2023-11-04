package com.kinghorn.justtasks;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class DetailView extends LinearLayout {

    private boolean EditMode = false;
    public DetailView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void UpdateDetails(JSONObject task, ModalInterface inter) throws JSONException {
        Date _d = new Date(task.getString("date"));

        TextView _title = (TextView) findViewById(R.id.details_title);
        TextView _desc = (TextView) findViewById(R.id.details_description);
        TextView _date = (TextView) findViewById(R.id.details_date);

        _title.setText(task.getString("title"));
        _desc.setText(task.getString("description"));
        _date.setText(TaskUtils.FormatDate(_d.getTime()));
    }
}
