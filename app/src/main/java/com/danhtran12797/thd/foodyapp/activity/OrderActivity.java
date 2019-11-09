package com.danhtran12797.thd.foodyapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.adapter.OrderAdapter;
import com.danhtran12797.thd.foodyapp.fragment.ConnectionFragment;
import com.danhtran12797.thd.foodyapp.model.Order;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    RotateLoading rotateLoading;
    OrderAdapter adapter;
    public static ArrayList<Order> arrOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        initView();
        initActionBar();
        if (Ultil.isNetworkConnected(this)) {
            loadData();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.layout_container, new ConnectionFragment()).commit();
        }
    }

    private void loadData() {
        rotateLoading.start();
        DataService dataService = APIService.getService();
        Call<List<Order>> callback = dataService.GetOrder(Ultil.user.getId());
        callback.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                arrOrder = (ArrayList<Order>) response.body();
                if(arrOrder.size()>0){
                    adapter = new OrderAdapter(OrderActivity.this, arrOrder);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderActivity.this);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    DividerItemDecoration dividerItemDecorationvider = new DividerItemDecoration(OrderActivity.this, linearLayoutManager.getOrientation());
                    recyclerView.addItemDecoration(dividerItemDecorationvider);
                }else{
                    Toast.makeText(OrderActivity.this, "Không có thực đơn nào!", Toast.LENGTH_SHORT).show();
                }

                rotateLoading.stop();
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
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
        toolbar = findViewById(R.id.toolbar_order);
        recyclerView = findViewById(R.id.recycler_view_order);
        rotateLoading = findViewById(R.id.rotateloading);
    }
}
