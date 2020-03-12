package com.proitdevelopers.bega.utilsClasses;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.model.CartItemProdutos;
import com.proitdevelopers.bega.model.FacturaItens;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class CheckOutItemsListView extends ConstraintLayout {

//    @BindView(R.id.container)
    LinearLayout layoutContainer;

    private List<CartItemProdutos> items = new ArrayList<>();
    private LayoutInflater inflater;

    public CheckOutItemsListView(Context context) {
        super(context);
        init(context, null);
    }

    public CheckOutItemsListView(Context context, @Nullable AttributeSet attrs) {
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

    public void setOrderItems(List<CartItemProdutos> items) {
        this.items.clear();
        this.items.addAll(items);

        renderItems();
    }

    private void renderItems() {
        layoutContainer.removeAllViews();
        for (CartItemProdutos item : items) {
            View view = inflater.inflate(R.layout.view_checkout_items_list_row, null);
            if (item.produtos != null) {

                Picasso.with(getContext())
                        .load(item.produtos.getImagemProduto())
                        .placeholder(R.drawable.hamburger_placeholder)
                        .into((CircleImageView) view.findViewById(R.id.thumbnail));

//                ((CircleImageView) view.findViewById(R.id.thumbnail)).setImageResource(R.drawable.snack_food);

                ((TextView) view.findViewById(R.id.name)).setText(item.produtos.getDescricaoProdutoC());
                ((TextView) view.findViewById(R.id.estabelecimento)).setText(item.produtos.getEstabelecimento());

                ((TextView) view.findViewById(R.id.price)).setText(getContext().getString(R.string.lbl_item_price_quantity, getContext().getString(R.string.price_with_currency, item.produtos.getPrecoUnid())+ " AKZ", item.quantity));


            }
            layoutContainer.addView(view);
        }
    }

}
