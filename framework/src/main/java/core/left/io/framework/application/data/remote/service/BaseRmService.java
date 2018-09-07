package core.left.io.framework.application.data.remote.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import core.left.io.framework.IRmCommunicator;
import core.left.io.framework.IRmServiceConnection;
import core.left.io.framework.R;
import core.left.io.framework.application.data.remote.model.BaseMeshData;
import core.left.io.framework.application.data.remote.model.MeshAcknowledgement;
import core.left.io.framework.application.data.remote.model.MeshData;
import core.left.io.framework.application.data.remote.model.MeshPeer;
import core.left.io.framework.util.lib.mesh.IMeshCallBack;
import core.left.io.framework.util.lib.mesh.MeshConfig;
import core.left.io.framework.util.lib.mesh.MeshProvider;
import timber.log.Timber;


/**
 * ============================================================================
 * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2018-08-07 at 1:10 PM].
 * <br>Email: azim@w3engineers.com
 * <br>----------------------------------------------------------------------------
 * <br>Project: android-framework.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2018-08-07 at 1:10 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2018-08-07 at 1:10 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
// TODO: 8/8/2018 Should override Baservice to stop services properly???
public class BaseRmService extends Service implements IMeshCallBack {

    private List<IRmCommunicator> mIRmCommunicators;
    private MeshProvider mMeshProvider;

    @Override
    public void onCreate() {
        super.onCreate();
        mIRmCommunicators = new ArrayList<>();

        initMesh();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null) {
            String action = intent.getAction();

            if(!TextUtils.isEmpty(action)) {

                switch (action) {
                    case BaseRmServiceNotificationHelper.ACTION_STOP_SERVICE:

                        shutTheService();

                        break;
                }

            }
        }

        return START_STICKY;
    }

    private void initMesh() {
        MeshConfig meshConfig = new MeshConfig();
        meshConfig.mSsid = "Azim";
        meshConfig.mPort = getResources().getInteger(R.integer.port_number);
        Timber.d("Local Remote Service initiating with port number:%d", meshConfig.mPort);

        mMeshProvider = MeshProvider.getInstance();
        mMeshProvider.setMeshConfig(meshConfig);
        mMeshProvider.setIMeshCallBack(this);
        mMeshProvider.start(getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIRmServiceConnection;
    }

    private final IRmServiceConnection.Stub mIRmServiceConnection = new IRmServiceConnection.Stub() {

        @Override
        public void setProfile(byte[] profileInfo) throws RemoteException {
            mMeshProvider.setProfileInfo(profileInfo);
        }

        @Override
        public void setRmCommunicator(IRmCommunicator iRmCommunicator) throws RemoteException {

            mIRmCommunicators.add(iRmCommunicator);

        }

        @Override
        public void setServiceForeground(boolean isForeGround) throws RemoteException {

            if(isForeGround) {
                new BaseRmServiceNotificationHelper(BaseRmService.this).startForegroundService();
            } else {
                new BaseRmServiceNotificationHelper(BaseRmService.this).stopForegroundService();
            }
        }

        @Override
        public boolean resetCommunicator(IRmCommunicator iRmCommunicator) throws RemoteException {
            return mIRmCommunicators.remove(iRmCommunicator);
        }
    };

    @Override
    public void onMesh(MeshData meshData) {

        for(IRmCommunicator iRmCommunicator : mIRmCommunicators) {

            if(iRmCommunicator != null) {
                try {
                    iRmCommunicator.onMeshData(meshData);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    @Override
    public void onMesh(MeshAcknowledgement meshAcknowledgement) {

        for(IRmCommunicator iRmCommunicator : mIRmCommunicators) {

            if(iRmCommunicator != null) {
                try {
                    iRmCommunicator.onMeshAcknowledgement(meshAcknowledgement);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    @Override
    public void onProfileInfo(BaseMeshData baseMeshData) {

        for(IRmCommunicator iRmCommunicator : mIRmCommunicators) {

            if(iRmCommunicator != null) {
                try {
                    iRmCommunicator.onProfileInfo(baseMeshData);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public void onPeerRemoved(MeshPeer meshPeer) {

        for(IRmCommunicator iRmCommunicator : mIRmCommunicators) {

            if(iRmCommunicator != null) {
                try {
                    iRmCommunicator.onPeerRemoved(meshPeer);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onInitSuccess(MeshPeer selfMeshPeer) {

        for(IRmCommunicator iRmCommunicator : mIRmCommunicators) {

            if(iRmCommunicator != null) {
                try {
                    iRmCommunicator.onLibraryInitSuccess();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onInitFailed(int reason) {
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        shutTheService();
    }

    private void shutTheService() {

        //Removing notification if present
        new BaseRmServiceNotificationHelper(this).stopForegroundService();

        //Stopping mesh
        if(mMeshProvider != null) {
            mMeshProvider.stop();
            mMeshProvider = null;
        }

        //Stopping service
        super.stopSelf();

        //Sending call back to app layer so that corresponding process can be cleared
        for(IRmCommunicator iRmCommunicator : mIRmCommunicators) {

            if(iRmCommunicator != null) {
                try {
                    iRmCommunicator.onServiceDestroy();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
