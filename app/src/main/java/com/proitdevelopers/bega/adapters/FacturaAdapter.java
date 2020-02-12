package com.proitdevelopers.bega.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.proitdevelopers.bega.utilsClasses.OrderItemsListView;
import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.helper.Utils;
import com.proitdevelopers.bega.model.Factura;

import java.util.List;

public class FacturaAdapter extends RecyclerView.Adapter<FacturaAdapter.FacturaViewHolder>{

    private static final String TAG = "FacturaAdapter";
    private Context context;
    private List<Factura> facturaList;
    private ItemClickListener itemClickListener;



    public FacturaAdapter(Context context, List<Factura> facturaList) {
        this.facturaList = facturaList;
        this.context = context;
    }

    @NonNull
    @Override
    public FacturaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedidos, parent, false);
        return new FacturaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacturaViewHolder holder, int position) {

        Factura factura = facturaList.get(position);
        if (factura != null){

            holder.txtFacturaId.setText(context.getString(R.string.order_id, String.valueOf(factura.idFactura)));
            holder.txtDataPedido.setText(Utils.getOrderTimestamp(factura.dataPedido));
            holder.orderItems.setOrderItems(factura.itens);
            holder.txtEstado.setText(factura.estado);

            if (factura.estadoPagamento.equals("Pagamento Efectuado")){
                holder.txtEstadoPagamento.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.txtEstadoPagamento.setBackgroundColor(ContextCompat.getColor(context, R.color.colorTextGreen));
            }else{
                holder.txtEstadoPagamento.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.txtEstadoPagamento.setBackgroundColor(ContextCompat.getColor(context, R.color.colorText));
            }
            holder.txtEstadoPagamento.setText(factura.estadoPagamento);

            int imageWallet = R.drawable.ic__wallet_orange_24dp;
            int imageTPA = R.drawable.ic_bi_card_orange_24dp;

            if (factura.metododPagamento!=null){
                if(factura.metododPagamento.equals("WalletProit-consulting")){
                    holder.imgMetododPagamento.setImageResource(imageWallet);
                } else {
                    holder.imgMetododPagamento.setImageResource(imageTPA);
                }
            }

            holder.txtMetododPagamento.setText(factura.metododPagamento);

            String total = String.valueOf(factura.total);
            holder.txtTotal.setText(context.getString(R.string.price_with_currency, Float.parseFloat(total))+ " AKZ");

            holder.txtDataPagamento.setText(Utils.getOrderTimestamp(factura.dataPagamento));



            boolean isExpanded = facturaList.get(position).isExpanded();
            holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);


        }



    }

    @Override
    public int getItemCount() {
        return facturaList.size();
    }

    class FacturaViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        private static final String TAG = "FacturaViewHolder";

        ConstraintLayout expandableLayout;
        TextView txtFacturaId, txtDataPedido;
        OrderItemsListView orderItems;
        ImageView imgMetododPagamento;
        TextView txtEstado, txtEstadoPagamento, txtMetododPagamento, txtTotal, txtDataPagamento;

        public FacturaViewHolder(@NonNull final View itemView) {
            super(itemView);

            expandableLayout = itemView.findViewById(R.id.expandableLayout);

            txtFacturaId = itemView.findViewById(R.id.txtFacturaId);
            txtDataPedido = itemView.findViewById(R.id.txtDataPedido);

            orderItems = itemView.findViewById(R.id.order_items);


            imgMetododPagamento = itemView.findViewById(R.id.imgMetododPagamento);
            txtEstado = itemView.findViewById(R.id.txtEstado);
            txtEstadoPagamento = itemView.findViewById(R.id.txtEstadoPagamento);
            txtMetododPagamento = itemView.findViewById(R.id.txtMetododPagamento);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            txtDataPagamento = itemView.findViewById(R.id.txtDataPagamento);



            txtFacturaId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Factura factura = facturaList.get(getAdapterPosition());
                    factura.setExpanded(!factura.isExpanded());
                    notifyItemChanged(getAdapterPosition());

                }
            });


            expandableLayout.setOnClickListener(this);


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
