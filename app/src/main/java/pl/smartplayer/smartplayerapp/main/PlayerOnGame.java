package pl.smartplayer.smartplayerapp.main;

import android.os.Parcel;
import android.os.Parcelable;

import pl.smartplayer.smartplayerapp.player.Player;

public class PlayerOnGame implements Parcelable {

    private int number;
    private Player player;

    public PlayerOnGame(int number, Player player) {
        this.number = number;
        this.player = player;
    }

    private PlayerOnGame(Parcel parcel) {
        this.number = parcel.readInt();
        this.player = parcel.readParcelable(Player.class.getClassLoader());
    }

    public int getNumber() {
        return number;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(number);
        parcel.writeParcelable(player, i);
    }

    public static final Creator<PlayerOnGame> CREATOR = new Creator<PlayerOnGame>() {
        @Override
        public PlayerOnGame createFromParcel(Parcel in) {
            return new PlayerOnGame(in);
        }

        @Override
        public PlayerOnGame[] newArray(int size) {
            return new PlayerOnGame[size];
        }
    };
}