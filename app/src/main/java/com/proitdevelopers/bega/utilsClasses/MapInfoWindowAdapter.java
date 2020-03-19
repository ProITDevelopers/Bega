package com.proitdevelopers.bega.utilsClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.helper.Common;
import com.proitdevelopers.bega.model.UsuarioPerfil;
import com.squareup.picasso.Picasso;

import java.util.Hashtable;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{


    private Context context;
    private View myContentsView;


    public MapInfoWindowAdapter(Context context) {
        this.context = context;

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        String foto =  Common.mCurrentUser.imagem;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        myContentsView = inflater.inflate(R.layout.custom_rider_info_window, null);
        CircleImageView circleImageView = (CircleImageView)myContentsView.findViewById(R.id.img);

        if (marker.getTitle().equals("Eu")){

            Picasso.with(context)
                    .load(foto)
                    .placeholder(R.drawable.ic_camera)
                    .into(circleImageView);

        } else if (marker.getTitle().equals("Local de entrega")){

            circleImageView.setImageResource(R.drawable.ic_address_orange_24dp);

        }


        TextView txtPickupTitle = ((TextView)myContentsView.findViewById(R.id.txtNameInfo));
        txtPickupTitle.setText(marker.getTitle());

        TextView txtPickupSnippet = ((TextView)myContentsView.findViewById(R.id.txtEnderecoSnippet));
        txtPickupSnippet.setText(marker.getSnippet());

        return myContentsView;
    }
}
