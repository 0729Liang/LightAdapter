package com.march.lightadapter.module;

import android.support.v7.widget.RecyclerView;

import com.march.lightadapter.LightAdapter;
import com.march.lightadapter.LightHolder;
import com.march.lightadapter.helper.LightLogger;
import com.march.lightadapter.listener.AdapterViewBinder;
import com.march.lightadapter.manager.SelectManager;

import java.util.ArrayList;
import java.util.List;

/**
 * CreateAt : 2017/3/24
 * Describe : 选择器模块
 * 支持单选多选
 * 支持holder更新优先，避免UI闪烁
 * 支持范型数据获取
 *
 * @author chendong
 */
public class SelectorModule<D> extends AbstractModule {

    public static final String TAG = SelectManager.class.getSimpleName();

    public static final int TYPE_SINGLE = 1;
    public static final int TYPE_MULTI = 2;

    private int mType;
    private List<D> mSelectDatas;
    private List<Integer> mSelectPos;
    private LightAdapter<D> mAdapter;

    public SelectorModule(LightAdapter<D> adapter, int type, AdapterViewBinder<D> binder) {
        mAdapter = adapter;
        mSelectPos = new ArrayList<>();
        mSelectDatas = new ArrayList<>();
        mType = type;
        adapter.addViewBinder(binder);
    }

    public void initSelect(int... initItems) {
        for (Integer initPos : initItems) {
            if (initPos != null && initPos >= 0) {
                mSelectPos.add(initPos);
            }
        }
    }

    private boolean checkPosition(int pos) {
        return pos >= 0 && pos < getDatas().size();
    }

    @SuppressWarnings("unchecked")
    private List<D> getDatas() {
        return mAdapter.getDatas();
    }

    // 不选中一个
    public void unSelect(int pos) {
        if (checkPosition(pos)) {
            if (mType == TYPE_MULTI) {
                mSelectDatas.remove(getDatas().get(pos));
            } else if (mType == TYPE_SINGLE) {
                mSelectDatas.clear();
            }
            updatePos(pos);
        }
    }

    // 选中一个
    public void doSelect(int pos) {
        if (checkPosition(pos)) {
            if (mType == TYPE_MULTI) {
                mSelectDatas.add(getDatas().get(pos));
            } else if (mType == TYPE_SINGLE) {
                mSelectDatas.clear();
                mSelectDatas.add(getDatas().get(pos));
            }
            updatePos(pos);
        }
    }

    // 切换，单选时去掉原来的，选中现在的，多选时，存在就去掉，不存在就添加
    public void toggle(int pos) {
        D data = getDatas().get(pos);
        // 多选模式，存在就删除，不存在就添加，更新当前项
        if (mType == TYPE_MULTI) {
            if (mSelectDatas.contains(data)) {
                unSelect(pos);
            } else {
                doSelect(pos);
            }
        } else if (mType == TYPE_SINGLE) {
            // 单选情况就是去掉原来的，添加现在的
            D lastData = mSelectDatas.size() > 0 ? mSelectDatas.get(0) : null;
            // 上一个不为空
            if (lastData != null) {
                // 当前选择和上一个选择相同不进行数据更新
                if (lastData.equals(data))
                    return;
                unSelect(getDatas().indexOf(lastData));
            }
            // 更新现在的
            doSelect(pos);
        }
    }


    public boolean isSelect(D data) {
        return mSelectDatas.contains(data);
    }

    public int size() {
        return mSelectDatas.size();
    }

    public int indexOf(D data) {
        return mSelectDatas.indexOf(data);
    }

    public void clear() {
        for (D selectData : mSelectDatas) {
            unSelect(getDatas().indexOf(selectData));
        }
        mSelectDatas.clear();
    }

    public void selectAll() {
        mSelectDatas.addAll(getDatas());
        for (D selectData : mSelectDatas) {
            doSelect(getDatas().indexOf(selectData));
        }
    }

    public D getResult() {
        if (mSelectDatas != null && mSelectDatas.size() > 0) {
            return mSelectDatas.get(0);
        }
        return null;
    }

    public List<D> getResults() {
        if (mSelectDatas != null) {
            return mSelectDatas;
        }
        return new ArrayList<>();
    }

    private boolean updatePosUseHolder(int pos) {
        if (mAdapter == null || !checkPosition(pos))
            return false;
        // 如果有header，传过来的位置是经过处理后的位置，即数据位置，返回真正的控件位置
        if (mAdapter.isHasHeader()) {
            pos = pos + 1;
            LightLogger.e(TAG, "传过来的位置是经过处理后的位置，即数据位置，返回真正的控件位置 = " + pos);
        }
        RecyclerView.ViewHolder holder = mAdapter.getRecyclerView().findViewHolderForAdapterPosition(pos);
        try {
            if (holder != null && holder instanceof LightHolder) {
                LightHolder viewHolder = (LightHolder) holder;

                if (mHolderUpdater != null && viewHolder.getData() != null) {
                    mHolderUpdater.onChanged(viewHolder, viewHolder.getData(),
                            pos, isSelect(viewHolder.getData()));
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updatePos(final int pos) {
        if (!updatePosUseHolder(pos))
            post(new Runnable() {
                @Override
                public void run() {
                    if (isSupportPos(pos))
                        mAttachAdapter.getUpdateModule().notifyItemChanged(pos);
                }
            });
    }
}

