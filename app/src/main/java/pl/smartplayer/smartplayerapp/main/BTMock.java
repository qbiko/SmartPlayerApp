package pl.smartplayer.smartplayerapp.main;

import android.content.Context;
import android.graphics.Point;

import java.util.Date;
import java.util.Map;
import java.util.Random;

import pl.smartplayer.smartplayerapp.utils.Point2D;
import pl.smartplayer.smartplayerapp.utils.PositionsCollector;
import pl.smartplayer.smartplayerapp.utils.PositionsRequest;


public class BTMock implements Runnable {

    private static final Integer waitingForNewCordinatesInterval = 1000;
    private static final Integer maxMovingRange = 20;

    private Context context;

    //TO jest tymczasowe, działające tylko dla boiska o określonym położeniu. Ogarnę jak będę robił dla rzeczywistego BT
    private static final double upperLeftCornerLat = 54.370012;
    private static final double upperLeftCornerLon = 18.629355;
    private static final double lowerRightCornerLat = 54.369448;
    private static final double lowerRightCornerLon = 18.630966;


    public BTMock(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        double diffLat = 0.001611;
        double diffLon = 0.000564;
        while (true) {
            try {
                Thread.sleep(waitingForNewCordinatesInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (MainActivity.isGameActive()) {
                Random rand = new Random();
                for (PlayerOnGame player : MainActivity.sDummyList) {
                    int newX = player.getPosition().x + (rand.nextInt(maxMovingRange) - maxMovingRange / 2);
                    newX = (newX < 0 ? 0 : newX);
                    newX = (newX > 1000 ? 1000 : newX);
                    double lon = upperLeftCornerLon + newX * (diffLon / 1000);
                    int newY = player.getPosition().y + (rand.nextInt(maxMovingRange) - maxMovingRange / 2);
                    newY = (newY < 0 ? 0 : newY);
                    newY = (newY > 1000 ? 1000 : newY);
                    double lat = upperLeftCornerLat + newY * (diffLat / 1000);
                    player.setPosition(newX,newY);
                    PositionsCollector.addResultToJson(new PositionsRequest(new Point2D(lon, lat),player.getNumber(),new Date()),context);
                }
            }
        }
    }
}
