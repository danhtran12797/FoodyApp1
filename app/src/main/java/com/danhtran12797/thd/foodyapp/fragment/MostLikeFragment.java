package com.danhtran12797.thd.foodyapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.adapter.LoveProductAdapter;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MostLikeFragment extends Fragment {

    View view;
    RotateLoading rotateLoading;
    RecyclerView recyclerView;
    LoveProductAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("CCC", "onAttach MostLikeFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("CCC", "onResume MostLikeFragment");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("CCC", "onCreateView MostLikeFragment");
        view = inflater.inflate(R.layout.fragment_most_like, container, false);
        initView();
        getData();
        return view;
    }

    private void initView() {
        rotateLoading = view.findViewById(R.id.rotateloading);
        recyclerView = view.findViewById(R.id.recyerViewLove);
    }

    private void getData() {
        rotateLoading.start();
        DataService dataService = APIService.getService();
        Call<List<Product>> callback = dataService.GetLoveProduct();
        callback.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                ArrayList<Product> arrLoveProduct = (ArrayList<Product>) response.body();
                adapter = new LoveProductAdapter(getActivity(), arrLoveProduct);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(adapter);
                rotateLoading.stop();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                rotateLoading.stop();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("CCC", "onDestroyView MostLikeFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("CCC", "onDestroy MostLikeFragment");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("CCC", "onDetach MostLikeFragment");
    }
}
