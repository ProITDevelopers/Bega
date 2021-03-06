package com.proitdevelopers.bega.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UsuarioPerfil implements Serializable {


    public int userID;

    @SerializedName("nomeCompleto")
    public String nomeCompleto;

    @SerializedName("primeiroNome")
    public String primeiroNome;

    @SerializedName("ultimoNome")
    public String ultimoNome;

    @SerializedName("email")
    public String email;


    @SerializedName("nCasa")
    public String nCasa;

    @SerializedName("sexo")
    public String sexo;


    @SerializedName("rua")
    public String rua;

    @SerializedName("bairro")
    public String bairro;

    @SerializedName("municipio")
    public String municipio;

    @SerializedName("provincia")
    public String provincia;

    @SerializedName("contactoMovel")
    public String contactoMovel;

    @SerializedName("contactoAlternativo")
    public String contactoAlternativo;

    @SerializedName("userName")
    public String userName;

    @SerializedName("imagem")
    public String imagem;

    public Wallet wallet;

    public float mSaldoWallet;

    public UsuarioPerfil() {
    }

    public UsuarioPerfil(String primeiroNome, String ultimoNome, String contactoMovel, String contactoAlternativo, String provincia, String municipio, String bairro, String rua, String nCasa, String sexo) {
        this.primeiroNome = primeiroNome;
        this.ultimoNome = ultimoNome;
        this.contactoMovel = contactoMovel;
        this.contactoAlternativo = contactoAlternativo;
        this.provincia = provincia;
        this.municipio = municipio;
        this.bairro = bairro;
        this.rua = rua;
        this.nCasa = nCasa;
        this.sexo = sexo;
    }

    public UsuarioPerfil(String primeiroNome, String ultimoNome, String email, String imagem) {
        this.primeiroNome = primeiroNome;
        this.ultimoNome = ultimoNome;
        this.email = email;
        this.imagem = imagem;
    }

}
