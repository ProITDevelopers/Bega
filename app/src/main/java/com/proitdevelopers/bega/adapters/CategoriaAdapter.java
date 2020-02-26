package com.proitdevelopers.bega.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.model.Categoria;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.proitdevelopers.bega.helper.Common.SPAN_COUNT_ONE;
import static com.proitdevelopers.bega.helper.Common.VIEW_TYPE_BIG;
import static com.proitdevelopers.bega.helper.Common.VIEW_TYPE_SMALL;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ItemViewHolder> {


    private GridLayoutManager mLayoutManager;


    private Context context;
    private List<Categoria> categoriaList;
    private ItemClickListener itemClickListener;

    public CategoriaAdapter(Context context, List<Categoria> categoriaList,GridLayoutManager layoutManager) {
        this.context = context;
        this.categoriaList = categoriaList;
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
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_category_layout, parent, false);
//        return new ItemViewHolder(itemView);

        View view;
        if (viewType == VIEW_TYPE_BIG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_layout, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_layout_grid, parent, false);
        }

        return new ItemViewHolder(view, viewType);

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



        public ItemViewHolder(View itemView, int viewType) {
            super(itemView);

//            progress_bar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
//            category_image = (ImageView) itemView.findViewById(R.id.category_image);
//            category_name = (TextView) itemView.findViewById(R.id.category_name);

            if (viewType == VIEW_TYPE_BIG) {
                progress_bar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
                category_image = (ImageView) itemView.findViewById(R.id.category_image);
                category_name = (TextView) itemView.findViewById(R.id.category_name);
            } else {
                progress_bar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
                category_image = (ImageView) itemView.findViewById(R.id.category_image);
                category_name = (TextView) itemView.findViewById(R.id.category_name);
            }


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
