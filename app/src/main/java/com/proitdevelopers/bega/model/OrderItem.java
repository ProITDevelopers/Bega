package com.proitdevelopers.bega.model;

import com.google.gson.annotations.SerializedName;

public class OrderItem {

    @SerializedName("produtoId")
    public int productId;

    @SerializedName("quantidade")
    public int quantity;

    public Produtos produtos;
}
