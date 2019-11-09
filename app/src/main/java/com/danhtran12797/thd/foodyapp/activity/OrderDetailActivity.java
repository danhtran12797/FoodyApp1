package com.danhtran12797.thd.foodyapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.adapter.OrderDetailAdapter;
import com.danhtran12797.thd.foodyapp.model.Order;
import com.danhtran12797.thd.foodyapp.model.OrderDetail;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.victor.loading.rotate.RotateLoading;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity implements OrderDetailAdapter.IOrderDetail {

    private static final String TAG = "OrderDetailActivity";

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView txt_code;
    private TextView txt_date;
    private TextView txt_state;
    private TextView txt_address;
    private TextView txt_phone;
    private TextView txt_name_user;
    private TextView txt_delivery;
    private TextView txt_payment;
    private TextView txt_total;
    private TextView txt_money_trans;
    private TextView txt_total_all;
    private Button btn_cancle;

    private RotateLoading rotateLoading;

    private Order order;
    private double money_transport = 0;
    private double all_money = 0;
    DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Intent intent = getIntent();
        order = (Order) intent.getSerializableExtra("order_detail");

        initView();
        initActionBar();
        setDataView();
        setViewDelivery();
        setViewPayment();

        txt_total.setText(decimalFormat.format(getTotalPriceProduct()) + " VNĐ");
        setAllMoney();
    }

    private void Update_Order_Detail() {
        DataService dataService = APIService.getService();
        Call<String> callback = dataService.Update_Order_Detail(order.getId());
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String message = response.body();
                Log.d(TAG, "onResponse: " + message);
                if (message.equals("success")) {
                    Intent intent = new Intent(OrderDetailActivity.this, OrderActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(OrderDetailActivity.this, "Hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void dialog_question_cancle() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(System.currentTimeMillis() + order.getId());
        builder.setIcon(R.drawable.icon_app_design);
        builder.setMessage("Lý do bạn xóa đơn hàng này?");
        EditText editText = new EditText(this);
        builder.setView(editText);
        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Update_Order_Detail();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private double getTotalPriceProduct() {
        double s = 0;
        for (OrderDetail orderDetail : order.getOrderDetail()) {
            s += Double.parseDouble(orderDetail.getPrice()) * Integer.parseInt(orderDetail.getQuantityProduct());
        }
        return s;
    }

    private void setAllMoney() {
        all_money = getTotalPriceProduct() + money_transport;
        txt_total_all.setText(decimalFormat.format(all_money) + " VNĐ");
    }

    private void setDataView() {
        OrderDetailAdapter adapter = new OrderDetailAdapter(OrderDetailActivity.this, (ArrayList<OrderDetail>) order.getOrderDetail());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderDetailActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecorationvider = new DividerItemDecoration(OrderDetailActivity.this, linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecorationvider);

        txt_date.setText(order.getDate());
        txt_code.setText(System.currentTimeMillis() + order.getId());
        if (order.getStatus().equals("1")) {
            txt_state.setText("Đang xử lý");
            btn_cancle.setVisibility(View.VISIBLE);
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_question_cancle();
                }
            });
        } else if (order.getStatus().equals("2")) {
            txt_state.setText("Đã nhận được hàng");
            btn_cancle.setVisibility(View.GONE);
        } else {
            txt_state.setText("Đã hủy");
            btn_cancle.setVisibility(View.GONE);
        }

        txt_name_user.setText(order.getName());
        txt_phone.setText(order.getPhone());
        txt_address.setText(order.getAddress());
    }

    private void setViewDelivery() {
        String delivery = order.getDelivery();
        if (delivery.equals("1")) {
            txt_delivery.setText("Giao hàng tiêu chuẩn(miễn phí)");
        } else {
            txt_delivery.setText("Shop giao nhanh(30.000 VNĐ)");
            txt_money_trans.setText("30,000 VNĐ");
            money_transport = 30000;
        }
    }

    private void setViewPayment() {
        String payment = order.getPayment();
        if (payment.equals("1")) {
            txt_payment.setText("Thanh toán tiền mặt khi nhận hàng");
        } else if (payment.equals("2")) {
            txt_payment.setText("Thẻ ATM nội địa/Internet Banking(Miễn phí thanh toán)");
        } else {
            txt_payment.setText("Thanh toán bắng thẻ quốc tế Visa, Master, JCB");
        }
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
        rotateLoading = findViewById(R.id.rotateloading);

        toolbar = findViewById(R.id.toolbar_order_detail);
        recyclerView = findViewById(R.id.recyler_view_order_detail);
        txt_code = findViewById(R.id.txt_code_order_detail);
        txt_date = findViewById(R.id.txt_date_order_detail);
        txt_state = findViewById(R.id.txt_state_order_detail);
        txt_name_user = findViewById(R.id.txt_name_user_order_detail);
        txt_phone = findViewById(R.id.txt_phone_user_order_detail);
        txt_address = findViewById(R.id.txt_address_user_order_detail);
        txt_delivery = findViewById(R.id.txt_delivery_order_detail);
        txt_payment = findViewById(R.id.txt_payment_order_detail);
        txt_total = findViewById(R.id.txt_total_price_order_detail);
        txt_total_all = findViewById(R.id.txt_total_price_product_order_detail);
        txt_money_trans = findViewById(R.id.txt_trans_order_detail);
        btn_cancle = findViewById(R.id.btn_cancle_order_detail);
    }

    private void getProduct(String id_product) {
        rotateLoading.start();

        DataService dataService = APIService.getService();
        Call<List<Product>> callback = dataService.GetProduct(id_product);
        callback.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                rotateLoading.stop();
                Log.d(TAG, "onResponse: YES");
                ArrayList<Product> arrayList = (ArrayList<Product>) response.body();
                Product product = arrayList.get(0);
                Intent intent = new Intent(OrderDetailActivity.this, DetailProductActivity.class);
                intent.putExtra("detail_product", product);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                rotateLoading.stop();
            }
        });
    }

    @Override
    public void itemClick(int position) {
        getProduct(order.getOrderDetail().get(position).getIdProduct());
    }
}
