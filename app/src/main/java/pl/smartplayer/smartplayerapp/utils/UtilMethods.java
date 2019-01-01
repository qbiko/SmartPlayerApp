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
            final String action = intent.getAction();
            if (MldpBluetoothService.ACTION_BLE_DATA_RECEIVED.equals(action)) {
                String data = intent.getStringExtra(MldpBluetoothService.INTENT_EXTRA_SERVICE_DATA);
                String serviceAdress = intent.getStringExtra(MldpBluetoothService.INTENT_EXTRA_SERVICE_ADDRESS);

                if (data != null) {
                    message = message.concat(data);
                    String messages[] = message.split("\\$");
                    if (messages.length > 1) {
                        String processingMessage = messages[0];
                        String restOfMessage = TextUtils.join("$", Arrays.copyOfRange(messages, 1, messages.length));

                        message = restOfMessage;

                        String processingMessagePart[] = processingMessage.replaceAll("\\s+", "").split(",");

                        try {
                            if (processingMessagePart[0].equals("GNGGA")) {
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
                                        Point2D leftDown = getPoint("leftDown");
                                        Point2D leftUp = getPoint("leftUp");
                                        Point2D rightUp = getPoint("rightUp");

                                        player.setPosition(getPixelPositionByPoints(leftUp,leftDown,playerPosition),
                                                getPixelPositionByPoints(leftUp,rightUp,playerPosition));

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
        double K = (leftOrUpperPoint.y - rightOrDownPoint.y) / (leftOrUpperPoint.x - rightOrDownPoint.x); // pomocniczy współczynnik
        double M = leftOrUpperPoint.y - (K * leftOrUpperPoint.x);
        double x = Math.abs(K*playerPosition.x - playerPosition.y + M) / Math.sqrt(Math.pow(K,2) + 1);
        double d = Math.sqrt(Math.pow((rightOrDownPoint.x - leftOrUpperPoint.x),2) + Math.pow((rightOrDownPoint.y - leftOrUpperPoint.y),2));
        return (int) ((1000 * x) / d);
    }
}