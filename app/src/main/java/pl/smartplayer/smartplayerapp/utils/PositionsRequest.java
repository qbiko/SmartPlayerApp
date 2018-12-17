package pl.smartplayer.smartplayerapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PositionsRequest {
    private Point2D point;
    private int number;
    private Date timestamp;

    public PositionsRequest(Point2D point, int number, Date timestamp) {
        this.point = point;
        this.number = number;
        this.timestamp = timestamp;
    }

    public Point2D getPoint() {
        return point;
    }

    public void setPoint(Point2D point) {
        this.point = point;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(timestamp);
    }
}
