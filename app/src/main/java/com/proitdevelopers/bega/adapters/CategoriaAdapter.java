package com.proitdevelopers.bega.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.model.CartItemProdutos;
import com.proitdevelopers.bega.model.Categoria;
import com.proitdevelopers.bega.model.Produtos;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ItemViewHolder> {

    private Context context;
    private List<Categoria> categoriaList;
    private ItemClickListener itemClickListener;

    public CategoriaAdapter(Context context, List<Categoria> categoriaList) {
        this.context = context;
        this.categoriaList = categoriaList;

    }






    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_layout, parent, false);
        return new ItemViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        Categoria categoria = categoriaList.get(position);

//        Picasso.with(context).load(categoria.getImagemCategoria()).placeholder(R.drawable.hamburger_placeholder).into(holder.category_image);

        Picasso.with(context)
                .load(categoria.getImagemCategoria())
                .placeholder(R.drawable.hamburger_placeholder)
                .error(R.drawable.hamburger_placeholder)
                .into(holder.category_image, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progress_bar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.progress_bar.setVisibility(View.GONE);
                    }
                });
        holder.category_name.setText(categoria.getNomeCategoria());

    }

    @Override
    public int getItemCount() {
        return categoriaList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar progress_bar;
        private ImageView category_image;
        private TextView category_name;



        public ItemViewHolder(View itemView) {
            super(itemView);

            progress_bar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            category_image = (ImageView) itemView.findViewById(R.id.category_image);
            category_name = (TextView) itemView.findViewById(R.id.category_name);


            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) itemClickListener.onClick(view, getAdapterPosition());
        }


    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


}
