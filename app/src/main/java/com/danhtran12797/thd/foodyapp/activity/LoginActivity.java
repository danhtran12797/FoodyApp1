package com.danhtran12797.thd.foodyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.adapter.LoginAdapter;
import com.danhtran12797.thd.foodyapp.fragment.LoginFragment;
import com.danhtran12797.thd.foodyapp.fragment.SignupFragment;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.google.android.material.tabs.TabLayout;

public class LoginActivity extends AppCompatActivity implements SignupFragment.SignupFragmentListener {

    TabLayout tabLogin;
    ViewPager viewPager;
    LoginAdapter loginAdapter;
    int postion;
    LoginFragment loginFragment;
    SignupFragment registerFragment;

    Intent intent = null;
    Product product = null;
    boolean check_love_product = false;
    boolean check_order = false;

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
            loginFragment = LoginFragment.getInstance(product, check_love_product, check_order);
            postion = 0;
            Log.d("AAA", "user_love_product: " + postion);
        } else if (intent.hasExtra("login_register")) {
            postion = getIntent().getIntExtra("login_register", -1);
            Log.d("AAA", "login_register: " + postion);
        } else if (intent.hasExtra("list_love_product_of_user")) {
            check_love_product = intent.getBooleanExtra("list_love_product_of_user", false);
            loginFragment = LoginFragment.getInstance(product, check_love_product, check_order);
            postion = 0;
            Log.d("AAA", "list_love_product_of_user: " + postion);
        } else if (intent.hasExtra("list_order_of_user")) {
            check_order = intent.getBooleanExtra("list_order_of_user", false);
            loginFragment = LoginFragment.getInstance(product, check_love_product, check_order);
            postion = 0;
        }
    }

    private void setLoginAdapter() {
        loginAdapter = new LoginAdapter(getSupportFragmentManager());
        loginAdapter.addFragment(loginFragment, "Login");
        loginAdapter.addFragment(registerFragment, "Signup");

        viewPager.setAdapter(loginAdapter);
        tabLogin.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(postion);
    }

    private void initView() {
        tabLogin = findViewById(R.id.tabLogin);
        viewPager = findViewById(R.id.viewPagerLogin);
    }

    @Override
    public void onInputRegister(String username) {
        viewPager.setCurrentItem(0);
        loginFragment.setTextUsername(username);
    }
}
