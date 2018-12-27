package pl.smartplayer.smartplayerapp.api;

import java.util.List;

import pl.smartplayer.smartplayerapp.field.Field;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FieldService {

    @GET("/api/Field/listOfFields/{clubId}")
    Call<List<Field>> getFieldsByClubId(@Path("clubId") int clubId);

    @POST("/api/Field/create")
    Call<Field> createField(@Body Field field);

}
