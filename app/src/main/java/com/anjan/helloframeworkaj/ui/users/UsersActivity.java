package com.anjan.helloframeworkaj.ui.users;

import android.Manifest;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;

import com.anjan.helloframeworkaj.databinding.ActivityUsersBinding;

import com.anjan.helloframeworkaj.R;
import com.anjan.helloframeworkaj.data.ViewModelCreator;
import com.anjan.helloframeworkaj.data.local.MyInfoProvider;
import com.anjan.helloframeworkaj.data.remote.model.UserEntityResponse;

import core.left.io.framework.application.data.BaseServiceLocator;
import core.left.io.framework.application.data.helper.BaseResponse;
import core.left.io.framework.application.ui.base.rm.RmBaseActivity;
import core.left.io.framework.util.collections.CollectionUtil;
import core.left.io.framework.util.helper.PermissionUtil;
import core.left.io.framework.util.helper.Toaster;


public class UsersActivity extends RmBaseActivity {

    private UsersViewModel mUsersViewModel;

    private UsersAdapter mUsersAdapter;
    private ViewModelCreator viewModelCreator;
    ActivityUsersBinding mActivityUsersBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_users;
    }

    @Override
    protected void startUI() {

        if (PermissionUtil.on(this).request(Manifest.permission.GET_ACCOUNTS)) {
            initData();
        }

    }

    private void initData() {

        // as `activity_users` is the layout name so the data binding will generate a ActivityUsersBinding
        mActivityUsersBinding = (ActivityUsersBinding) getViewDataBinding();

        // as variable name="userEntity" in layout and type is `UserEntity` so it will take UserEntity model
        mActivityUsersBinding.setUserEntity(new MyInfoProvider(getApplicationContext()).getMyProfileInfo());
        mUsersViewModel = getViewModel();

        initRecyclerView();

        mUsersViewModel.getUsersLiveData().observe(this, userEntityResponse ->
                onResponse(userEntityResponse, getString(R.string.users_unable_to_process)));
    }

    @Override
    protected <T extends BaseResponse> void onSuccessResponse(@NonNull T baseResponse) {
        super.onSuccessResponse(baseResponse);

        UserEntityResponse userEntityResponse = (UserEntityResponse) baseResponse;

        if (userEntityResponse.state == UserEntityResponse.ADDED) {

            mUsersAdapter.addItem(userEntityResponse.mUserEntity);

        } else if (userEntityResponse.state == UserEntityResponse.GONE) {

            mUsersAdapter.removeItem(userEntityResponse.mUserEntity);

        }
    }

    private void initRecyclerView() {
        mUsersAdapter = new UsersAdapter();
        mActivityUsersBinding.rv.setAdapter(mUsersAdapter);
        mActivityUsersBinding.rv.setLayoutManager(new LinearLayoutManager(this));
    }


    @SuppressWarnings("unchecked")
    private UsersViewModel getViewModel() {

        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {

            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

                viewModelCreator = ViewModelCreator.getInstance();
                return (T) viewModelCreator.getUsersModel(getApplicationContext());

            }

        }).get(UsersViewModel.class);

    }

    @Override
    protected BaseServiceLocator getServiceLocator() {
        return viewModelCreator;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (CollectionUtil.hasItem(permissions) && permissions[0].equals(Manifest.permission.GET_ACCOUNTS)) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initData();
            } else {
                Toaster.showLong(getString(R.string.user_activity_permission_required));
            }
        }
    }
}
