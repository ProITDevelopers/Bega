package com.proitdevelopers.bega.fragmentos;


import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.adapters.CategoriaAdapter;
import com.proitdevelopers.bega.adapters.ItemClickListener;
import com.proitdevelopers.bega.helper.Common;
import com.proitdevelopers.bega.model.Categoria;

import java.util.List;

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
    ProgressBar progressBar;
    RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;
    List<Categoria> categoriaList;


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

        //Carrega o menu
        progressBar =  view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerCategory =  view.findViewById(R.id.recyclerCategory);
        recyclerCategory.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerCategory.setLayoutManager(layoutManager);
//        recyclerCategory.setLayoutManager(gridLayoutManager);

        categoriaList = Common.getCategoryList();

        loadCategories();

        return view;
    }

    private void loadCategories() {
        CategoriaAdapter categoriaAdapter = new CategoriaAdapter(getContext(),categoriaList);
        recyclerCategory.setAdapter(categoriaAdapter);
        categoriaAdapter.notifyDataSetChanged();
        categoriaAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {

                Categoria categoria = categoriaList.get(position);
                goToFragment(categoria.getNomeCategoria());

            }
        });
    }

    private void goToFragment(String categoria){
        if (getActivity()!=null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            if (fragmentManager != null){
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (categoria.equals("Altas Horas")){
                    fragmentTransaction.replace(R.id.frame_layout, new AltasHorasEstabelecimentoFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }else{
                    fragmentTransaction.replace(R.id.frame_layout, new EstabelecimentoFragment());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();


        if (itemId == R.id.menu_refresh) {
            loadCategories();
            return true;
        }




        return super.onOptionsItemSelected(item);
    }
}
