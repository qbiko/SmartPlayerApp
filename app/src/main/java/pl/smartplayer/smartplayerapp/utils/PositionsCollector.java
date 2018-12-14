package pl.smartplayer.smartplayerapp.utils;

import android.annotation.TargetApi;
import android.os.Build;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.SynchronousQueue;

public class PositionsCollector implements Runnable {

     private static SynchronousQueue<PositionsRequest> requestsQueue = new SynchronousQueue();

    @Override
    public void run() {
        while (true) {
            if(!requestsQueue.isEmpty()){
                addResultToJson(requestsQueue.poll());
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void addResultToJson(PositionsRequest positionsRequest) {
        try {
            String filePath = "data.json";
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
                JSONArray mainArray = new JSONArray();

                FileWriter fileWriter = new FileWriter(filePath);

                fileWriter.write(mainArray.toJSONString());
                fileWriter.flush();
            }

            JSONParser parser = new JSONParser();
            JSONArray mainArray = (JSONArray) parser.parse(new FileReader(filePath));

            JSONObject searched = null;

            for (Object obj : mainArray){
                JSONObject jsonObject = ((JSONObject)obj);
                if(jsonObject.get("id").equals(String.valueOf(positionsRequest.getNumber()))){
                    searched = jsonObject;
                }
            }

            if(searched != null){
                JSONObject positionsObject = new JSONObject();
                positionsObject.put("timestamp",positionsRequest.getTimestamp());
                positionsObject.put("longitude",positionsRequest.getPoint().x);
                positionsObject.put("latitude",positionsRequest.getPoint().y);

                JSONArray jsonArray = (JSONArray) searched.get("positions");
                jsonArray.add(positionsObject);
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
                mainArray.add(jsonPlayerObject);


                FileWriter fileWriter = new FileWriter(filePath);

                fileWriter.write(mainArray.toJSONString());
                fileWriter.flush();

            }


            FileWriter fileWriter = new FileWriter(filePath);

            fileWriter.write(mainArray.toJSONString());
            fileWriter.flush();

        } catch (Exception e){
            //TODO
        }
    }

    public static void addToQueue(PositionsRequest request){
       requestsQueue.add(request);
    }
}
