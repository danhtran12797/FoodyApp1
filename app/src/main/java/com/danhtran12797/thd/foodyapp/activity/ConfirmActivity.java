package com.danhtran12797.thd.foodyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.adapter.ConfirmAdapter;
import com.danhtran12797.thd.foodyapp.model.AddressShipping;
import com.danhtran12797.thd.foodyapp.model.ShopingCart;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import maes.tech.intentanim.CustomIntent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.danhtran12797.thd.foodyapp.activity.PayActivity.id_delivery;
import static com.danhtran12797.thd.foodyapp.activity.PayActivity.id_payment;

public class ConfirmActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ConfirmActivity";

    private Toolbar toolbar;
    private Button btn_confirm;
    private TextView txt_total_all_price;
    private TextView txt_total_price;
    private TextView txt_transport;
    private RecyclerView recyclerView;
    private TextView txt_delivery;
    private TextView txt_payment;
    private TextView txt_name;
    private TextView txt_phone;
    private TextView txt_address;
    private TextView txt_request;
    private RotateLoading rotateLoading;

    private double money_transport = 0;
    private double all_money = 0;
    private AddressShipping address;
    private String delivery;
    private String payment;

    DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        initView();
        initActionBar();
        setViewDelivery();
        setViewPayment();
        setViewAddressUser();
        setAdapter();

        txt_total_price.setText(decimalFormat.format(getTotalPriceProduct()) + " VNĐ");
        setAllMoney();
    }

    private double getTotalPriceProduct() {
        double s = 0;
        for (ShopingCart shopingCart : Ultil.arrShoping) {
            s += shopingCart.getPrice() * shopingCart.getQuantity();
        }
        return s;
    }

    private void setAllMoney() {
        all_money = getTotalPriceProduct() + money_transport;
        txt_total_all_price.setText(decimalFormat.format(all_money) + " VNĐ");
    }

    private void setViewAddressUser() {
        for (AddressShipping addressShipping : Ultil.arrAddressShipping) {
            if (addressShipping.isCheck()) {
                address = addressShipping;
                txt_name.setText(address.getName());
                txt_phone.setText(address.getPhone());
                txt_address.setText(address.getAddress());
                return;
            }
        }
    }

    private void setViewDelivery() {
        delivery = id_delivery;
        if (delivery.equals("1")) {
            txt_delivery.setText("Giao hàng tiêu chuẩn(miễn phí)");
        } else {
            txt_delivery.setText("Shop giao nhanh(30.000 VNĐ)");
            txt_transport.setText("30,000 VNĐ");
            money_transport = 30000;
        }
    }

    private void setViewPayment() {
        payment = id_payment;
        if (payment.equals("1")) {
            txt_payment.setText("Thanh toán tiền mặt khi nhận hàng");
        } else if (payment.equals("2")) {
            txt_payment.setText("Thẻ ATM nội địa/Internet Banking(Miễn phí thanh toán)");
        } else {
            txt_payment.setText("Thanh toán bắng thẻ quốc tế Visa, Master, JCB");
        }
    }

    private void setAdapter() {
        ConfirmAdapter adapter = new ConfirmAdapter(this, Ultil.arrShoping);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecorationvider = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecorationvider);
    }

    private void initView() {
        rotateLoading = findViewById(R.id.rotateloading);
        toolbar = findViewById(R.id.toolbar_confirm);
        btn_confirm = findViewById(R.id.btn_confirm);
        txt_total_all_price = findViewById(R.id.txt_total_price_product_confirm);
        txt_total_price = findViewById(R.id.txt_total_price_confirm);
        txt_transport = findViewById(R.id.txt_trans_confirm);
        recyclerView = findViewById(R.id.recyler_view_confirm);
        txt_delivery = findViewById(R.id.txt_delivery_confirm);
        txt_payment = findViewById(R.id.txt_payment_confirm);
        txt_name = findViewById(R.id.txt_name_confirm);
        txt_phone = findViewById(R.id.txt_phone_confirm);
        txt_address = findViewById(R.id.txt_address_confirm);
        txt_request = findViewById(R.id.txt_request);

        btn_confirm.setOnClickListener(this);
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

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "fadein-to-fadeout");
    }

    private void confirm_order_detail(String json_shoping) {
        DataService dataService = APIService.getService();
        Call<String> callback = dataService.OrdersDetail(json_shoping);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String message = response.body();
                Log.d(TAG, "confirm_order_detail: " + message);
                if (message.equals("success")) {
                    rotateLoading.stop();
                    Ultil.removeShopingCart(ConfirmActivity.this);
                    Intent intent = new Intent(ConfirmActivity.this, OrderSuccessActivity.class);
                    startActivity(intent);
                    finish();
                    CustomIntent.customType(ConfirmActivity.this, "fadein-to-fadeout");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                rotateLoading.stop();
                Log.d(TAG, "error: " + t.getMessage());
            }
        });
    }

    private void confirm_order(final String request) {
        rotateLoading.start();

        DataService dataService = APIService.getService();
        Call<String> callback = dataService.Orders(Ultil.user.getId(), address.getName()
                , address.getPhone(), address.getAddress(), request
                , Ultil.user.getEmail(), delivery, payment);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String id_order = response.body();
                Log.d(TAG, "id_order: " + id_order);
                if (Integer.parseInt(id_order) > 0) {
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < Ultil.arrShoping.size(); i++) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("id_order", id_order);
                            jsonObject.put("price", Ultil.arrShoping.get(i).getPrice());
                            jsonObject.put("quantity", Ultil.arrShoping.get(i).getQuantity());
                            jsonObject.put("name", Ultil.arrShoping.get(i).getName());
                            jsonObject.put("id_product", Ultil.arrShoping.get(i).getId());
                            jsonObject.put("image", Ultil.arrShoping.get(i).getImage());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        jsonArray.put(jsonObject);
                    }
                    confirm_order_detail(jsonArray.toString());
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                rotateLoading.stop();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                confirm_order(txt_request.getText().toString());
                break;
        }
    }
}
