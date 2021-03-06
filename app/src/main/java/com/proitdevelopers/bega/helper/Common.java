package com.proitdevelopers.bega.helper;

import android.location.Location;

import com.proitdevelopers.bega.model.Categoria;
import com.proitdevelopers.bega.model.UsuarioPerfil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Common {

    public static final int SPAN_COUNT_ONE = 1;
    public static final int SPAN_COUNT_THREE = 2;

    public static final int VIEW_TYPE_SMALL = 1;
    public static final int VIEW_TYPE_BIG = 2;


    public static final String DB_REALM = "begasrealm";


    public static UsuarioPerfil mCurrentUser = new UsuarioPerfil();

    public static Location mLastLocation;

    public static String ROCK_BURGER_LOGO = "https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Frock_burger_logo.png?alt=media&token=b77ba4ad-d16b-4119-9b27-53b8e534fa51";
    public static String KFC_LOGO = "https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Fkfc_logo.png?alt=media&token=f07bbb8b-2949-4307-96e1-729059256dc3";
    public static String DOOH_PONTO_LOGO = "https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Fdooh_ponto_logo.png?alt=media&token=fcb7d40b-aecc-4eb0-b803-6d8d0e0bf876";
    public static String BEGA_MANUAL_USER = "https://www.dropbox.com/s/unrijjzt01cubgf/bega_android.pdf?dl=0";


    public static String bearerApi = "Bearer ";
    public static String msgErro = "Preencha o campo.";
    public static String msgErroEspaco = "Preencha o campo sem espaço.";
    public static String msgErroTentar = "Tentar de Novo";
    public static String msgErroLetras = "Preencha o com letras.";
    public static String msgErroLetrasCaracteres = "Preencha no mínimo com 3 letras.";
    public static String msgErroSenha = "Senha fraca.";
    public static String msgErroSenhaDiferente = "As senhas devem ser iguais";
    public static String msgErroSTelefone = "Preencha o campo com um nº telefone.";
    public static String msgErroSEmailTelefone = "Preencha o campo com email ou nº telefone.";
    public static String msgErroSEmail = "Preencha o campo com email válido.";
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
    public static String CATEGORY_QUITUTES_2="https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Fquitutes_category_2.jpg?alt=media&token=95fbb7d3-26b6-4f6c-b3ce-a770db6d5d37";

    public static String TOP_MENU_CATEGORY="https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Fmenu_pics_official%2Ftop_menu_category.png?alt=media&token=222cd49c-bd2e-47ea-ba73-9ff1b676fb90";
    public static String CATEGORY_EVENTOS="https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Fmenu_pics_official%2FEVENTOS.jpg?alt=media&token=03576d3f-841d-4dac-bacc-ac73edb3ad67";
    public static String CATEGORY_RESTAURANTE="https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Fmenu_pics_official%2FRESTAURANTES.jpg?alt=media&token=34cc6a22-0414-4df6-9753-51abc78e3533";
    public static String CATEGORY_CASEIRO="https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Fmenu_pics_official%2FCASEIROS.jpg?alt=media&token=00391823-7026-4dc2-9d88-8683b6ef2f67";
    public static String CATEGORY_ALTAS_HORAS="https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Fmenu_pics_official%2FALTAS_HORAS.jpg?alt=media&token=82fd4849-7883-4ae1-9dcf-a2e6fd8ae32f";
    public static String CATEGORY_COVENIENCIA="https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Fmenu_pics_official%2FCONVENIENCIA.jpg?alt=media&token=fe9b1085-0d6f-437f-b93c-e9b700cc8ff4";
    public static String CATEGORY_ESTAFETAS="https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Fmenu_pics_official%2FEstafetas_category.png?alt=media&token=6a787123-d251-41f9-8729-e10f18c09c1d";
    public static String CATEGORY_QUITUTES="https://firebasestorage.googleapis.com/v0/b/firetutorial-9cfcc.appspot.com/o/bega%2Fmenu_pics_official%2Fquitutes_category.png?alt=media&token=e1cd7edd-3cb7-4c7f-9aa1-76fb1abc9245";



    public static List<Categoria> getCategoryList(){
        List<Categoria> categoriaList = new ArrayList<>();

        categoriaList.add(new Categoria(1,"Restaurantes",CATEGORY_RESTAURANTE));
        categoriaList.add(new Categoria(2,"Conveniência",CATEGORY_COVENIENCIA));
        categoriaList.add(new Categoria(3,"Caseiros",CATEGORY_CASEIRO));
        categoriaList.add(new Categoria(4,"Eventos",CATEGORY_EVENTOS));
        categoriaList.add(new Categoria(2,"Altas Horas",CATEGORY_ALTAS_HORAS));
//        categoriaList.add(new Categoria(2,"Estafetas",CATEGORY_ESTAFETAS));
//        categoriaList.add(new Categoria(7,"Quitutes",CATEGORY_QUITUTES));

//        categoriaList.add(new Categoria(1,"Bebidas",CATEGORY_DRINKS));
//        categoriaList.add(new Categoria(2,"Hambúrgers",CATEGORY_HAMBURGER));
//        categoriaList.add(new Categoria(3,"Pizzas",CATEGORY_PIZZA));
//        categoriaList.add(new Categoria(4,"Altas Horas",CATEGORY_HAPPY_HOUR));
//        categoriaList.add(new Categoria(5,"Cerveja e Asinhas",CATEGORY_BEER_WINGS));
//        categoriaList.add(new Categoria(6,"Caseiro",CATEGORY_CHURRASCO));
//        categoriaList.add(new Categoria(7,"Quitutes",CATEGORY_QUITUTES_2));

        return categoriaList;
    }

}
