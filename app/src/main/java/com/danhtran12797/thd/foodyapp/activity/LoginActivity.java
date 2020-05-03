package com.danhtran12797.thd.foodyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.adapter.LoginAdapter;
import com.danhtran12797.thd.foodyapp.fragment.LoginFragment;
import com.danhtran12797.thd.foodyapp.fragment.SignupFragment;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.google.android.material.tabs.TabLayout;
import com.victor.loading.rotate.RotateLoading;

public class LoginActivity extends AppCompatActivity implements SignupFragment.SignupFragmentListener, ILoading {

    TabLayout tabLogin;
    ViewPager viewPager;
    LoginAdapter loginAdapter;
    int postion;
    LoginFragment loginFragment;
    SignupFragment registerFragment;
    RotateLoading rotateLoading;
    FrameLayout layout_container;
    RelativeLayout layout_login;

    Intent intent = null;
    Product product = null;
    String login_to = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        get_intent();
        setLoginAdapter();
    }

    private void get_intent() {
        loginFragment = new LoginFragment();
        registerFragment = new SignupFragment();

        intent = getIntent();
        if (intent.hasExtra("user_love_product")) {
            product = (Product) intent.getSerializableExtra("user_love_product");
            login_to = intent.getStringExtra("login_to");
            loginFragment = LoginFragment.getInstance(product, login_to);
            postion = 0;
        } else if (intent.hasExtra("login_register")) {
            postion = getIntent().getIntExtra("login_register", -1);
        } else if (intent.hasExtra("login_to")) {
            login_to = intent.getStringExtra("login_to");
            loginFragment = LoginFragment.getInstance(product, login_to);
            postion = 0;
        }
    }

    private void setLoginAdapter() {
        loginAdapter = new LoginAdapter(getSupportFragmentManager());
        loginAdapter.addFragment(loginFragment, "Đăng nhập");
        loginAdapter.addFragment(registerFragment, "Đăng ký");

        viewPager.setAdapter(loginAdapter);
        tabLogin.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(postion);
    }

    private void initView() {
        tabLogin = findViewById(R.id.tabLogin);
        viewPager = findViewById(R.id.viewPagerLogin);
        rotateLoading=findViewById(R.id.rotateLoading);
        layout_container=findViewById(R.id.layout_container);
        layout_login=findViewById(R.id.layout_login);
        layout_container.setVisibility(View.GONE);
    }

    @Override
    public void onInputRegister(String username) {
        viewPager.setCurrentItem(0);
        loginFragment.setTextUsername(username);
    }

    @Override
    public void start_loading() {
        rotateLoading.start();
        layout_container.setVisibility(View.VISIBLE);
    }

    @Override
    public void stop_loading(boolean isConnect) {
        rotateLoading.stop();
        layout_container.setVisibility(View.GONE);
        if(!isConnect){
            Ultil.show_snackbar(layout_login, null);
        }
    }
}
