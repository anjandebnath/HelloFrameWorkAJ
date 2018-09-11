package com.anjan.helloframeworkaj.ui.users;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.support.annotation.NonNull;

import com.anjan.helloframeworkaj.data.UserDataSource;
import com.anjan.helloframeworkaj.data.remote.model.UserEntityResponse;

import core.left.io.framework.application.ui.base.BaseRxViewModel;



/**
 * ============================================================================
 * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2018-08-07 at 12:33 PM].
 * <br>Email: azim@w3engineers.com
 * <br>----------------------------------------------------------------------------
 * <br>Project: android-framework.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2018-08-07 at 12:33 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2018-08-07 at 12:33 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
public class UsersViewModel extends BaseRxViewModel {

    private LiveData<UserEntityResponse> mUsersLiveData;

    public UsersViewModel(@NonNull UserDataSource userDataSource) {
        mUsersLiveData = LiveDataReactiveStreams.fromPublisher(userDataSource.
                getAllUsers());
    }


    public LiveData<UserEntityResponse> getUsersLiveData() {
        return mUsersLiveData;
    }
}
