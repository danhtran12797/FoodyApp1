package com.danhtran12797.thd.foodyapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.DetailProductActivity;
import com.danhtran12797.thd.foodyapp.activity.ListUserProductLoveActivity;
import com.danhtran12797.thd.foodyapp.activity.OrderActivity;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.model.User;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    View view;
    EditText edtEmail;
    EditText edtPass;
    Button btnLogin;
    RotateLoading rotateLoading;

    Intent intent;

    Product product = null;
    boolean is_user_seen_love_product = false;
    boolean is_user_seen_order = false;

    public static LoginFragment getInstance(Product product, boolean check1, boolean check2) {
        LoginFragment fragment = new LoginFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", product);
        bundle.putSerializable("list_love_product_of_user", check1);
        bundle.putSerializable("list_order_of_user", check2);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            product = (Product) bundle.getSerializable("product");
            is_user_seen_love_product = bundle.getBoolean("list_love_product_of_user");
            is_user_seen_order = bundle.getBoolean("list_order_of_user");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        initView();
        evenLogin();
        return view;
    }

    public void setTextUsername(String username) {
        edtEmail.setText(username);
        edtPass.setFocusable(true);
    }

    private void evenLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = edtEmail.getText().toString().trim();
                String password = edtPass.getText().toString().trim();

                if (validateForm(email, password)) {
                    getData(email, password);
                }
            }
        });
    }

    private void getData(String username, String password) {
        //Ultil.showProgressDialog(getContext(), null, null, null);
        rotateLoading.start();

        DataService dataService = APIService.getService();
        Call<List<User>> callback = dataService.Login(username, password);
        callback.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                //Ultil.hideProgressDialog();
                rotateLoading.stop();

                ArrayList<User> arrUser = (ArrayList<User>) response.body();
                Ultil.user = arrUser.get(0);
                Ultil.setUserPreference(getActivity());
                Toast.makeText(getContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                if (product != null) {
                    intent = new Intent(getActivity(), DetailProductActivity.class);
                    intent.putExtra("detail_product", product);
                    getActivity().startActivity(intent);
                    //getActivity().finish();
                } else if (is_user_seen_love_product) {
                    intent = new Intent(getActivity(), ListUserProductLoveActivity.class);
                    getActivity().startActivity(intent);
                    //getActivity().finish();
                } else if (is_user_seen_order) {
                    Log.d("AAA", "Order: " + is_user_seen_order);
                    intent = new Intent(getActivity(), OrderActivity.class);
                    getActivity().startActivity(intent);
                }
                Log.d("AAA", "Order: " + is_user_seen_order);
                getActivity().finish();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                //Ultil.hideProgressDialog();
                rotateLoading.stop();
                Ultil.showDialog(getContext(), null, "Bạn đăng nhập sai. Vui lòng kiểm tra lại!", null, null);
            }
        });
    }


    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (email.isEmpty()) {
            //Field can't be empty
            edtEmail.setError("Không được bỏ trống");
            valid = false;
        } else {
            edtEmail.setError(null);
        }

        if (password.isEmpty()) {
            edtPass.setError("Không được bỏ trống");
            valid = false;
        } else {
            edtPass.setError(null);
        }

        return valid;
    }

    private void initView() {
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPass = view.findViewById(R.id.edtPass);
        btnLogin = view.findViewById(R.id.btnLogin);
        rotateLoading = view.findViewById(R.id.rotateloading);
    }
}
