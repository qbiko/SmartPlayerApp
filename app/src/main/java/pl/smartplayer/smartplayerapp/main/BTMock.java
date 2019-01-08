package pl.smartplayer.smartplayerapp.main;

import android.widget.Toast;

import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import pl.smartplayer.smartplayerapp.utils.Point2D;
import pl.smartplayer.smartplayerapp.utils.PositionsProcessor;
import pl.smartplayer.smartplayerapp.utils.PositionsRequest;


public class BTMock implements Runnable {

    private static final Integer waitingForNewCordinatesInterval = 1000;
    private static final Integer maxMovingRange = 20;

    //TO jest tymczasowe, działające tylko dla boiska o określonym położeniu. Ogarnę jak będę robił dla rzeczywistego BT
    private static final double upperLeftCornerLat = 54.370012;
    private static final double upperLeftCornerLon = 18.629355;


    private MainActivity mainActivity;


    public BTMock(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
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
                    player.setPosition(newX, newY);
                    try {
                        PositionsProcessor.addResultToJson(new PositionsRequest(new Point2D(lon, lat), player.getPlayer().getDbId(), new Date()));
                    } catch (ParseException | IOException e) {
                        try {
                            PositionsProcessor.sendResults(); //Jeżeli coś się nie powiedzie to spróbuj wysłać zebrane wyniki
                        } catch (IOException | ParseException e1) {
                            //Jak nic nie da się zrobić to wywal plik
                            File file = new File(PositionsProcessor.getFilePath());
                            file.delete();
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mainActivity, "Nie udało się przesłać danych na serwer!", Toast.LENGTH_SHORT);
                                }
                            });
                        }
                    }
                }
            }
        }
    }
}
