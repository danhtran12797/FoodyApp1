package com.danhtran12797.thd.foodyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.adapter.LoveProductAdapter;
import com.danhtran12797.thd.foodyapp.fragment.ConnectionFragment;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllProductActivity extends AppCompatActivity {

    private static final String TAG = "AllProductActivity";

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private RotateLoading rotateLoading;

    private LoveProductAdapter adapter;
    private ArrayList<Product> arrProduct;
    private int page = 0;
    private int total_page = 0;

    private Intent intent;

    LinearLayoutManager linearLayoutManager;
    //ProgressDialog progressDialog;
    boolean isLoading = false;
    boolean checkStop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_product);

        initView();
        initActionBar();
        if (Ultil.isNetworkConnected(this)) {
            getTotalPageAllProduct();
            eventScrollRecyclerView();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.layout_container, new ConnectionFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void eventScrollRecyclerView() {
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int frist = linearLayoutManager.findFirstVisibleItemPosition();
                int visible = linearLayoutManager.getChildCount();
                int total_count = linearLayoutManager.getItemCount();

                Log.d(TAG, "frist: " + frist);
                Log.d(TAG, "visible: " + visible);
                Log.d(TAG, "total_count: " + total_count);

                //&&frist!=0
                if (frist + visible >= total_count && isLoading == false && page <= total_page && total_page != 0) {
                    isLoading = true;
                    //progressDialog.show();
                    rotateLoading.start();
                    loadData(++page);
                }
            }
        });
    }

    private void getTotalPageAllProduct() {
        rotateLoading.start();
        DataService dataService = APIService.getService();
        Call<String> callback = dataService.GetTotalAllPage();
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                total_page = Integer.parseInt(response.body());
                loadData(++page);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "error: " + t.getMessage());
            }
        });
    }

    private void loadData(int page) {
        if (page > total_page && checkStop == false) {
            checkStop = true;
            Toast.makeText(AllProductActivity.this, "Đã hết dữ liệu", Toast.LENGTH_SHORT).show();
            return;
        }
        DataService dataService = APIService.getService();
        Call<List<Product>> callback = dataService.GetAllProduct(page);
        callback.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                arrProduct.addAll(response.body());
                adapter.notifyDataSetChanged();

                isLoading = false;
                //progressDialog.hide();
                rotateLoading.stop();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                rotateLoading.stop();
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
        recyclerView = findViewById(R.id.recyclerView_all_product);
        toolbar = findViewById(R.id.toolbar_all_product);
        rotateLoading = findViewById(R.id.rotateloading);

//        progressDialog=new ProgressDialog(this);
//        progressDialog.setCancelable(false);

        arrProduct = new ArrayList<>();
        adapter = new LoveProductAdapter(AllProductActivity.this, arrProduct);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);

    }
}
