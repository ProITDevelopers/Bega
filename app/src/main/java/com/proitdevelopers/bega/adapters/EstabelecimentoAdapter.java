package com.proitdevelopers.bega.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.model.Estabelecimento;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.proitdevelopers.bega.helper.Common.SPAN_COUNT_ONE;
import static com.proitdevelopers.bega.helper.Common.VIEW_TYPE_BIG;
import static com.proitdevelopers.bega.helper.Common.VIEW_TYPE_SMALL;

public class EstabelecimentoAdapter extends RecyclerView.Adapter<EstabelecimentoAdapter.ItemViewHolder>{

    private List<Estabelecimento> mItems;
    private GridLayoutManager mLayoutManager;
    private Context context;
    private ItemClickListener itemClickListener;

    public EstabelecimentoAdapter(Context context, List<Estabelecimento> items, GridLayoutManager layoutManager) {
        this.context = context;
        mItems = items;
        mLayoutManager = layoutManager;
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

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_BIG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_estabelecimento_list, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_estabelecimento_grid, parent, false);
        }
        return new ItemViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        Estabelecimento item = mItems.get(position);

        holder.title.setText(item.nomeEstabelecimento);

        Picasso.with(context).load(item.logotipo).placeholder(R.drawable.shop_placeholder).into(holder.iv);
//        holder.iv.setImageResource(item.logotipo);
        if (getItemViewType(position) == VIEW_TYPE_BIG) {
            holder.info.setText(item.descricao);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        ImageView iv;
        CircleImageView iv;
        TextView title;
        TextView info;

        ItemViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_TYPE_BIG) {
                iv = (CircleImageView) itemView.findViewById(R.id.image_big);
                title = (TextView) itemView.findViewById(R.id.title_big);
                info = (TextView) itemView.findViewById(R.id.tv_info);
            } else {
                iv = (CircleImageView) itemView.findViewById(R.id.image_small);
                title = (TextView) itemView.findViewById(R.id.title_small);
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
