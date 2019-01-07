package pl.smartplayer.smartplayerapp.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import pl.smartplayer.smartplayerapp.api.ApiClient;
import pl.smartplayer.smartplayerapp.api.GameService;
import pl.smartplayer.smartplayerapp.main.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PositionsProcessor {

    public static int requestCount = 0;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void addResultToJson(PositionsRequest positionsRequest) throws IOException, ParseException {
        File file = new File(getFilePath());
        if (!file.exists()) {
            file.createNewFile();
            JSONObject mainObject = createJsonModel();
            writeResultsToFile(mainObject);
        }

        JSONParser parser = new JSONParser();
        JSONObject mainObject = (JSONObject) parser.parse(new FileReader(getFilePath()));

        JSONObject searched = null;
        JSONArray playersArray = null;

        if (mainObject.get("players") instanceof JSONArray) {
            playersArray = (JSONArray) mainObject.get("players");
        } else if (mainObject.get("players") instanceof JSONObject) {
            playersArray = new JSONArray();
            playersArray.add(mainObject.get("players"));
        }


        for (Object obj : playersArray) {
            org.json.simple.JSONObject jsonObject = ((org.json.simple.JSONObject) obj);
            if (String.valueOf(jsonObject.get("id")).equals(String.valueOf(positionsRequest.getNumber()))) {
                searched = jsonObject;
            }
        }

        if (searched != null) {
            JSONObject positionsObject = createPositionModelNode(positionsRequest);

            JSONArray positionsArray = (JSONArray) searched.get("positions");
            positionsArray.add(positionsObject);
            searched.put("positions", positionsArray);
            writeResultsToFile(mainObject);

        } else {
            JSONObject positionsObject = createPositionModelNode(positionsRequest);
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(positionsObject);

            JSONObject jsonPlayerObject = new JSONObject();
            jsonPlayerObject.put("id", positionsRequest.getNumber());
            jsonPlayerObject.put("positions", jsonArray);
            playersArray.add(jsonPlayerObject);
            mainObject.put("players", playersArray);

            writeResultsToFile(mainObject);

        }

        requestCount++;
        Log.d("Iteration:",String.valueOf(requestCount));
        if (requestCount == 10) {
            requestCount = 0;
            try {
                sendResults();

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private static void writeResultsToFile(JSONObject mainObject) throws IOException {
        FileWriter fileWriter = new FileWriter(getFilePath());
        fileWriter.write(mainObject.toJSONString());
        fileWriter.flush();
    }

    @NonNull
    private static JSONObject createPositionModelNode(PositionsRequest positionsRequest) {
        JSONObject positionsObject = new JSONObject();
        positionsObject.put("date", positionsRequest.getDateString());
        positionsObject.put("longitude", positionsRequest.getPoint().y);
        positionsObject.put("latitude", positionsRequest.getPoint().x);
        return positionsObject;
    }

    @NonNull
    private static JSONObject createJsonModel() {
        JSONObject mainObject = new JSONObject();

        mainObject.put("teamId", MainActivity.sClubId);
        mainObject.put("gameId", MainActivity.sGameId);
        JSONArray playersArray = new JSONArray();
        mainObject.put("players", playersArray);
        return mainObject;
    }

    public static void sendResults() throws IOException, ParseException {

        String filePath = getFilePath();

        JSONParser parser = new JSONParser();
        JSONObject mainObject = (JSONObject) parser.parse(new FileReader(filePath));

        GameService gameService = ApiClient.getClient().create(GameService.class);
        Call<Void> call = gameService.sendResultsFile(mainObject);
        call.enqueue(callback);

    }

    @NonNull
    public static String getFilePath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartPlayer";
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        }
        return path + "/data.json";
    }

    private static Callback<Void> callback = new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if(response.isSuccessful()){
                Log.i("Sending data complete","Deleting File");
                File file = new File(getFilePath());
                file.delete();
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            //Nic nie robimy, przy kolejnej próbie wysłania po prostu pójdzie większa paczka
        }
    };
}
