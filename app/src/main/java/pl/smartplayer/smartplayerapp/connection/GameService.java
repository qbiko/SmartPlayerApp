package pl.smartplayer.smartplayerapp.connection;

import org.json.simple.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GameService {

    @POST("/api/Game/createPosition")
    Call<Void> sendResultsFile(@Body JSONObject jsonObject) throws IOException;
}
