package com.proitdevelopers.bega.helper;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.proitdevelopers.bega.model.Categoria;
import com.proitdevelopers.bega.model.UsuarioPerfil;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Common {


    public static final String DB_REALM = "begasrealm";


    public static UsuarioPerfil mCurrentUser = new UsuarioPerfil();

    public static String ROCK_BURGER_LOGO = "https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Frock_burger_logo.png?alt=media&token=b77ba4ad-d16b-4119-9b27-53b8e534fa51";
    public static String KFC_LOGO = "https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Fkfc_logo.png?alt=media&token=f07bbb8b-2949-4307-96e1-729059256dc3";
    public static String DOOH_PONTO_LOGO = "https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Fdooh_ponto_logo.png?alt=media&token=fcb7d40b-aecc-4eb0-b803-6d8d0e0bf876";


    public static String bearerApi = "Bearer ";
    public static String msgErro = "Preencha o campo.";
    public static String msgErroEspaco = "Preencha o campo sem espaço.";
    public static String msgErroTentar = "Tentar de Novo";
    public static String msgErroLetras = "Preencha o com letras.";
    public static String msgErroSenha = "Senha fraca.";
    public static String msgErroSenhaDiferente = "As senhas devem ser iguais";
    public static String msgErroSTelefone = "Preencha o campo com um nº telefone.";
    public static String msgErroSEmailTelefone = "Preencha o campo com email ou nº telefone.";
    public static String msgErroTelefone = "Preencha com um número válido";
    public static String msgQuasePronto = "Quase Pronto...!";
    public static String msgErroTelefoneIguais = "Os números devem ser diferentes";
    public static String msgAEnviarTelefone = "A enviar o nº Telefone..";
    public static String msgSupporte = "Enviamos o código de confirmação para - ";
    public static String msgReenviarNumTelef = "A reenviar o Nº Telefone..";
    public static String msgEnviandoCodigo = "Enviando o código de confirmação..!";

    public static String CATEGORY_DRINKS="https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Fdrinks_category.jpg?alt=media&token=7b9e1f30-5a05-4baa-90db-af87bf9fbc8b";
    public static String CATEGORY_HAMBURGER="https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Fhamburger_category.png?alt=media&token=7c803deb-624f-40fc-b277-8ae1b7d14abd";
    public static String CATEGORY_PIZZA="https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Fpizza_category.png?alt=media&token=38048a35-93b8-4886-a823-d3668f75bd6a";
    public static String CATEGORY_HAPPY_HOUR="https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Fhappy_hour_category.png?alt=media&token=c38ff628-1bda-461b-9493-dc14da45bdae";
    public static String CATEGORY_BEER_WINGS="https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Fbeer_wings_category.jpg?alt=media&token=8e8cb91e-94cd-480d-bffc-05ff50f2ff4f";
    public static String CATEGORY_CHURRASCO="https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Fchurrasco_category.jpg?alt=media&token=eed541df-a852-4e09-8836-4c1f5cc59f58";


    public static List<Categoria> getCategoryList(){
        List<Categoria> categoriaList = new ArrayList<>();

        categoriaList.add(new Categoria(1,"Bebidas",CATEGORY_DRINKS));
        categoriaList.add(new Categoria(2,"Hamburgers",CATEGORY_HAMBURGER));
        categoriaList.add(new Categoria(3,"Pizzas",CATEGORY_PIZZA));
        categoriaList.add(new Categoria(4,"Altas Horas",CATEGORY_HAPPY_HOUR));
        categoriaList.add(new Categoria(5,"Cerveja e Asinhas",CATEGORY_BEER_WINGS));
        categoriaList.add(new Categoria(6,"Grelhados",CATEGORY_CHURRASCO));

        return categoriaList;
    }

}
