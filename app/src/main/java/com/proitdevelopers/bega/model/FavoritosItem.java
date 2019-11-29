package com.proitdevelopers.bega.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FavoritosItem extends RealmObject {
    @PrimaryKey
    public String id = UUID.randomUUID().toString();
    public Produtos produtos;
    public int quantity = 0;

    public FavoritosItem() {}
}
