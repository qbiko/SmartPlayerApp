package pl.smartplayer.smartplayerapp.field;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Field implements Parcelable {

    @SerializedName("id")
    private long dbId;
    @SerializedName("name")
    private String name;
    @SerializedName("address")
    private String address;
    @SerializedName("private")
    private boolean isPrivate;
    @SerializedName("fieldCoordinates")
    private Map<String,Map<String, Double>> coordinates;
    @SerializedName("clubId")
    private long clubId;

    public Field(String name, String address, boolean isPrivate, Map<String,Map<String, Double>>
            coordinates, long clubId) {
        this.name = name;
        this.address = address;
        this.isPrivate = isPrivate;
        this.coordinates = coordinates;
        this.clubId = clubId;
    }

    private Field(Parcel parcel) {
        this.dbId = parcel.readLong();
        this.name = parcel.readString();
        this.address = parcel.readString();
        this.isPrivate = parcel.readByte() != 0;
        this.coordinates = new HashMap<>();
        parcel.readMap(coordinates, null);
        this.clubId = parcel.readLong();
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(dbId);
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeByte((byte) (isPrivate ? 1 : 0));
        parcel.writeMap(coordinates);
        parcel.writeLong(clubId);
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

    public Map<String, Map<String, Double>> getCoordinates() {
        return coordinates;
    }

    public long getDbId() {
        return dbId;
    }
}
