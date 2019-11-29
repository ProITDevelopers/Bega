package com.proitdevelopers.bega.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.model.CartItemProdutos;
import com.proitdevelopers.bega.model.Produtos;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class CartProdutosAdapter extends RecyclerView.Adapter<CartProdutosAdapter.ItemViewHolder> {

    private Context context;
    private List<CartItemProdutos> cartItems = Collections.emptyList();
    private CartProductsAdapterListener listener;

    public CartProdutosAdapter(Context context, CartProductsAdapterListener listener) {
        this.context = context;
        this.listener = listener;

    }

    public void setData(List<CartItemProdutos> cartItems) {
        if (cartItems == null) {
            this.cartItems = Collections.emptyList();
        }

        this.cartItems = cartItems;

        notifyDataSetChanged();
    }




    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_list_item, parent, false);
        return new ItemViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        CartItemProdutos cartItem = cartItems.get(position);
        Produtos product = cartItem.produtos;
        Picasso.with(context).load(product.getImagemProduto()).placeholder(R.drawable.hamburger_placeholder).into(holder.thumbnail);
        holder.name.setText(product.getDescricaoProdutoC());
        holder.price.setText(holder.name.getContext().getString(R.string.lbl_item_price_quantity, context.getString(R.string.price_with_currency, product.getPrecoUnid()), cartItem.quantity));


        if (listener != null)
            holder.btn_remove.setOnClickListener(view -> listener.onCartItemRemoved(position, cartItem));

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnail;
        private TextView name;
        private TextView price;
        private Button btn_remove;


        public ItemViewHolder(View itemView) {
            super(itemView);

            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            name = (TextView) itemView.findViewById(R.id.name);
            price = (TextView) itemView.findViewById(R.id.price);
            btn_remove = (Button) itemView.findViewById(R.id.btn_remove);

        }


    }

    public interface CartProductsAdapterListener {
        void onCartItemRemoved(int index, CartItemProdutos cartItem);
    }
}
