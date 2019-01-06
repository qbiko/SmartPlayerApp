package pl.smartplayer.smartplayerapp.main;

import com.google.gson.annotations.SerializedName;

public class Team {
    @SerializedName("id")
    private long dbId;
    @SerializedName("name")
    private String teamName;
    @SerializedName("clubId")
    private long clubId;

    public String getTeamName() {
        return teamName;
    }

}
