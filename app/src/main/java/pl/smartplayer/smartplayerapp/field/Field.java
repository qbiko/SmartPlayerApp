package pl.smartplayer.smartplayerapp.field;

public class Field {
    private long dbId;
    private String name;
    private double[][] latlonAttitudes;

    public Field(long dbId, String name, double[][] latlonAttitudes) {
        this.dbId = dbId;
        this.name = name;
        this.latlonAttitudes = latlonAttitudes;
    }

    public long getDbId() {
        return dbId;
    }

    public String getName() {
        return name;
    }

    public double[][] getLatlonAttitudes() {
        return latlonAttitudes;
    }
}
