package framework;

import core.left.io.framework.App;
import timber.log.Timber;

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
 * All release related tasks will be done here and common task in {@link App}
 * We placed this class so that if any thing specific only for release is required and to adjust with
 * application class structure of gradle Android plugin
 */
public abstract class BaseApplication extends App {

    @Override
    protected void plantTimber() {

        Timber.plant(new Timber.Tree() {

            @Override
            protected void log(int priority, String tag, String message, Throwable t) { }

            @Override
            protected boolean isLoggable(String tag, int priority) {
                //Do not like to log during release
                return false;
            }
        });

    }
}