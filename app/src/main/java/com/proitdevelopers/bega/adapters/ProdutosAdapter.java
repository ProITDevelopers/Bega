package com.proitdevelopers.bega.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.model.CartItemProdutos;
import com.proitdevelopers.bega.model.Produtos;
import com.squareup.picasso.Picasso;

import java.io.CharArrayReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import io.realm.RealmResults;

public class ProdutosAdapter extends RecyclerView.Adapter<ProdutosAdapter.ItemViewHolder> {

    public static final int SPAN_COUNT_ONE = 1;
    public static final int SPAN_COUNT_THREE = 2;

    private static final int VIEW_TYPE_SMALL = 1;
    private static final int VIEW_TYPE_BIG = 2;

    private Context context;
    private List<Produtos> produtosList;
//    private RealmResults<Produtos> produtosList;
    private GridLayoutManager mLayoutManager;
    private RealmResults<CartItemProdutos> cartItems;

    private ProductsAdapterListener listener;

    private ItemClickListener itemClickListener;

    public ProdutosAdapter(Context context, List<Produtos> produtosList, ProductsAdapterListener listener, GridLayoutManager layoutManager) {
        this.context = context;
        this.produtosList = produtosList;
        this.listener = listener;
        this.mLayoutManager = layoutManager;

    }

    @Override
    public int getItemViewType(int position) {
        int spanCount = mLayoutManager.getSpanCount();
        if (spanCount == SPAN_COUNT_ONE) {
            return VIEW_TYPE_BIG;
        } else {
            return VIEW_TYPE_SMALL;
        }
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_BIG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto_list, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto_grid, parent, false);
        }
        return new ItemViewHolder(view, viewType);
    }



    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        Produtos produto = produtosList.get(position);


        if (produto!=null){

            Picasso.with(context).load(produto.getImagemProduto()).placeholder(R.drawable.hamburger_placeholder).into(holder.thumbnail);

            holder.name.setText(produto.getDescricaoProdutoC());
//        holder.descricao.setText(produto.getDescricaoProduto());
//        holder.price.setText(produto.getPrecoUnid());


            String preco = String.valueOf(produto.getPrecoUnid());

            holder.price.setText(context.getString(R.string.price_with_currency, Float.parseFloat(preco)));


//        holder.iv.setImageResource(item.logotipo);
            if (getItemViewType(position) == VIEW_TYPE_BIG) {
                holder.descricao.setText(produto.getDescricaoProduto());
            }

            holder.btn_addCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    listener.onProductAddedCart(position, produto);

                }
            });

            holder.ic_add.setOnClickListener(view -> {
                listener.onProductAddedCart(position, produto);
            });

            holder.ic_remove.setOnClickListener(view -> {
                listener.onProductRemovedFromCart(position, produto);
            });

            if (cartItems != null) {
                CartItemProdutos cartItem = cartItems.where().equalTo("produtos.idProduto", produto.getIdProduto()).findFirst();
                if (cartItem != null) {
                    holder.product_count.setText(String.valueOf(cartItem.quantity));
                    holder.ic_add.setVisibility(View.VISIBLE);
                    holder.ic_remove.setVisibility(View.VISIBLE);
                    holder.product_count.setVisibility(View.VISIBLE);
                    holder.btn_addCart.setEnabled(false);
                } else {
                    holder.product_count.setText(String.valueOf(0));
                    holder.ic_add.setVisibility(View.GONE);
                    holder.ic_remove.setVisibility(View.GONE);
                    holder.product_count.setVisibility(View.GONE);
                    holder.btn_addCart.setEnabled(true);
                }
            }

        }


    }

    @Override
    public int getItemCount() {
        return produtosList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView thumbnail;
        private TextView name;
        private TextView descricao;
        private TextView price;
        private Button btn_addCart;

        private ImageView ic_add,ic_remove;
        private TextView product_count;

        public ItemViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_TYPE_BIG) {
                thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
                name = (TextView) itemView.findViewById(R.id.name);
                descricao = (TextView) itemView.findViewById(R.id.descricao);
                price = (TextView) itemView.findViewById(R.id.price);
                btn_addCart = (Button) itemView.findViewById(R.id.btn_addCart);

                ic_add = (ImageView) itemView.findViewById(R.id.ic_add);
                ic_remove = (ImageView) itemView.findViewById(R.id.ic_remove);
                product_count = (TextView) itemView.findViewById(R.id.product_count);

            } else {
                thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
                name = (TextView) itemView.findViewById(R.id.name);
                price = (TextView) itemView.findViewById(R.id.price);
                btn_addCart = (Button) itemView.findViewById(R.id.btn_addCart);
            }


            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
//            thumbnail.setOnClickListener(this);
//            btn_addCart.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    Snackbar.make(view, produtosList.get(getAdapterPosition()).getDescricaoProdutoC()+" adicionado ao Carrinho!", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//
//                }
//            });
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) itemClickListener.onClick(view, getAdapterPosition());
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setCartItems(RealmResults<CartItemProdutos> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    public void updateItem(int position, RealmResults<CartItemProdutos> cartItems) {
        this.cartItems = cartItems;
        notifyItemChanged(position);
    }

    public interface ProductsAdapterListener {
        void onProductAddedCart(int index, Produtos product);

        void onProductRemovedFromCart(int index, Produtos product);
    }
}
