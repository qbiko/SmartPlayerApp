package pl.smartplayer.smartplayerapp.main;

import android.graphics.Point;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import pl.smartplayer.smartplayerapp.player.Player;
import pl.smartplayer.smartplayerapp.utils.Point2D;

public class PlayerOnGame implements Parcelable {

    private int number;
    private Player player;
    private Point position = new Point(500,1000);
    private Point2D cartographicalPosition = new Point2D(0,0);
    private String moduleMac = "";
    private Double distance = 0.0;

    public PlayerOnGame(int number, Player player) {
        this.number = number;
        this.player = player;
    }


    public PlayerOnGame(int number, Player player, String mac) {
        this(number,player);
        moduleMac = mac;
    }


    private PlayerOnGame(Parcel parcel) {
        this.number = parcel.readInt();
        this.moduleMac = parcel.readString();
        this.player = parcel.readParcelable(Player.class.getClassLoader());
    }

    public int getNumber() {
        return number;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPosition(Point point){
        position = point;

    }

    public Point getPosition(){
        return position;
    }

    public String getModuleMac() {
        return moduleMac;
    }

    public Point2D getCartographicalPosition() {
        return cartographicalPosition;
    }

    public void setCartographicalPosition(Point2D cartographicalPosition) {
        this.cartographicalPosition = cartographicalPosition;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(number);
        parcel.writeString(moduleMac);
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
