package pl.smartplayer.smartplayerapp.main;

import android.graphics.Point;
import android.widget.Toast;

import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import pl.smartplayer.smartplayerapp.utils.Point2D;
import pl.smartplayer.smartplayerapp.utils.PositionsProcessor;
import pl.smartplayer.smartplayerapp.utils.PositionsRequest;

import static pl.smartplayer.smartplayerapp.utils.UtilMethods.measureMeters;


public class BTMock implements Runnable {

    private static final Integer waitingForNewCordinatesInterval = 1000;
    private static final Integer maxMovingRange = 50;

    //TO jest tymczasowe, działające tylko dla boiska o określonym położeniu. Ogarnę jak będę robił dla rzeczywistego BT
    private static final double upperLeftCornerLat = 54.370012;
    private static final double upperLeftCornerLon = 18.629355;


    public BTMock() {
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
                for (PlayerOnGame player : MainActivity.mPlayersOnGameList) {
                    int newX = player.getPosition().x + (rand.nextInt(maxMovingRange) - maxMovingRange / 2);
                    newX = (newX < 0 ? 0 : newX);
                    newX = (newX > 1000 ? 1000 : newX);
                    double lon = upperLeftCornerLon + newX * (diffLon / 1000);
                    int newY = player.getPosition().y + (rand.nextInt(maxMovingRange) - maxMovingRange / 2);
                    newY = (newY < 0 ? 0 : newY);
                    newY = (newY > 1000 ? 1000 : newY);
                    double lat = upperLeftCornerLat + newY * (diffLat / 1000);

                    Point2D playerPosition = new Point2D(lat,lon);

                    if(player.getCartographicalPosition() == null){
                        player.setCartographicalPosition(playerPosition);
                    } else {
                        Point2D point2D = player.getCartographicalPosition();
                        player.setDistance(player.getDistance() + measureMeters(point2D.x,point2D.y,playerPosition.x,playerPosition.y)/1000.0);
                        player.setCartographicalPosition(playerPosition);
                    }
                    player.setPosition(new Point(newX, newY));


                }
            }
        }
    }
}
