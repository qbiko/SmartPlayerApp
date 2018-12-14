package pl.smartplayer.smartplayerapp.utils;

import android.graphics.Point;

class PositionsRequest {
    private Point point;
    private int number;
    private long timestamp;

    public PositionsRequest(Point point, int number, long timestamp) {
        this.point = point;
        this.number = number;
        this.timestamp = timestamp;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
