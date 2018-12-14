package pl.smartplayer.smartplayerapp.main;

import android.graphics.Point;

import java.util.Map;
import java.util.Random;


public class BTMock implements Runnable {

    private static final Integer waitingForNewCordinatesInterval = 1000;
    private static final Integer maxMovingRange = 20;

    @Override
    public void run() {
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
                    int newY = player.getPosition().y + (rand.nextInt(maxMovingRange) - maxMovingRange / 2);
                    newY = (newY < 0 ? 0 : newY);
                    newY = (newY > 1000 ? 1000 : newY);
                    player.setPosition(newX,newY);
                }
            }
        }
    }
}
