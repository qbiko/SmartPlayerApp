package pl.smartplayer.smartplayerapp.player;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalDate;
import org.joda.time.Years;

import java.util.Date;

public class Player implements Parcelable {

    @SerializedName("id")
    private long dbId;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("dateOfBirth")
    private Date dateOfBirth;
    @SerializedName("growth")
    private int height;
    @SerializedName("weight")
    private int weight;

    public Player(long dbId, String firstName, String lastName, Date dateOfBirth, int height, int weight) {
        this.dbId = dbId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.height = height;
        this.weight = weight;
    }

    Player(Parcel parcel) {
        this.dbId = parcel.readLong();
        this.firstName = parcel.readString();
        this.lastName = parcel.readString();
        this.dateOfBirth = (java.util.Date)parcel.readSerializable();
        this.height = parcel.readInt();
        this.weight = parcel.readInt();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return Years.yearsBetween(new LocalDate(dateOfBirth), new LocalDate()).getYears();
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(dbId);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeSerializable(dateOfBirth);
        parcel.writeInt(height);
        parcel.writeInt(weight);
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };
}
