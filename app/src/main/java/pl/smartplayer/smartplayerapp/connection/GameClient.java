package pl.smartplayer.smartplayerapp.connection;

import android.content.Context;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameClient extends AbstractClient {

    GameService service = getRetrofitInstance().create(GameService.class);

    private Context context;

    public GameClient(Context context) {
        this.context = context;
    }

    public void sendJSONResults(JSONObject resultsObject) {

        try {
            Call<Void> call = service.sendResultsFile(resultsObject);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    //TuNic nie robimy w sumie
                    if(response.code() == 200)
                        Toast.makeText(context,"Dane zostały wysłane na serwer!", Toast.LENGTH_LONG);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context,"Nie udało się przesłać danych na serwer",Toast.LENGTH_LONG);
                    t.printStackTrace(); //Tu dajemy np. jakieś dane domyślne, ogólnie akcje jak coś się skaszani
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
