package com.danhtran12797.thd.foodyapp.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.adapter.BannerAdapter;
import com.danhtran12797.thd.foodyapp.model.Banner;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class BannerFragment extends Fragment {

    View view;
    ViewPager viewPager;
    CircleIndicator circleIndicator;
    BannerAdapter bannerAdapter;
    Handler handler;
    Runnable runnable;
    int currnetItem;
    private static final String TAG = "BannerFragment";

    public BannerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_banner, container, false);
        initView();
        GetData();
        return view;
    }

    private void initView() {
        viewPager = view.findViewById(R.id.viewPagerBanner);
        circleIndicator = view.findViewById(R.id.circleIndicator);
    }

    private void GetData() {
        DataService dataService = APIService.getService();
        Call<List<Banner>> callback = dataService.GetDatabanner();
        callback.enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {
                ArrayList<Banner> banners = (ArrayList<Banner>) response.body();
                Log.d("BannerFragment", "size: " + banners.size());
                bannerAdapter = new BannerAdapter(getActivity(), banners);
                viewPager.setAdapter(bannerAdapter);
                circleIndicator.setViewPager(viewPager);

                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        currnetItem = viewPager.getCurrentItem();
                        currnetItem++;
                        if (currnetItem >= viewPager.getAdapter().getCount()) {
                            currnetItem = 0;
                        }
                        viewPager.setCurrentItem(currnetItem, true);
                        handler.postDelayed(runnable, 4500);
                    }
                };
                handler.postDelayed(runnable, 4500);
            }

            @Override
            public void onFailure(Call<List<Banner>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

}
