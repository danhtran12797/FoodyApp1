package com.danhtran12797.thd.foodyapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
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

public class ListUserProductLoveActivity extends AppCompatActivity {

    private static final String TAG = "ListUserProductLoveActi";

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayout layout;
    private LoveProductAdapter adapter;
    private ArrayList<Product> arrProduct = null;
    private RotateLoading rotateLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user_product_love);

        initView();
        initActionBar();
        if (Ultil.isNetworkConnected(this)) {
            loadData();
            Toast.makeText(this, "Trượt ngang để xóa thực đơn bạn thích", Toast.LENGTH_SHORT).show();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.layout_container, new ConnectionFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_product_love, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_remove_all_list_product_love:
                if (arrProduct != null) {
                    if(arrProduct.size()!=0){
                        deleteAllUserProduct();
                        adapter.deleteAllItem();
                        close_layout_list_product();
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAdapter(final ArrayList<Product> arrProduct) {
        adapter = new LoveProductAdapter(ListUserProductLoveActivity.this, arrProduct);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                deleteUserLoveProduct(arrProduct.get(position));
                adapter.deleteItem(position);
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void deleteAllUserProduct() {
        DataService dataService = APIService.getService();
        Call<String> callback = dataService.DeleteAllUserLoveProduct(Ultil.user.getId());
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String message = response.body();
                Log.d(TAG, message);
                if (message.equals("success")) {
                    Toast.makeText(ListUserProductLoveActivity.this, "Đã xóa tất cả thực đơn trong danh sách yêu thích của bạn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void deleteUserLoveProduct(final Product product) {
        DataService dataService = APIService.getService();
        Call<String> callback = dataService.DeleteUserLoveProduct(product.getId(), Ultil.user.getId());
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String message = response.body();
                Log.d(TAG, message);
                if (message.equals("success")) {
                    Toast.makeText(ListUserProductLoveActivity.this, "Đã xóa thực đơn '" + product.getName() + "' trong danh sách yêu thích của bạn", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "size1 :" + arrProduct.size());
                    if (arrProduct.size() == 0) {
                        close_layout_list_product();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void loadData() {
        rotateLoading.start();
        DataService dataService = APIService.getService();
        Call<List<Product>> callback = dataService.GetProductUserLove(Ultil.user.getId());
        callback.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                arrProduct = (ArrayList<Product>) response.body();
                if (arrProduct.size() > 0) {
                    open_layout_list_product();
                    setAdapter(arrProduct);
                } else {
                    close_layout_list_product();
                }
                rotateLoading.stop();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                rotateLoading.stop();
            }
        });
    }

    private void open_layout_list_product() {
        recyclerView.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
    }

    private void close_layout_list_product() {
        recyclerView.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
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
        toolbar = findViewById(R.id.toolbar_list_user_product_love);
        recyclerView = findViewById(R.id.recyclerView_list_user_product_love);
        layout = findViewById(R.id.layout_no_product_love);
        rotateLoading = findViewById(R.id.rotateloading);
    }
}
