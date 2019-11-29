package com.proitdevelopers.bega.model;

public class Categoria {

    private int idCategoria;
    private String nomeCategoria, imagemCategoria;

    public Categoria() {
    }

    public Categoria(int idCategoria, String nomeCategoria, String imagemCategoria) {
        this.idCategoria = idCategoria;
        this.nomeCategoria = nomeCategoria;
        this.imagemCategoria = imagemCategoria;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public void setNomeCategoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }

    public String getImagemCategoria() {
        return imagemCategoria;
    }

    public void setImagemCategoria(String imagemCategoria) {
        this.imagemCategoria = imagemCategoria;
    }
}
