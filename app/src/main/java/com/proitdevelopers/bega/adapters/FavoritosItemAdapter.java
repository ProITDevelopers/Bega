package com.proitdevelopers.bega.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.model.FavoritosItem;
import com.proitdevelopers.bega.model.Produtos;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FavoritosItemAdapter extends RecyclerView.Adapter<FavoritosItemAdapter.ItemViewHolder> {

    private Context context;
    private List<FavoritosItem> favoritosItems = Collections.emptyList();
    private FavoritosItemListener listener;

    public FavoritosItemAdapter(Context context, FavoritosItemListener listener) {
        this.context = context;
        this.listener = listener;

    }

    public void setData(List<FavoritosItem> favoritosItems) {
        if (favoritosItems == null) {
            this.favoritosItems = Collections.emptyList();
        }

        this.favoritosItems = favoritosItems;

        notifyDataSetChanged();
    }




    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favoritos_list_item, parent, false);
        return new ItemViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        FavoritosItem favoritosItem = favoritosItems.get(position);
        Produtos product = favoritosItem.produtos;
        holder.name.setText(product.getDescricaoProdutoC());
        holder.estabelecimento.setText(product.getEstabelecimento());


        Picasso.with(context).load(product.getImagemProduto()).fit().centerCrop().placeholder(R.drawable.hamburger_placeholder).into(holder.thumbnail);



        if (listener != null)
            holder.btn_remove.setOnClickListener(view -> listener.onFavoritoItemRemoved(position, favoritosItem));

    }

    @Override
    public int getItemCount() {
        return favoritosItems.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
//        private ImageView thumbnail;
        private CircleImageView thumbnail;
        private TextView name;
        private TextView estabelecimento;
        private Button btn_remove;


        public ItemViewHolder(View itemView) {
            super(itemView);

//            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            thumbnail = (CircleImageView) itemView.findViewById(R.id.thumbnail);
            name = (TextView) itemView.findViewById(R.id.name);
            estabelecimento = (TextView) itemView.findViewById(R.id.estabelecimento);
            btn_remove = (Button) itemView.findViewById(R.id.btn_remove);

        }


    }

    public interface FavoritosAdapterListener {
        void onProductAddedFav(int index, Produtos product);

        void onProductRemovedFromFav(int index, Produtos product);
    }

    public interface FavoritosItemListener {
        void onFavoritoItemRemoved(int index, FavoritosItem cartItem);
    }
}
