package com.danhtran12797.thd.foodyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.adapter.SelectAddressAdapter;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;

import maes.tech.intentanim.CustomIntent;

public class AddressOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView txtAddAddress;
    private Button btnAddAddress;
    private RecyclerView recyclerView;
    private SelectAddressAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_order);

        initView();
        initActionBar();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_address_order);
        txtAddAddress = findViewById(R.id.txt_add_new_address);
        btnAddAddress = findViewById(R.id.btn_shipping_address);
        recyclerView = findViewById(R.id.recycler_view_select_address);

        btnAddAddress.setOnClickListener(this);
        txtAddAddress.setOnClickListener(this);

        setAdapter();
    }

    private void dummy_data() {
//        arrAddressShipping=new ArrayList<>();
//        arrAddressShipping.add(new AddressShipping("Danh Trần", "409 Lê Quang Định", "01234567",false));
//        arrAddressShipping.add(new AddressShipping("Lan Trần", "63 Võ Thị Sáu", "01234567",false));
//        arrAddressShipping.add(new AddressShipping("Nam Nguyễn", "11/4 Hai Bà Trưng", "01234567",true));
//        arrAddressShipping.add(new AddressShipping("Hà Anh Phi", "26/2 Bạch Đăng", "01234567",false));
    }

    private int getPositionCheck() {
        for (int i = 0; i < Ultil.arrAddressShipping.size(); i++) {
            if (Ultil.arrAddressShipping.get(i).isCheck() == true) {
                return i;
            }
        }
        return 0;
    }

    private void setAdapter() {
        Ultil.arrAddressShipping = Ultil.getAddressShipping(this);
        if (Ultil.arrAddressShipping == null) {
            startActivity(new Intent(this, AddLocationOrderActivity.class));
            finish();
        } else {
            adapter = new SelectAddressAdapter(this, Ultil.arrAddressShipping, getPositionCheck());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            DividerItemDecoration dividerItemDecorationvider = new DividerItemDecoration(this, linearLayoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecorationvider);
            recyclerView.setAdapter(adapter);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_shipping_address:
                if (Ultil.arrAddressShipping.size() == 0) {
                    Toast.makeText(this, "Vui lòng thêm địa chỉ mới", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(this, PayActivity.class));
                    CustomIntent.customType(this, "fadein-to-fadeout");
                }
                break;
            case R.id.txt_add_new_address:
                startActivity(new Intent(this, AddLocationOrderActivity.class));
                CustomIntent.customType(this, "fadein-to-fadeout");
                break;
        }
    }
}
