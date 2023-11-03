package com.kinghorn.justtasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;


public class TaskModal {
    private Context ModalContext;
    private ViewGroup RootView;
    private View ModalLayout;
    public TaskModal(Context context, ViewGroup root) {
        this.ModalContext = context;
        this.RootView = root;
    }

    public void show(String title, String description, ModalInterface inter) {
        LayoutInflater _inflate = (LayoutInflater) ModalContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ModalLayout = (View) _inflate.inflate(R.layout.modal_layout, (ViewGroup) RootView, true);

        TextView _title = (TextView) ModalLayout.findViewById(R.id.modal_title);
        TextView _description = (TextView) ModalLayout.findViewById(R.id.modal_message);
        Button _cancel = (Button) ModalLayout.findViewById(R.id.modal_cancel);
        Button _confirm = (Button) ModalLayout.findViewById(R.id.modal_confirm);

        _title.setText(title);
        _description.setText(description);

        _cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inter.OnCancel();
                hide();
            }
        });

        _confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inter.OnConfirm();
                hide();
            }
        });
    }

    public void hide() {
        RootView.removeView(RootView.findViewById(R.id.modal_shade));
        RootView.invalidate();
        RootView.requestLayout();
    }
}
