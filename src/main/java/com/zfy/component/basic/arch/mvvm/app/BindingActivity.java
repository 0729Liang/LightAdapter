package com.zfy.component.basic.arch.mvvm.app;

import android.databinding.ViewDataBinding;

import com.zfy.component.basic.arch.base.app.AppActivity;
import com.zfy.component.basic.arch.base.app.AppDelegate;
import com.zfy.component.basic.arch.mvvm.BaseViewModel;
import com.zfy.component.basic.arch.mvvm.IBindingView;

/**
 * CreateAt : 2018/9/11
 * Describe : Mvvm Binding Activity
 *
 * @author chendong
 */
public abstract class BindingActivity<VM extends BaseViewModel, VDB extends ViewDataBinding>
        extends AppActivity
        implements IBindingView<VM, VDB> {

    protected MvvmDelegate<VM, VDB> mDelegate = new MvvmDelegate<>();

    @Override
    public VDB binding() {
        return mDelegate.binding();
    }

    @Override
    public VM viewModel() {
        return mDelegate.viewModel();
    }

    @Override
    public <E extends BaseViewModel> E provideViewModel(Class<E> clazz) {
        return mDelegate.provideViewModel(clazz);
    }

    @Override
    public AppDelegate getAppDelegate() {
        return mDelegate;
    }

}
