package com.proitdevelopers.bega;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TimerInfoBar extends RelativeLayout implements View.OnClickListener {

    private TimerInfoBarListener listener;

    TextView txt_temporizador,txt_timer;

    RelativeLayout relativeLayout;

    public TimerInfoBar(Context context) {
        super(context);
        init(context, null);
    }

    public TimerInfoBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_timer_info_bar, null);
        txt_temporizador = view.findViewById(R.id.txt_timer);
        txt_timer = view.findViewById(R.id.txt_timer);
        relativeLayout = view.findViewById(R.id.container);
        relativeLayout.setOnClickListener(this);
        addView(view);
    }

    public void setListener(TimerInfoBarListener listener) {
        this.listener = listener;
    }


    public void setData(String time) {
        txt_timer.setText(getContext().getString(R.string.timer_info_bar, time));
    }

    @Override
    public void onClick(View view) {
        if (listener != null)
            listener.onClick();
    }

    public interface TimerInfoBarListener {
        void onClick();
    }
}
