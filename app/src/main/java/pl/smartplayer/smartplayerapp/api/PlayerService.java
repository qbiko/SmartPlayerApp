package pl.smartplayer.smartplayerapp.api;

import java.util.List;

import pl.smartplayer.smartplayerapp.player.Player;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PlayerService {

    @GET("/api/Player/listOfPlayersForClub/{clubId}")
    Call<List<Player>> getPlayersByClubId(@Path("clubId") int clubId);

}
