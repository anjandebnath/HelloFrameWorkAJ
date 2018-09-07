package core.left.io.framework.application.data.helper.local.base;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;
import android.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Base class to develop any entity. Prepared as databinding compatible. So, at initial version this
 * is not serializable or parcelable
 */
public abstract class BaseEntity extends BaseObservable implements Parcelable {

    //Any new insertion was getting conflict with first Id. Could not find any efficient way to insert
    //with auto generate id (as of raw sqlite) rather autoinc.
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = BaseColumnNames.ID)
    @NonNull
    protected long mId;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
    }

    public BaseEntity() {
    }

    protected BaseEntity(Parcel in) {
        this.mId = in.readLong();
    }

    /**
     * To copy current object to a new Object. Child class should call this method.
     * Useful to copy to new object. Might require as when we transport data through parcelable
     * then all the binders are broadcasted with any change of the source object which might not
     * be always desired
     * @param baseEntity
     * @return
     */
    public BaseEntity copy(BaseEntity baseEntity) {
        if(baseEntity == null) {
            return null;
        }

        baseEntity.mId = mId;
        return baseEntity;
    }
}