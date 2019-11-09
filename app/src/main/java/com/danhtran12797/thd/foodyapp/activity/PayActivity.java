package com.danhtran12797.thd.foodyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.danhtran12797.thd.foodyapp.R;

import maes.tech.intentanim.CustomIntent;

public class PayActivity extends AppCompatActivity {

    private static final String TAG = "PayActivity";

    private Toolbar toolbar;
    private RadioGroup radioGroupPayment;
    private RadioGroup radioGroupDelivery;
    //    private RadioButton radio_standard;
//    private RadioButton radio_fast;
//    private RadioButton radio_money;
//    private RadioButton radio_insland;
//    private RadioButton radio_international;
    private Button btn_pay_next;

    public static String id_delivery;
    public static String id_payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        initView();
        initActionBar();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_pay);
        btn_pay_next = findViewById(R.id.btn_pay_next);
        radioGroupDelivery = findViewById(R.id.radio_group_delivery);
        radioGroupPayment = findViewById(R.id.radio_group_payment);
//        radio_standard=findViewById(R.id.radio_delivery_standard);
//        radio_fast=findViewById(R.id.radio_delivery_fast);
//        radio_money=findViewById(R.id.radio_payment_money);
//        radio_insland=findViewById(R.id.radio_payment_insland);
//        radio_international=findViewById(R.id.radio_payment_international);

        btn_pay_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id_delivery_radio = radioGroupDelivery.getCheckedRadioButtonId();
                int id_payment_radio = radioGroupPayment.getCheckedRadioButtonId();
                if (id_delivery_radio != -1 && id_payment_radio != -1) {
                    RadioButton radioButtonDelivery = findViewById(id_delivery_radio);
                    RadioButton radioButtonPayment = findViewById(id_payment_radio);
                    id_delivery = (String) radioButtonDelivery.getTag();
                    id_payment = (String) radioButtonPayment.getTag();
                    startActivity(new Intent(PayActivity.this, ConfirmActivity.class));
                    CustomIntent.customType(PayActivity.this, "fadein-to-fadeout");
                } else {
                    if (id_delivery_radio == -1) {
                        Toast.makeText(PayActivity.this, "Vui lòng chọn hình thức giao hàng", Toast.LENGTH_SHORT).show();
                    }
                    if (id_payment_radio == -1) {
                        Toast.makeText(PayActivity.this, "Vui lòng chọn hình thức thanh toán", Toast.LENGTH_SHORT).show();
                    }
                }
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

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "fadein-to-fadeout");
    }
}
