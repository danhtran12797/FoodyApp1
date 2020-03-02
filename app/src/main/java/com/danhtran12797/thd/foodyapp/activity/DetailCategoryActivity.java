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
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailCategoryActivity extends AppCompatActivity {

    private static final String TAG = "DetailCategoryActivity";

    private Intent intent;

    Toolbar toolbar;
    RecyclerView recyclerView;
    RotateLoading rotateloading;
    LoveProductAdapter adapter;
    ArrayList<Product> arrayList;
    String id_category;
    String name_category;
    int page = 0;
    int total_page = 0;
    LinearLayoutManager linearLayoutManager;
    //ProgressDialog progressDialog;
    boolean isLoading = false;
    boolean checkStop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_category);

        Intent intent = getIntent();
        id_category = intent.getStringExtra("id_category");
        name_category = intent.getStringExtra("name_category");

        initView();
        initActionBar();
        getTotalPage(id_category);
        eventScrollRecyclerView();
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

    private void initActionBar() {
        toolbar = findViewById(R.id.toolbar_category);
        toolbar.setTitle(name_category);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getTotalPage(String iddm) {
        rotateloading.start();
        DataService dataService = APIService.getService();
        Call<String> callback = dataService.GetTotalPage(iddm);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                total_page = Integer.parseInt(response.body());
                Log.d(TAG, "onResponse: " + total_page);
                loadData(++page);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "error: " + t.getMessage());
            }
        });
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

                if (frist + visible >= total_count && isLoading == false && page <= total_page && total_page != 0) {
                    isLoading = true;
                    //progressDialog.show();
                    rotateloading.start();
                    loadData(++page);
                    Log.d(TAG, "onScrolled: sui quá");
                }

            }
        });
    }

    private void loadData(int page) {
        if (page > total_page && checkStop == false) {
            //progressDialog.hide();
            rotateloading.stop();
            checkStop = true;
            Toast.makeText(DetailCategoryActivity.this, "Đã hết dữ liệu", Toast.LENGTH_SHORT).show();
            return;
        }
        DataService dataService = APIService.getService();
        Call<List<Product>> callback = dataService.GetDetailCategory(id_category, page);
        callback.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                arrayList.addAll(response.body());
                adapter.notifyDataSetChanged();

                isLoading = false;
                //progressDialog.hide();
                rotateloading.stop();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "error: " + t.getMessage());
            }
        });
    }

    private void initView() {
        rotateloading = findViewById(R.id.rotateloading);

        recyclerView = findViewById(R.id.recyclerViewDetailCategory);
        linearLayoutManager = new LinearLayoutManager(this);

        arrayList = new ArrayList<>();
        adapter = new LoveProductAdapter(getApplicationContext(), arrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }
}
