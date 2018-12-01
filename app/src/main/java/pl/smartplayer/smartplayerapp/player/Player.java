package pl.smartplayer.smartplayerapp.player;

import android.os.Parcel;
import android.os.Parcelable;

public class Player implements Parcelable {
    private long dbId;
    private String firstName;
    private String lastName;
    private int age;
    private int height;
    private int weight;

    public Player(long dbId, String firstName, String lastName, int age, int height, int weight) {
        this.dbId = dbId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    Player(Parcel parcel) {
        this.dbId = parcel.readLong();
        this.firstName = parcel.readString();
        this.lastName = parcel.readString();
        this.age = parcel.readInt();
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
        return age;
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
        parcel.writeInt(age);
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
