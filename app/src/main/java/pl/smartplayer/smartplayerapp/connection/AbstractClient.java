package pl.smartplayer.smartplayerapp.connection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class AbstractClient { //Abstract żeby jakby co można było sobie dodać kolejne klienty np. określony klient dla jakiegoś obszaru. Jak wszystko zrobimy w jednym też będzie ok :)
    //static final String BASE_URL = "https://jsonplaceholder.typicode.com"; //Tutaj mamy link do serwisu z którego korzystałem podczas testu
    static final String BASE_URL = "https://smartplayer-backend.localtunnel.me/"; //Tutaj nasz, ten który seba podał

    public Retrofit getRetrofitInstance() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}
