package com.proitdevelopers.bega.utilsClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.model.UsuarioPerfil;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    private View myView;

    public CustomInfoWindow(Context context) {
        myView = LayoutInflater.from(context)
                .inflate(R.layout.custom_rider_info_window, null) ;
    }

    @Override
    public View getInfoWindow(Marker marker) {

        String foto = (String) marker.getTag();

        CircleImageView circleImageView = myView.findViewById(R.id.img);

        if (marker.getTitle().equals("Eu")){
            Picasso.with(myView.getContext()).load(foto).placeholder(R.drawable.ic_camera).into(circleImageView);
        } else if (marker.getTitle().equals("Local de entrega")){

           circleImageView.setImageResource(R.drawable.ic_address_orange_24dp);

        }


        TextView txtPickupTitle = ((TextView)myView.findViewById(R.id.txtNameInfo));
        txtPickupTitle.setText(marker.getTitle());

        TextView txtPickupSnippet = ((TextView)myView.findViewById(R.id.txtEnderecoSnippet));
        txtPickupSnippet.setText(marker.getSnippet());


        return myView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
