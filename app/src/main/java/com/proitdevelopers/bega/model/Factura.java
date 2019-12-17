package com.proitdevelopers.bega.model;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.List;

public class Factura implements Comparator<Factura> {

    public int idFactura;
    public String metododPagamento;
    public String horaEntregueMotoboy;
    public String horaRecebidoCliente;
    public String estado;
    public String estadoPagamento;
    public int clienteID;
    public String dataPagamento,dataPedido;
    public float total;


    public List<FacturaItens> itens;

    private boolean expanded;

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public int compare(Factura o1, Factura o2) {
        // Order ascending.
        int ret = o1.dataPedido.compareTo(o2.dataPedido);

        return ret;
    }
}
