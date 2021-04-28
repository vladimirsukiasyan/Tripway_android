package com.tiparo.tripway.views.adapters;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tiparo.tripway.R;
import com.tiparo.tripway.dao.PageDataDao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RecycleViewAdapter extends BaseQuickAdapter<PageDataDao, BaseViewHolder> {

    private List<PageDataDao> mDataList;

    public RecycleViewAdapter(int layoutResId, @Nullable List<PageDataDao> data) {
        super(layoutResId, data);
        mDataList = data;
    }

    public RecycleViewAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, PageDataDao pageDataDao) {
        baseViewHolder.setText(R.id.largeTitle, pageDataDao.getLargeTitle());
        baseViewHolder.setText(R.id.likecount, pageDataDao.getLikeCount());
        baseViewHolder.setText(R.id.title, pageDataDao.getTitle());
        baseViewHolder.setText(R.id.time, pageDataDao.getUpdateTime());
    }
}
