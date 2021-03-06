package com.proitdevelopers.bega.utilsClasses;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.proitdevelopers.bega.R;

public class CartInfoBar extends RelativeLayout implements View.OnClickListener {

    private CartInfoBarListener listener;

    TextView cartInfo,cart_total;

    RelativeLayout relativeLayout;

    public CartInfoBar(Context context) {
        super(context);
        init(context, null);
    }

    public CartInfoBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_cart_info_bar, null);
        cartInfo = view.findViewById(R.id.cart_price);
        cart_total = view.findViewById(R.id.cart_total);
        relativeLayout = view.findViewById(R.id.container);
        relativeLayout.setOnClickListener(this);
        addView(view);
    }

    public void setListener(CartInfoBarListener listener) {
        this.listener = listener;
    }



//    public void setData(int itemCount, String price) {
//        cartInfo.setText(getContext().getString(R.string.cart_info_bar_data, itemCount, price));
//    }

    public void setData(int itemCount, String price) {
        cartInfo.setText(getContext().getString(R.string.cart_info_bar_items, itemCount));
        cart_total.setText(getContext().getString(R.string.cart_info_bar_total, Float.parseFloat(price)) + " AKZ");
    }

    @Override
    public void onClick(View view) {
        if (listener != null)
            listener.onClick();
    }

    public interface CartInfoBarListener {
        void onClick();
    }
}
