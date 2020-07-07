package com.proitdevelopers.bega.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.helper.Common;
import com.proitdevelopers.bega.localDB.AppPref;
import com.proitdevelopers.bega.model.UsuarioPerfil;
import com.proitdevelopers.bega.utilsClasses.AppCompatPreferenceActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

public class ConfiguracoesActivity extends AppCompatPreferenceActivity {

    Drawable toolbarBK;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_configuracoes);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        toolbarBK = getResources().getDrawable( R.drawable.toolbar_bk );



        if (getSupportActionBar()!=null){
            getSupportActionBar().setBackgroundDrawable(toolbarBK);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            setTitle("Definições");
            // load settings fragment
            getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
        }



    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_configuracoes);

            // feedback preference click listener
            Preference key_meu_perfil = findPreference(getString(R.string.key_meu_perfil));
            key_meu_perfil.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {

                    openEditPerfilActivity(getActivity());
                    return true;
                }
            });

            // feedback preference click listener
            Preference key_alterar_senha = findPreference(getString(R.string.key_alterar_senha));
            key_alterar_senha.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    openAlterarSenhaActivity(getActivity());

                    return true;
                }
            });

            // feedback preference click listener
//            Preference key_bega_manual = findPreference(getString(R.string.key_bega_manual));
//            key_bega_manual.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                public boolean onPreferenceClick(Preference preference) {
//                    openInternetActivity(getActivity());
//                    Toast.makeText(getActivity(), "key_bega_manual", Toast.LENGTH_SHORT).show();
//
//                    return true;
//                }
//            });

            Preference key_app_version = findPreference(getString(R.string.key_app_version));
            key_app_version.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
//                    sendFeedback(getActivity());


                    return true;
                }
            });


            // feedback preference click listener
            Preference myPref = findPreference(getString(R.string.key_send_feedback));
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity());
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    public static void openEditPerfilActivity(Context context) {

        Intent intent = new Intent(context, EditarPerfilActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("mCurrentUser",Common.mCurrentUser);
        context.startActivity(intent);

    }

    public static void openAlterarSenhaActivity(Context context) {

        Intent intent = new Intent(context, AlterarSenhaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

    }


    public static void openInternetActivity(Context context) {

        Intent intent = new Intent(context, WebViewInternetActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("bega_manual","Manual Bega");
        context.startActivity(intent);

    }


    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            /*body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;*/

            body = "\n\n-------------------------------------------------\nPor favor não remova essa informação\n SO do Dispositivo: Android \n Versão do SO do Dispositivo: " +
                    Build.VERSION.RELEASE + "\n Versão da Aplicação: " + body + "\n Marca do Dispositivo: " + Build.BRAND +
                    "\n Modelo do Dispositivo: " + Build.MODEL + "\n Fabricante do Dispositivo: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"proitandroit@gmail.com"});
        /*intent.putExtra(Intent.EXTRA_SUBJECT, "Query from android app");*/
        intent.putExtra(Intent.EXTRA_SUBJECT, "Consulta do aplicativo Android");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }

}
