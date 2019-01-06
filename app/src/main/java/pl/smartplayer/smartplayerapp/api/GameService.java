package pl.smartplayer.smartplayerapp.api;

import org.json.simple.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GameService {

    @POST("/api/Game/createPosition")
    Call<Void> sendResultsFile(@Body JSONObject jsonObject);


    @POST("/api/Game/createGame")
    Call<JSONObject> createNewGame(@Body JSONObject jsonObject);
}
