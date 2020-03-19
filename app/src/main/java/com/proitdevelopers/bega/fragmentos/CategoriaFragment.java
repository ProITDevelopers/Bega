package com.proitdevelopers.bega.fragmentos;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.proitdevelopers.bega.R;

import com.proitdevelopers.bega.adapters.CategoriaAdapter;
import com.proitdevelopers.bega.adapters.ItemClickListener;
import com.proitdevelopers.bega.helper.Common;
import com.proitdevelopers.bega.localDB.AppPref;
import com.proitdevelopers.bega.model.Categoria;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.proitdevelopers.bega.helper.Common.SPAN_COUNT_ONE;
import static com.proitdevelopers.bega.helper.Common.SPAN_COUNT_THREE;
import static com.proitdevelopers.bega.helper.Common.TOP_MENU_CATEGORY;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoriaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoriaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;

    RecyclerView recyclerCategory;
    ProgressBar progress_bar_top, progressBar;
    RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;
    List<Categoria> categoriaList;
    CategoriaAdapter categoriaAdapter;

    ImageView category_image_bega,category_image_top;

    private ConstraintLayout coordinatorLayout;
    private RelativeLayout errorLayout;
    private TextView btnTentarDeNovo;


    public CategoriaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoriaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoriaFragment newInstance(String param1, String param2) {
        CategoriaFragment fragment = new CategoriaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_categoria, container, false);

        categoriaList = Common.getCategoryList();

        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        errorLayout = (RelativeLayout) view.findViewById(R.id.erroLayout);
        btnTentarDeNovo = (TextView) view.findViewById(R.id.btn);
        btnTentarDeNovo.setText("Tentar de Novo");



        category_image_bega =  view.findViewById(R.id.category_image_bega);
        category_image_top =  view.findViewById(R.id.category_image_top);

        progress_bar_top =  view.findViewById(R.id.progress_bar);
        progressBar =  view.findViewById(R.id.progressBar);

        recyclerCategory =  view.findViewById(R.id.recyclerCategory);
        recyclerCategory.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        gridLayoutManager = new GridLayoutManager(getContext(), AppPref.getInstance().getListGridViewMode());
//        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                if (position % 5 == 0){
                    return 2;
                }else{
                    return 1;
                }

            }
        });
//        recyclerCategory.setLayoutManager(gridLayoutManager);
        if (AppPref.getInstance().getListGridViewMode() ==  SPAN_COUNT_THREE) {

            recyclerCategory.setLayoutManager(gridLayoutManager);
        } else {

            recyclerCategory.setLayoutManager(layoutManager);
        }

        Picasso.with(getContext())
                .load(TOP_MENU_CATEGORY)
                .placeholder(R.drawable.hamburger_placeholder)
                .error(R.drawable.hamburger_placeholder)
                .into(category_image_top, new Callback() {
                    @Override
                    public void onSuccess() {
                        progress_bar_top.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        progress_bar_top.setVisibility(View.GONE);
                    }
                });


        verifConecxaoCategoria();

        return view;
    }

    private void verifConecxaoCategoria() {

        if (getContext()!=null) {

            ConnectivityManager conMgr =  (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    mostarMsnErro();
                } else {
                    loadCategories();

                }
            }

        }

    }

    private void mostarMsnErro(){

        if (errorLayout.getVisibility() == View.GONE){
            errorLayout.setVisibility(View.VISIBLE);

            coordinatorLayout.setVisibility(View.GONE);

        }

        btnTentarDeNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coordinatorLayout.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                verifConecxaoCategoria();
            }
        });
    }

    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);

        categoriaAdapter = new CategoriaAdapter(getContext(),categoriaList,gridLayoutManager);
        recyclerCategory.setAdapter(categoriaAdapter);
        categoriaAdapter.notifyDataSetChanged();
        categoriaAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {

                Categoria categoria = categoriaList.get(position);
                goToFragment(categoria.getNomeCategoria(),categoria.getIdCategoria());

            }
        });
        progressBar.setVisibility(View.GONE);
    }

    private void goToFragment(String categoria, int categoriaID){
        if (getActivity()!=null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            if (fragmentManager != null){
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (categoria.equals("Altas Horas")){

                    AltasHorasEstabelecimentoFragment altasHorasEstabelecimentoFragment = new AltasHorasEstabelecimentoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("categoria", categoria);
                    altasHorasEstabelecimentoFragment.setArguments(bundle);

                    fragmentTransaction.replace(R.id.frame_layout, altasHorasEstabelecimentoFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }else{

                    EstabelecimentoFragment estabelecimentoFragment = new EstabelecimentoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("categoriaID", categoriaID);
                    bundle.putString("categoria", categoria);
                    estabelecimentoFragment.setArguments(bundle);

                    fragmentTransaction.replace(R.id.frame_layout, estabelecimentoFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_categoria_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        MenuItem item =  menu.findItem(R.id.menu_view);
//        loadIcon(item);
//        super.onPrepareOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();


        if (itemId == R.id.menu_refresh) {

            if (errorLayout.getVisibility() == View.VISIBLE){
                errorLayout.setVisibility(View.GONE);
                coordinatorLayout.setVisibility(View.VISIBLE);
            }
            verifConecxaoCategoria();


            return true;
        }

//        if (itemId == R.id.menu_view) {
//            switchLayout();
//            switchIcon(item);
//            return true;
//        }




        return super.onOptionsItemSelected(item);
    }

    private void loadIcon(MenuItem item) {
        if (AppPref.getInstance().getListGridViewMode() ==  SPAN_COUNT_THREE) {
            item.setTitle("Lista");
            item.setIcon(getResources().getDrawable(R.drawable.ic_grid_on_white_24dp));
        } else {
            item.setTitle("Grelha");
            item.setIcon(getResources().getDrawable(R.drawable.ic_list_white_24dp));
        }
    }

    private void switchLayout() {
        if (gridLayoutManager.getSpanCount() == SPAN_COUNT_ONE) {
            gridLayoutManager.setSpanCount(SPAN_COUNT_THREE);


            recyclerCategory.setLayoutManager(gridLayoutManager);

        } else {
            gridLayoutManager.setSpanCount(SPAN_COUNT_ONE);

            recyclerCategory.setLayoutManager(layoutManager);
        }


        try {
            categoriaAdapter.notifyItemRangeChanged(0, categoriaAdapter.getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppPref.getInstance().saveListGridViewMode(gridLayoutManager.getSpanCount());

    }

    private void switchIcon(MenuItem item) {


        if (gridLayoutManager.getSpanCount() ==  SPAN_COUNT_THREE) {
            item.setIcon(getResources().getDrawable(R.drawable.ic_grid_on_white_24dp));
        } else {
            item.setIcon(getResources().getDrawable(R.drawable.ic_list_white_24dp));
        }

    }
}
