package pl.smartplayer.smartplayerapp.main;

import android.os.AsyncTask;

public class RepaintTask extends AsyncTask {
    MainActivity mainActivity;
    private static final int REPAINT_INTERVAL = 1000;

    public RepaintTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        int i = 0;
        while (i == 0) {      //Gdyż IDE się pruje o while(true) w tym miejscu
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.repaintImageView();
                }
            });
            try {
                Thread.sleep(REPAINT_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
