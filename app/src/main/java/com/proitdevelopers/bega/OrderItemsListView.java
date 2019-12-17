package com.proitdevelopers.bega;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.proitdevelopers.bega.model.FacturaItens;

import java.util.ArrayList;
import java.util.List;



public class OrderItemsListView extends ConstraintLayout {

//    @BindView(R.id.container)
    LinearLayout layoutContainer;

    private List<FacturaItens> items = new ArrayList<>();
    private LayoutInflater inflater;

    public OrderItemsListView(Context context) {
        super(context);
        init(context, null);
    }

    public OrderItemsListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.layout_order_items_list_view, this);
//        ButterKnife.bind(this);
        if (inflater == null) {
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }
        layoutContainer = findViewById(R.id.container);





    }

    public void setOrderItems(List<FacturaItens> items) {
        this.items.clear();
        this.items.addAll(items);

        renderItems();
    }

    private void renderItems() {
        layoutContainer.removeAllViews();
        for (FacturaItens item : items) {
            View view = inflater.inflate(R.layout.view_order_items_list_row, null);
            if (item.produto != null) {

                ((TextView) view.findViewById(R.id.textView)).setText("Item:");
                ((TextView) view.findViewById(R.id.textViewQty)).setText("Qtd:");

                ((TextView) view.findViewById(R.id.txtProduto)).setText(item.produto);
                ((TextView) view.findViewById(R.id.txtQuantidade)).setText(String.valueOf(item.quantidade));

            }
            layoutContainer.addView(view);
        }
    }

}
