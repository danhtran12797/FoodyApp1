package com.danhtran12797.thd.foodyapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.adapter.NewProductAdapter;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewProductFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    RotateLoading rotateLoading;
    NewProductAdapter adapter;

    @Override
    public void onResume() {
        super.onResume();
        Log.d("CCC", "onResume NewProductFragment");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("CCC", "onCreateView NewProductFragment");
        view = inflater.inflate(R.layout.fragment_new_product, container, false);
        initView();
        getData();
        return view;
    }

    private void getData() {
        rotateLoading.start();
        DataService dataService = APIService.getService();
        Call<List<Product>> callback = dataService.GetRecentProduct();
        callback.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                ArrayList<Product> arrProduct = (ArrayList<Product>) response.body();
                adapter = new NewProductAdapter(arrProduct);
                recyclerView.setAdapter(adapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                rotateLoading.stop();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                rotateLoading.stop();
            }
        });
    }

    private void initView() {
        recyclerView = view.findViewById(R.id.recyerViewNewProduct);
        rotateLoading = view.findViewById(R.id.rotateloading);
    }
}
