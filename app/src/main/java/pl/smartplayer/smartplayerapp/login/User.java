package pl.smartplayer.smartplayerapp.login;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class User {
    @SerializedName("accessToken")
    private String accessToken;
    @SerializedName("refreshToken")
    private String refreshToken;
    @SerializedName("expiration")
    private Date expiration;
    @SerializedName("clubId")
    private int clubId;
    @SerializedName("userName")
    private String userName;

    public int getClubId() {
        return clubId;
    }
}
