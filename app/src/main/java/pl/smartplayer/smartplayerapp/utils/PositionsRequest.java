package pl.smartplayer.smartplayerapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PositionsRequest {
    private Point2D point;
    private long number;
    private Date date;

    public PositionsRequest(Point2D point, long number, Date date) {
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

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getDateString() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(date);
    }
}
