package pl.smartplayer.smartplayerapp.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import pl.smartplayer.smartplayerapp.connection.GameClient;

public class PositionsCollector {

    public static int requestCount = 0;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void addResultToJson(PositionsRequest positionsRequest, Context context) {
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            String filePath =  path + "/data.json";
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
                JSONObject mainObject = new JSONObject();

                FileWriter fileWriter = new FileWriter(filePath);

                mainObject.put("teamId",1); //TODO
                mainObject.put("gameId",12); // TODO
                JSONArray playersArray = new JSONArray();
                mainObject.put("players",playersArray);
                fileWriter.write(mainObject.toJSONString());
                fileWriter.flush();
            }

            JSONParser parser = new JSONParser();
            JSONObject mainObject = (JSONObject) parser.parse(new FileReader(filePath));

            JSONObject searched = null;
            JSONArray playersArray = null;

            if(mainObject.get("players") instanceof JSONArray){
                playersArray  = (JSONArray) mainObject.get("players");
            } else if (mainObject.get("players") instanceof JSONObject){
                playersArray = new JSONArray();
                playersArray.add(mainObject.get("players"));
            }


            for (Object obj : playersArray){
                JSONObject jsonObject = ((JSONObject)obj);
                if(String.valueOf(jsonObject.get("id")).equals(String.valueOf(positionsRequest.getNumber()))){
                    searched = jsonObject;
                }
            }

            if(searched != null){
                JSONObject positionsObject = new JSONObject();
                positionsObject.put("timestamp",positionsRequest.getTimestamp());
                positionsObject.put("longitude",positionsRequest.getPoint().x);
                positionsObject.put("latitude",positionsRequest.getPoint().y);

                JSONArray positionsArray = (JSONArray) searched.get("positions");
                positionsArray.add(positionsObject);
                searched.put("positions",positionsArray);
                FileWriter fileWriter = new FileWriter(filePath);
                fileWriter.write(mainObject.toJSONString());
                fileWriter.flush();

            } else {
                JSONObject positionsObject = new JSONObject();
                positionsObject.put("timestamp",positionsRequest.getTimestamp());
                positionsObject.put("longitude",positionsRequest.getPoint().x);
                positionsObject.put("latitude",positionsRequest.getPoint().y);
                JSONArray jsonArray = new JSONArray();
                jsonArray.add(positionsObject);

                JSONObject jsonPlayerObject = new JSONObject();
                jsonPlayerObject.put("id",positionsRequest.getNumber());
                jsonPlayerObject.put("positions",jsonArray);
                playersArray.add(jsonPlayerObject);
                mainObject.put("players", playersArray);


                FileWriter fileWriter = new FileWriter(filePath);

                fileWriter.write(mainObject.toJSONString());
                fileWriter.flush();

            }

            requestCount++;
            if(requestCount == 10){ //TODO : Jakoś to zmienić na bardziej elegancko
                sendResults(context);

            }
        } catch (Exception e){
            e.printStackTrace();
            //TODO
        }
    }

    private static void sendResults(Context context) {

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            String filePath = path + "/data.json";

            JSONParser parser = new JSONParser();
            JSONObject mainObject = (JSONObject) parser.parse(new FileReader(filePath));

            File file = new File(filePath);
            file.delete();

            GameClient client = new GameClient(context);
            client.sendJSONResults(mainObject);

        } catch (Exception e){
            //TODO
        }
    }
}
