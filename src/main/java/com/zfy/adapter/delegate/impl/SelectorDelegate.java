package com.zfy.adapter.delegate.impl;

import com.zfy.adapter.LightHolder;
import com.zfy.adapter.able.Selectable;
import com.zfy.adapter.common.LightValues;

import java.util.List;

/**
 * CreateAt : 2018/11/1
 * Describe :
 *
 * @author chendong
 */
public class SelectorDelegate extends BaseDelegate {

    public interface SelectorBinder {
        void onBindSelectableViewHolder(LightHolder holder, int position, Selectable obj);
    }

    private SelectorBinder mSelectorBinder;
    private int mSelectType;

    public void setSelectorBinder(int type, SelectorBinder selectorBinder) {
        mSelectorBinder = selectorBinder;
        mSelectType = type;
    }

    @Override
    public int getKey() {
        return SELECTOR;
    }

    @Override
    public boolean onBindViewHolder(LightHolder holder, int position) {
        if (mSelectorBinder == null) {
            return super.onBindViewHolder(holder, position);
        }
        int pos = mAdapter.toModelIndex(position);
        Object data = mAdapter.getItem(pos);
        if (data instanceof Selectable) {
            mSelectorBinder.onBindSelectableViewHolder(holder, position, (Selectable) data);
        }
        return super.onBindViewHolder(holder, position);
    }


    // 单选时，选中一个，取消选中其他的
    private void releaseOthers(Selectable selectable) {
        List datas = mAdapter.getDatas();
        for (Object data : datas) {
            if (data.equals(selectable)) {
                continue;
            }
            if (data instanceof Selectable) {
                Selectable item = (Selectable) data;
                if (item.isSelected()) {
                    releaseItem(item);
                }
            }
        }
    }

    /**
     * 选中某一项
     *
     * @param selectable 继承 Selectable 接口
     */
    public void selectItem(Selectable selectable) {
        if (selectable.isSelected()) {
            return;
        }
        if (mSelectType == LightValues.SINGLE) {
            releaseOthers(selectable);
        }
        selectable.setSelected(true);
        int pos = mAdapter.getDatas().indexOf(selectable);
        int layoutIndex = mAdapter.toLayoutIndex(pos);
        LightHolder holder = (LightHolder) mAdapter.getRecyclerView().findViewHolderForLayoutPosition(layoutIndex);
        if (holder != null) {
            mSelectorBinder.onBindSelectableViewHolder(holder, pos, selectable);
        } else {
            mAdapter.notifyItem().change(layoutIndex);
        }
    }

    /**
     * 释放某一项
     *
     * @param selectable 继承 Selectable 接口
     */
    public void releaseItem(Selectable selectable) {
        if (!selectable.isSelected()) {
            return;
        }
        selectable.setSelected(false);
        int pos = mAdapter.getDatas().indexOf(selectable);
        int layoutIndex = mAdapter.toLayoutIndex(pos);
        LightHolder holder = (LightHolder) mAdapter.getRecyclerView().findViewHolderForLayoutPosition(layoutIndex);
        if (holder != null) {
            mSelectorBinder.onBindSelectableViewHolder(holder, pos, selectable);
        } else {
            mAdapter.notifyItem().change(layoutIndex);
        }
    }

    /**
     * 选中改为不选中，不选中改为选中
     *
     * @param selectable 继承 Selectable 接口
     */
    public void toggleItem(Selectable selectable) {
        if (selectable.isSelected()) {
            releaseItem(selectable);
        } else {
            selectItem(selectable);
        }
    }
}
