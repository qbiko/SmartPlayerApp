package pl.smartplayer.smartplayerapp.field;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Field implements Parcelable {
    private long dbId;
    private String name;
    private List<Location> coordinates;

    Field(long dbId, String name, List<Location> coordinates) {
        this.dbId = dbId;
        this.name = name;
        this.coordinates = coordinates;
    }

    private Field(Parcel parcel) {
        this.dbId = parcel.readLong();
        this.name = parcel.readString();
        this.coordinates = new ArrayList<>();
        parcel.readList(coordinates, null);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(dbId);
        parcel.writeString(name);
        parcel.writeList(coordinates);
    }

    public String getName() {
        return name;
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
