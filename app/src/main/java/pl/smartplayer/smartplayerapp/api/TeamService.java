package pl.smartplayer.smartplayerapp.api;

import java.util.List;

import pl.smartplayer.smartplayerapp.main.Team;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TeamService {

    @GET("/api/Team/listOTeams/{clubId}")
    Call<List<Team>> getTeamsByClubId(@Path("clubId") int clubId);

}
