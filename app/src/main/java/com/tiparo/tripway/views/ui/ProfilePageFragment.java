package com.tiparo.tripway.views.ui;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tiparo.tripway.R;
import com.tiparo.tripway.dao.PageDataDao;
import com.tiparo.tripway.databinding.FragmentProfilePageBinding;
import com.tiparo.tripway.views.adapters.RecycleViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ProfilePageFragment extends Fragment {

    FragmentProfilePageBinding binding;
    RecycleViewAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_page, container, false);
        binding.setLifecycleOwner(this);

        initData();
        return binding.getRoot();
    }

    private void initData() {
        List<PageDataDao> mDataList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(Calendar.getInstance().getTimeInMillis());
            String res = simpleDateFormat.format(date);

            PageDataDao pageDataDao = new PageDataDao();
            //pageDataDao.setImage();
            pageDataDao.setLargeTitle("LargeTitle-" + i);
            pageDataDao.setTitle("title-" + i);
            pageDataDao.setUpdateTime(res);
            pageDataDao.setLikeCount(i + "");
            mDataList.add(pageDataDao);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new RecycleViewAdapter(R.layout.item_content_profile, mDataList);
        //binding.profileRecycleview.setLayoutManager(linearLayoutManager);
        binding.profileRecycleview.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
