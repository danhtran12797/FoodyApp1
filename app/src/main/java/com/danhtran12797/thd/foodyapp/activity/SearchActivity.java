package com.danhtran12797.thd.foodyapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.adapter.LoveProductAdapter;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private SearchView searchView;
    private LoveProductAdapter adapter;
    private ArrayList<Product> arrProduct;
    private RotateLoading rotateLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initView();
        initActionBar();
        evenSearchView();
    }

    private void evenSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setQuery("", false);
                searchView.requestFocus();
                arrProduct.clear();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setAdapter(ArrayList<Product> arrayList) {
        adapter = new LoveProductAdapter(this, arrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        searchView.clearFocus();
    }

    private void getData(String query) {
        rotateLoading.start();
        DataService dataService = APIService.getService();
        Call<List<Product>> callback = dataService.GetProductFromSearch(query);
        callback.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                rotateLoading.stop();
                arrProduct.clear();
                arrProduct = (ArrayList<Product>) response.body();
                setAdapter(arrProduct);
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                rotateLoading.stop();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_search_view);
        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.recycler_view_search);
        rotateLoading = findViewById(R.id.rotateloading);

        arrProduct = new ArrayList<>();
        adapter = new LoveProductAdapter(this, arrProduct);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
