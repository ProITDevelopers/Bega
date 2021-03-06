package com.proitdevelopers.bega.fragmentos;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.proitdevelopers.bega.R;
import com.proitdevelopers.bega.activities.ProdutosActivity;
import com.proitdevelopers.bega.adapters.EstabelecimentoAdapter;
import com.proitdevelopers.bega.adapters.ItemClickListener;
import com.proitdevelopers.bega.api.ApiClient;
import com.proitdevelopers.bega.api.ApiInterface;
import com.proitdevelopers.bega.localDB.AppPref;
import com.proitdevelopers.bega.model.Estabelecimento;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.proitdevelopers.bega.helper.Common.SPAN_COUNT_ONE;
import static com.proitdevelopers.bega.helper.Common.SPAN_COUNT_THREE;
import static com.proitdevelopers.bega.helper.MetodosUsados.conexaoInternetTrafego;
import static com.proitdevelopers.bega.helper.MetodosUsados.mostrarMensagem;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EstabelecimentoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EstabelecimentoFragment extends Fragment {

    private String TAG = "EstabelecimentoFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;

    private ProgressBar progressBar;

    private List<Estabelecimento> estabelecimentoList = new ArrayList<>();


    private RecyclerView recyclerView;
    private EstabelecimentoAdapter itemAdapter;
    private GridLayoutManager gridLayoutManager;

    private ConstraintLayout coordinatorLayout;
    private RelativeLayout errorLayout;
    private TextView btnTentarDeNovo;

    int idTipoEstabelecimento;
    String categoriaNome;

    Toolbar toolbar;
    public TextView txtToolbar;

    SearchView searchView;


    public EstabelecimentoFragment() {
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
    public static EstabelecimentoFragment newInstance(String param1, String param2) {
        EstabelecimentoFragment fragment = new EstabelecimentoFragment();
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
            idTipoEstabelecimento = getArguments().getInt("categoriaID", 0);
            categoriaNome = getArguments().getString("categoria");
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_estabelecimento, container, false);

        if (getActivity()!=null){
            toolbar =  ((AppCompatActivity)getActivity()).findViewById(R.id.toolbar);
            txtToolbar = toolbar.findViewById(R.id.txtToolbar);
            txtToolbar.setText(categoriaNome);
        }



        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        errorLayout = (RelativeLayout) view.findViewById(R.id.erroLayout);
        btnTentarDeNovo = (TextView) view.findViewById(R.id.btn);
        btnTentarDeNovo.setText("Tentar de Novo");

        searchView = (SearchView) view.findViewById(R.id.search_bar);
        searchView.setQueryHint(getString(R.string.pesquisar));
//        searchView.onActionViewExpanded();
        searchView.setIconifiedByDefault(true);


        gridLayoutManager = new GridLayoutManager(getContext(), AppPref.getInstance().getListGridViewMode());
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewEstab);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);


//        verifConecxaoEstabelecimentos();

        if (searchView==null) {

            verifConecxaoEstabelecimentos("");

        } else {
            verifConecxaoEstabelecimentos(searchView.getQuery().toString());
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                verifConecxaoEstabelecimentos(newText);
                return false;
            }
        });


        return view;
    }

    private void verifConecxaoEstabelecimentos(String searchText) {

        if (getActivity()!=null) {
            ConnectivityManager conMgr =  (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr!=null) {
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null){
                    mostarMsnErro();
                } else {
                    carregarListaEstabelicimentos(searchText);
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
                verifConecxaoEstabelecimentos("");
            }
        });
    }

    private void carregarListaEstabelicimentos(String searchText) {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Estabelecimento>> rv = apiInterface.getEstabelecimentosPorTipo(idTipoEstabelecimento);
//        Call<List<Estabelecimento>> rv = apiInterface.getAllEstabelecimentos();
        rv.enqueue(new Callback<List<Estabelecimento>>() {
            @Override
            public void onResponse(@NonNull Call<List<Estabelecimento>> call, @NonNull Response<List<Estabelecimento>> response) {

                if (response.isSuccessful()) {

                    if (response.body()!=null){


                        estabelecimentoList.clear();
                        if (searchView!=null) {
                            for (Estabelecimento estabelecimento:response.body()){
                                if (estabelecimento.nomeEstabelecimento.toLowerCase().startsWith(searchText.toLowerCase()) ||
                                        estabelecimento.nomeEstabelecimento.toLowerCase().contains(searchText.toLowerCase())
                                )
                                    estabelecimentoList.add(estabelecimento);
                            }
                        } else{

                            estabelecimentoList = response.body();
                        }

                        progressBar.setVisibility(View.GONE);
                        setAdapters(estabelecimentoList);


                    }

                } else {

                    Toast.makeText(getContext(), ""+response.message(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }


            }

            @Override
            public void onFailure(@NonNull Call<List<Estabelecimento>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                if (!conexaoInternetTrafego(getContext(),TAG)){
                    mostrarMensagem(getContext(),R.string.txtMsg);
                }else  if ("timeout".equals(t.getMessage())) {
                    mostrarMensagem(getContext(),R.string.txtTimeout);
                }else {
                    mostrarMensagem(getContext(),R.string.txtProblemaMsg);
                }
            }
        });
    }



    private void setAdapters(List<Estabelecimento> estabelecimentoList) {

        if (estabelecimentoList.size()>0){
            itemAdapter = new EstabelecimentoAdapter(getContext(), estabelecimentoList, gridLayoutManager);
            recyclerView.setAdapter(itemAdapter);
            recyclerView.setLayoutManager(gridLayoutManager);

            itemAdapter.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position) {

                    Estabelecimento estabelecimento = estabelecimentoList.get(position);

                    Intent intent = new Intent(getContext(), ProdutosActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("idEstabelecimento",estabelecimento.estabelecimentoID);
                    intent.putExtra("imagemCapa",estabelecimento.imagemCapa);
                    intent.putExtra("nomeEstabelecimento",estabelecimento.nomeEstabelecimento);
                    startActivity(intent);

                }
            });
        }


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_options_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        MenuItem item =  menu.findItem(R.id.menu_switch_layout);
//        loadIcon(item);
//        super.onPrepareOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

//        if (itemId == R.id.menu_switch_layout) {
//            switchLayout();
//            switchIcon(item);
//            return true;
//        }

        if (itemId == R.id.menu_refresh) {
            if (errorLayout.getVisibility() == View.VISIBLE){
                errorLayout.setVisibility(View.GONE);
                coordinatorLayout.setVisibility(View.VISIBLE);
            }
            verifConecxaoEstabelecimentos("");
            return true;
        }




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

        } else {
            gridLayoutManager.setSpanCount(SPAN_COUNT_ONE);

        }


        try {
            itemAdapter.notifyItemRangeChanged(0, itemAdapter.getItemCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppPref.getInstance().saveListGridViewMode(gridLayoutManager.getSpanCount());

    }

    private void switchIcon(MenuItem item) {
//        if (gridLayoutManager.getSpanCount() == SPAN_COUNT_THREE) {

        if (gridLayoutManager.getSpanCount() ==  SPAN_COUNT_THREE) {
            item.setIcon(getResources().getDrawable(R.drawable.ic_grid_on_white_24dp));
        } else {
            item.setIcon(getResources().getDrawable(R.drawable.ic_list_white_24dp));
        }

    }
}
