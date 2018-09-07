package core.left.io.framework.util.collections;

import android.support.annotation.NonNull;

import java.util.Collection;

/**
 * ============================================================================
 * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2018-08-13 at 11:15 AM].
 * <br>Email: azim@w3engineers.com
 * <br>----------------------------------------------------------------------------
 * <br>Project: android-framework.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2018-08-13 at 11:15 AM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2018-08-13 at 11:15 AM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
public class CollectionUtil {

    public static synchronized <M, T extends Matchable<M>> int deleteFromCollection(Collection<T> collection,
                                                                                    @NonNull M toMatchWith) {

        return deleteFromCollection(collection, toMatchWith, null);

    }

    public static synchronized <M, T extends Matchable<M>> int deleteFromCollection(Collection<T> collection,
                                                                                    @NonNull M toMatchWith,
                                                                                    Collection<T> returnedCollection) {

        int deleteCount = 0;
        for (T item : collection) {

            if(item != null && toMatchWith.equals(item.getMatcher())) {

                if(collection.remove(item)) {
                    deleteCount++;

                    if(returnedCollection != null) {
                        returnedCollection.add(item);
                    }
                }

            }

        }
        return deleteCount;

    }

    public static synchronized <M, T extends Matchable<M>> void getMatchedCollection(Collection<T> collection,
                                                                                    @NonNull M toMatchWith,
                                                                                    Collection<T> returnedCollection) {

        for (T item : collection) {

            if(item != null && toMatchWith.equals(item.getMatcher())) {

                if(collection.contains(item)) {
                    if(returnedCollection != null) {
                        returnedCollection.add(item);
                    }
                }

            }
        }

    }

    public static <T> boolean hasItem(Collection<T> collection) {
        return collection != null && collection.size() > 0;
    }

    public static <T> boolean hasItem(T[] collection) {
        return collection != null && collection.length > 0;
    }

}
