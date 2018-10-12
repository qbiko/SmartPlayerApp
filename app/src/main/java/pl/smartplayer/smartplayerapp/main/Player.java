package pl.smartplayer.smartplayerapp.main;

public class Player {
    private long dbId;
    private String firstname;
    private String lastname;
    private int number;
    private int age;
    private int height;
    private int weight;

    public Player(long dbId, String firstname, String lastname, int number, int age, int height, int weight) {
        this.dbId = dbId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.number = number;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    public long getDbId() {
        return dbId;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public int getNumber() {
        return number;
    }

    public int getAge() {
        return age;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }
}
