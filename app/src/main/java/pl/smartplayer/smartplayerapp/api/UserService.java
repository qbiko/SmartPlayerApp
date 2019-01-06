package pl.smartplayer.smartplayerapp.api;

import org.json.simple.JSONObject;

import pl.smartplayer.smartplayerapp.login.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {

    @POST("/api/User/token")
    Call<User> getToken(@Body JSONObject jsonObject);

}
