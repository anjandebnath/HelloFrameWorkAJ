package com.anjan.helloframeworkaj.data.remote;

import com.anjan.helloframeworkaj.data.UserDataSource;
import com.anjan.helloframeworkaj.data.remote.model.UserEntity;
import com.anjan.helloframeworkaj.data.remote.model.UserEntityResponse;
import com.anjan.util.gson.GSonHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import core.left.io.framework.App;
import core.left.io.framework.application.data.remote.BaseRmDataSource;
import core.left.io.framework.application.data.remote.model.BaseMeshData;
import core.left.io.framework.application.data.remote.model.MeshAcknowledgement;
import core.left.io.framework.application.data.remote.model.MeshData;
import core.left.io.framework.application.data.remote.model.MeshPeer;
import core.left.io.framework.util.collections.CollectionUtil;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import timber.log.Timber;

/**
 * ============================================================================
 * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2018-08-07 at 12:41 PM].
 * <br>Email: azim@w3engineers.com
 * <br>----------------------------------------------------------------------------
 * <br>Project: android-framework.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2018-08-07 at 12:41 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2018-08-07 at 12:41 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
// TODO: 8/9/2018 Separate communicator with service and communicator with above App layer. This class
// now actually serving double purpose
public class RmDataSource extends BaseRmDataSource implements UserDataSource {

    private List<UserEntity> mUserEntities;

    private interface ProfileObserver {
        void onProfile(UserEntity userEntity);
        void onPeerGone(UserEntity userEntity);
    }

    private ProfileObserver mProfileObserver;
    private static RmDataSource mRmDataSource;
    private static final Object lock = new Object();

    private RmDataSource(byte[] profileInfo) {

        super(App.getContext(), profileInfo);
        mUserEntities = new ArrayList<>();

    }

    public static synchronized RmDataSource getRmDataSource(UserEntity profileInfo) {

        if(mRmDataSource == null) {

            synchronized (lock) {

                if(mRmDataSource == null) {//In the blocking time object might have constructed from different thread
                    byte[] bytes = Objects.requireNonNull(GSonHelper.toJson(profileInfo)).getBytes();

                    mRmDataSource = new RmDataSource(bytes);
                }
            }
        }

        return mRmDataSource;
    }

    // Backpressure is buffer as we want all the peer properly and do not want to miss any intermediate
    // events
    @Override
    public Flowable<UserEntityResponse> getAllUsers() {
        return Flowable.create(e -> {

                    mProfileObserver = new ProfileObserver() {
                        @Override
                        public void onProfile(UserEntity userEntity) {

                            UserEntityResponse userEntityResponse = new UserEntityResponse(userEntity);
                            userEntityResponse.state = UserEntityResponse.ADDED;

                            e.onNext(userEntityResponse);
                        }

                        @Override
                        public void onPeerGone(UserEntity userEntity) {

                            UserEntityResponse userEntityResponse = new UserEntityResponse(userEntity);
                            userEntityResponse.state = UserEntityResponse.GONE;

                            e.onNext(userEntityResponse);

                        }
                    };

                    e.setCancellable(() -> mProfileObserver = null);

                },
                BackpressureStrategy.BUFFER);
    }





    //Direct RM part - should be transported in separate class
    @Override
    protected void onPeer(BaseMeshData meshPeer) {
        // TODO: 8/8/2018 Use gson to prepare model from byte data
        Timber.d("Peer Event");

        UserEntity userEntity = GSonHelper.fromJson(new String(meshPeer.mData), UserEntity.class);

        if(mProfileObserver != null && userEntity != null) {


            //Ensuring single entry of same Id. Double check
            int count = CollectionUtil.deleteFromCollection(mUserEntities,
                    Arrays.toString(meshPeer.mMeshPeer.getPeerId()));

            userEntity.setUserId(Arrays.toString(meshPeer.mMeshPeer.getPeerId()));
            mUserEntities.add(userEntity);

            mProfileObserver.onProfile(userEntity);
        }
    }

    @Override
    protected void onPeerGone(MeshPeer meshPeer) {

        if(meshPeer != null && meshPeer.getPeerId() != null) {

            List<UserEntity> returnedList = new ArrayList<>();
            int count = CollectionUtil.deleteFromCollection(mUserEntities, Arrays.toString(meshPeer.getPeerId()),
                    returnedList);
            Timber.d("%d Peer removed::based on::%s", count, Arrays.toString(meshPeer.getPeerId()));

            //Expected item count 1 only
            if(mProfileObserver != null && CollectionUtil.hasItem(returnedList)) {
                mProfileObserver.onPeerGone(returnedList.get(0));
            }
        }

    }

    @Override
    protected void onData(MeshData meshData) {

    }

    @Override
    protected void onAcknowledgement(MeshAcknowledgement meshAcknowledgement) {

    }
}
