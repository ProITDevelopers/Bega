package com.proitdevelopers.bega.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.proitdevelopers.bega.R;

public class CheckOutActivity extends AppCompatActivity {

    private TextView txtNumero,txtEndereco,txtLatitude,txtLongitude;
    private String tipoPagamento,numero,endereco,latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Checkout");
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent()!=null){
            tipoPagamento = getIntent().getStringExtra("tipoPagamento");
            numero = getIntent().getStringExtra("numero");
            endereco = getIntent().getStringExtra("endereco");
            latitude = getIntent().getStringExtra("latitude");
            longitude = getIntent().getStringExtra("longitude");
        }

        txtNumero = findViewById(R.id.txtNumero);
        txtEndereco = findViewById(R.id.txtEndereco);
        txtLatitude = findViewById(R.id.txtLatitude);
        txtLongitude = findViewById(R.id.txtLongitude);







        txtNumero.setText("Contacto: "+numero +", pagamento: "+tipoPagamento);
        txtEndereco.setText("Endereco: "+endereco);
        txtLatitude.setText("Latitude: "+latitude);
        txtLongitude.setText("Longitude: "+longitude);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
