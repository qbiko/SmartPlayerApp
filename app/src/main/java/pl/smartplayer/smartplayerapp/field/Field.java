package pl.smartplayer.smartplayerapp.field;

import android.os.Parcel;
import android.os.Parcelable;

public class Field implements Parcelable {
    private long dbId;
    private String name;

    public Field(long dbId, String name) {
        this.dbId = dbId;
        this.name = name;
    }

    public Field(Parcel parcel) {
        this.dbId = parcel.readLong();
        this.name = parcel.readString();
    }

    public long getDbId() {
        return dbId;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(dbId);
        parcel.writeString(name);
    }

    public static final Creator<Field> CREATOR = new Creator<Field>() {
        @Override
        public Field createFromParcel(Parcel in) {
            return new Field(in);
        }

        @Override
        public Field[] newArray(int size) {
            return new Field[size];
        }
    };

}
