package pl.smartplayer.smartplayerapp.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pl.smartplayer.smartplayerapp.R;
import pl.smartplayer.smartplayerapp.main.MainActivity;
import pl.smartplayer.smartplayerapp.main.MldpBluetoothService;
import pl.smartplayer.smartplayerapp.main.PlayerOnGame;
import retrofit2.Call;
import retrofit2.Callback;

public class UtilMethods {

    public static <T> Dialog createInvalidConnectWithApiDialog(Context context, final Call<T>
            call, final Callback<T> callback, final ProgressDialog progressDialog) {

        progressDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder
                (context);

        builder.setTitle(R.string.invalid_connect_with_api_information);
        builder.setPositiveButton(R.string.yes, new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressDialog.show();
                call.clone().enqueue(callback);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing, close dialog
            }
        });
        return builder.create();
    }

    public static <T> void updateUIList(List<T> sourceList, List<T> targetList, BaseAdapter
            adapter) {
        targetList.clear();
        targetList.addAll(sourceList);
        adapter.notifyDataSetChanged();
    }

    // ----------------------------------------------------------------------------------------------------------------
    // BroadcastReceiver handles various events fired by the MldpBluetoothService service.
    public static final BroadcastReceiver bleServiceReceiver = new BroadcastReceiver() {
        private String message = "";

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!MainActivity.isGameActive()){
                return;
            }

            final String action = intent.getAction();
            if (MldpBluetoothService.ACTION_BLE_DATA_RECEIVED.equals(action)) {
                String data = intent.getStringExtra(MldpBluetoothService.INTENT_EXTRA_SERVICE_DATA);
                String serviceAdress = intent.getStringExtra(MldpBluetoothService.INTENT_EXTRA_SERVICE_ADDRESS);

                if (data != null) {
                    message = message + data;
                    String messages[] = message.split("\\$");
                    if (messages.length > 1) {
                        String processingMessage = messages[0];
                        String restOfMessage = TextUtils.join("$", Arrays.copyOfRange(messages, 1, messages.length));

                        message = restOfMessage;

                        String processingMessagePart[] = processingMessage.replaceAll("\\s+", "").split(",");

                        try {
                            if (processingMessagePart[0].equals("GNGGA")) {
                                Log.e("Error!", "Processing message: " + processingMessage);
                                SimpleDateFormat sdf = new SimpleDateFormat("HHmmss.SS");
                                Date dateToSend = sdf.parse(processingMessagePart[1]);

                                Double nmeaLat = Double.parseDouble(processingMessagePart[2]);
                                Double nmeaLon = Double.parseDouble(processingMessagePart[4]);
                                Double correctLat = nmeaLat%100 / 60 + Math.floor(nmeaLat/100);
                                Double correctLon = nmeaLon%100 / 60 + Math.floor(nmeaLon/100);
                                if (processingMessagePart[3].equals("S")) {
                                    correctLat = -correctLat;
                                }

                                if (processingMessagePart[5].equals("W")) {
                                    correctLon = -correctLon;
                                }

                                for(PlayerOnGame player : MainActivity.mPlayersOnGameList){
                                    if(player.getModuleMac().equals(serviceAdress)){

                                        Point2D playerPosition = new Point2D(correctLon, correctLat);
                                        player.setPosition(getPixelPosition(playerPosition));

                                        if(player.getCartographicalPosition() == null){
                                            player.setCartographicalPosition(playerPosition);
                                        } else {
                                            Point2D point2D = player.getCartographicalPosition();
                                            player.setDistance(player.getDistance() + measureMeters(point2D.x,point2D.y,playerPosition.x,playerPosition.y)/1000.0);
                                            player.setCartographicalPosition(playerPosition);
                                        }

                                        PositionsProcessor.addResultToJson(new PositionsRequest(playerPosition, player.getPlayer().getDbId(), dateToSend));
                                    }
                                }
                            }
                        } catch (Exception e) {

                            Log.e("Error!", "Unable to process message: " + processingMessage);
                        }
                    }
                }
            }
        }
    };

    private static Point2D getPoint(String pointName) {
        double lat = MainActivity.sSelectedField.getCoordinates().get(pointName).get("lat");
        double lon = MainActivity.sSelectedField.getCoordinates().get(pointName).get("lon");
        return new Point2D(lat,lon);
    }


    public static int getPixelPositionByPoints(Point2D leftOrUpperPoint, Point2D rightOrDownPoint, Point2D playerPosition){

        Point2D leftDown = getPoint("leftDown");
        Point2D leftUp = getPoint("leftUp");
        Point2D rightUp = getPoint("rightUp");

        double K = (leftOrUpperPoint.y - rightOrDownPoint.y) / (leftOrUpperPoint.x - rightOrDownPoint.x); // pomocniczy współczynnik
        double M = leftOrUpperPoint.y - (K * leftOrUpperPoint.x);
        double x = (K*playerPosition.x - playerPosition.y + M) / Math.sqrt(Math.pow(K,2) + 1);

        double d = Math.sqrt(Math.pow((rightOrDownPoint.x - leftOrUpperPoint.x),2) + Math.pow((rightOrDownPoint.y - leftOrUpperPoint.y),2));
        int position =  (int) ((1000 * x) / d);
        if(position < 0) {
            position = -5;
        } else if (position > 1000){
            position = 1005;
        }
        return position;
    }

    public static Point getPixelPosition(Point2D playerPosition){

        Point2D leftDown = getPoint("leftDown");
        Point2D leftUp = getPoint("leftUp");
        Point2D rightUp = getPoint("rightUp");
        Point2D rightDown = getPoint("rightDown");

        double K = (leftUp.y - leftDown.y) / (leftUp.x - leftDown.x); // pomocniczy współczynnik
        double M = leftUp.y - (K * leftUp.x);
        double x = Math.abs(K*playerPosition.x - playerPosition.y + M) / Math.sqrt(Math.pow(K,2) + 1);

        double d = Math.sqrt(Math.pow((leftDown.x - leftUp.x),2) + Math.pow((leftDown.y - leftUp.y),2));
        int positionX =  (int) ((1000 * x) / d);


        //KONTROLNE CZY NA PEWNO JEST POMIĘDZY OBIEMA LINIAMI
        double Kk = (rightUp.y - rightDown.y) / (rightUp.x - rightDown.x);
        double Mk = rightUp.y - (Kk * rightUp.x);
        double xk = Math.abs(Kk*playerPosition.x - playerPosition.y + Mk) / Math.sqrt(Math.pow(Kk,2) + 1);

        double dk = Math.sqrt(Math.pow((rightDown.x - rightUp.x),2) + Math.pow((rightDown.y - rightUp.y),2));
        int positionXk =  (int) ((1000 * xk) / dk);

        if(positionX < 1000 && positionXk<1000){
        } else if (positionX > positionXk){
            positionX = 1010;
        } else if (positionX < positionXk){
            positionX = -10;
        }


        double Kd = (leftUp.y - rightUp.y) / (leftUp.x - rightUp.x); // pomocniczy współczynnik
        double Md = leftUp.y - (Kd * leftUp.x);
        double xd = Math.abs(Kd*playerPosition.x - playerPosition.y + Md) / Math.sqrt(Math.pow(Kd,2) + 1);

        double dd = Math.sqrt(Math.pow((rightUp.x - leftUp.x),2) + Math.pow((rightUp.y - leftUp.y),2));
        int positionY =  (int) ((1000 * xd) / dd);


        //KONTROLNE CZY NA PEWNO JEST POMIĘDZY OBIEMA LINIAMI
        double Kkd = (leftDown.y - rightDown.y) / (leftDown.x - rightDown.x);
        double Mkd = leftDown.y - (Kkd * leftDown.x);
        double xkd = Math.abs(Kkd*playerPosition.x - playerPosition.y + Mkd) / Math.sqrt(Math.pow(Kkd,2) + 1);

        double dkd = Math.sqrt(Math.pow((rightDown.x - leftDown.x),2) + Math.pow((rightDown.y - leftDown.y),2));
        int positionYk =  (int) ((1000 * xkd) / dkd);

        if(positionY < 1000 && positionYk<1000){

        } else if (positionY > positionYk){
            positionY = 1010;
        } else if (positionY < positionYk){
            positionY = -10;
        }

        return new Point(positionX,positionY);
    }

    private static double measureMeters(double lat1,double lon1,double lat2,double lon2){  // generally used geo measurement function
        double R = 6378.137; // Radius of earth in KM
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        return d * 1000; // meters
    }
}