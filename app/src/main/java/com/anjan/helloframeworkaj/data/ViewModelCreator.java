package com.anjan.helloframeworkaj.data;

import android.content.Context;

import com.anjan.helloframeworkaj.data.local.MyInfoProvider;
import com.anjan.helloframeworkaj.data.remote.RmDataSource;
import com.anjan.helloframeworkaj.ui.users.UsersViewModel;

import core.left.io.framework.App;
import core.left.io.framework.application.data.BaseServiceLocator;
import core.left.io.framework.application.data.remote.BaseRmDataSource;

/**
 * Created by Anjan Debnath on 9/11/2018.
 * Copyright (c) 2018, W3 Engineers Ltd. All rights reserved.
 */
public class ViewModelCreator extends BaseServiceLocator {


    private final static ViewModelCreator SERVICE_LOCATOR = new ViewModelCreator();

    private ViewModelCreator() {}


    public static ViewModelCreator getInstance() {
        return SERVICE_LOCATOR;
    }

    public UsersViewModel getUsersModel(Context context) {

        UserDataSource userDataSource = RmDataSource.getRmDataSource(new MyInfoProvider(context).getMyProfileInfo());
        return new UsersViewModel(userDataSource);

    }

    @Override
    public BaseRmDataSource getRmDataSource() {
        return RmDataSource.getRmDataSource(new MyInfoProvider(App.getContext()).getMyProfileInfo());
    }
}
