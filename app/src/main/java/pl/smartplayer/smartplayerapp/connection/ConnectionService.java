package pl.smartplayer.smartplayerapp.connection;


import com.google.gson.JsonArray;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ConnectionService{

    //Tu dajemy endpointy, GET i POST określa nam path, jako parametry można podawać jakieś PathParam np:
    // @GET("/image/{id}")
    // Call<ResponseBody> example(@Path("id") int id);

    @GET("/posts")
    Call<JsonArray> test() throws IOException;
}
