package com.proitdevelopers.bega.model;

import com.google.gson.annotations.SerializedName;

public class OrderItem {

    @SerializedName("produtoId")
    public int produtoId;

    @SerializedName("quantidade")
    public int quantidade;

    @SerializedName("produtos")
    public Produtos produtos;
}
