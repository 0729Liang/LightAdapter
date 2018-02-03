package com.march.lightadapter.listener;

import com.march.lightadapter.LightHolder;

/**
 * CreateAt : 16/8/20
 * Describe : 选择 module 更新器
 *
 * @author chendong
 */
public interface OnHolderUpdateListener<D> {
    void onChanged(LightHolder<D> holder, D data, int pos, boolean isSelect);
}