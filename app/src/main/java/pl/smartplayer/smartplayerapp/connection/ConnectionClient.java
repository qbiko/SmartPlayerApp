package pl.smartplayer.smartplayerapp.connection;

import com.google.gson.JsonArray;

import org.json.JSONObject;

import java.io.IOException;

import pl.smartplayer.smartplayerapp.main.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnectionClient extends AbstractClient {

    private final MainActivity mainActivity;
    ConnectionService service = getRetrofitInstance().create(ConnectionService.class); //Tworzymy sobie instancje serwisu z całym połączeniem, na tym wywołujemy już konkretne endpointy z danego serwisu i dajemy enqueue

    public ConnectionClient(MainActivity activity) { //Przekazujemy sobie aby mieć jakby co dostęp do kontrolek, bind nie będzie działał w tej klasie bo bind przyjmuje activity w parametrze
        mainActivity = activity;
    }

    public void test() {

        try {
            Call<JsonArray> call = service.test(); //Na Callu możemy wywołać właśnie enqueue. W testach mamy po prostu execute, ale tam nie krzyczy nam że nie można w main thread :)
            call.enqueue(new Callback<JsonArray>() { //Zrobione to jest tak gdyż nie można wywoływać zapytań bezpośrednio z głównego wątku
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    //Tu wykonujesz akcję w chwili gdy dostałeś odpowiedź
                    //mainActivity.textView.setText(response.body().toString());

                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    t.printStackTrace(); //Tu dajemy np. jakieś dane domyślne, ogólnie akcje jak coś się skaszani
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
