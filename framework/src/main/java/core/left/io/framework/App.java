package core.left.io.framework;

import android.content.Context;
import android.support.multidex.MultiDexApplication;



import core.left.io.framework.util.helper.Toaster;



/*
* ****************************************************************************
* * Copyright © 2018 W3 Engineers Ltd., All rights reserved.
* *
* * Created by:
* * Name : Anjan Debnath
* * Date : 10/25/17
* * Email : anjan@w3engineers.com
* *
* * Purpose: Base Application class
* *
* * Last Edited by : SUDIPTA KUMAR PAIK on 03/08/18.
* * History:
* * 1:
* * 2:
* *
* * Last Reviewed by : SUDIPTA KUMAR PAIK on 03/08/18.
* ****************************************************************************
*/

/**
 * All application layer common tasks
 */
public abstract class App extends MultiDexApplication {
    private static App sContext;

    public static Context getContext() {

        if(sContext != null) {
            return sContext;
        }
        return null;
    }

    protected abstract void plantTimber();

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;

        //init timber
        plantTimber();

        //init crashlytics
//        todo check has string or not
        //Fabric.with(this, new Crashlytics());

        Toaster.init(getResources().getColor(R.color.accent));
    }
}