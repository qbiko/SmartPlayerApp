package pl.smartplayer.smartplayerapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PositionsRequest {
    private Point2D point;
    private int number;
    private Date date;

    public PositionsRequest(Point2D point, int number, Date date) {
        this.point = point;
        this.number = number;
        this.date = date;
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

    public String getDateString() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(date);
    }
}
