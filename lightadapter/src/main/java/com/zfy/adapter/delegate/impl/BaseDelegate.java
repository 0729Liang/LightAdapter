package com.zfy.adapter.delegate.impl;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.zfy.adapter.LightAdapter;
import com.zfy.adapter.LightHolder;
import com.zfy.adapter.common.ItemType;
import com.zfy.adapter.delegate.IDelegate;

import java.util.ArrayList;
import java.util.List;

/**
 * CreateAt : 2018/10/28
 * Describe : 委托基类
 *
 * @author chendong
 */
public abstract class BaseDelegate implements IDelegate {

    protected LightAdapter mAdapter;
    protected RecyclerView mView;

    boolean isAttached() {
        return mAdapter != null && mView != null;
    }

    protected List<Runnable> mAttachRecyclerViewPendingRunnables = new ArrayList<>();

    protected void postOnRecyclerViewAttach(Runnable runnable) {
        if (isAttached() && mView.getLayoutManager() != null) {
            runnable.run();
        } else {
            mAttachRecyclerViewPendingRunnables.add(runnable);
        }
    }

    @Override
    public LightHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull LightHolder holder) {

    }

    @Override
    public boolean onBindViewHolder(LightHolder holder, int layoutIndex) {
        return false;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mView = recyclerView;
        if (!mAttachRecyclerViewPendingRunnables.isEmpty()) {
            for (Runnable pendingRunnable : mAttachRecyclerViewPendingRunnables) {
                pendingRunnable.run();
            }
            mAttachRecyclerViewPendingRunnables.clear();
        }
    }

    @Override
    public void onAttachAdapter(LightAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getTopItemCount() {
        return 0;
    }

    @Override
    public int getAboveItemCount(int level) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return ItemType.TYPE_NONE;
    }
}
