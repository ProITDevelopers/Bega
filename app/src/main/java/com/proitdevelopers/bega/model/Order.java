package com.proitdevelopers.bega.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order {

    @SerializedName("localEncomenda")
    public LocalEncomenda localEncomenda;

    @SerializedName("itensFacturaos")
    public List<OrderItem> orderItems;

    public Order() {
    }
}
