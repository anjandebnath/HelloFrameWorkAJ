package com.anjan.helloframeworkaj.ui.users;

/*
 *  ****************************************************************************
 *  * Created by : Md. Azizul Islam on 7/6/2018 at 2:13 PM.
 *  * Email : azizul@w3engineers.com
 *  *
 *  * Purpose:
 *  *
 *  * Last edited by : Md. Azizul Islam on 7/6/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */

import android.databinding.ViewDataBinding;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.anjan.helloframeworkaj.R;
import com.anjan.helloframeworkaj.data.remote.model.UserEntity;
import com.anjan.helloframeworkaj.databinding.ItemUserInfoBinding;

import core.left.io.framework.application.ui.base.BaseAdapter;
import core.left.io.framework.application.ui.base.BaseViewHolder;


public class UsersAdapter extends BaseAdapter<UserEntity> {

    private final int USER_TYPE = 1;

    @Override
    public boolean isEqual(UserEntity left, UserEntity right) {
        return TextUtils.equals(left.getUserId(), right.getUserId());
    }

    @Override
    public int getItemViewType(int position) {

        return USER_TYPE;
    }

    @Override
    public BaseViewHolder newViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case USER_TYPE:
                return new UserHolderRemote(inflate(parent, R.layout.item_user_info));
        }

        return null;
    }

    private class UserHolderRemote extends BaseViewHolder<UserEntity> {

        private ItemUserInfoBinding mItemUserInfoBinding;

        UserHolderRemote(ViewDataBinding viewDataBinding) {
            super(viewDataBinding);
            mItemUserInfoBinding = (ItemUserInfoBinding) viewDataBinding;
        }

        @Override
        public void bind(UserEntity item) {
            mItemUserInfoBinding.setUserEntity(item);
            mItemUserInfoBinding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {

            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getItem(getAdapterPosition()));
            }

        }
    }
}
