package pl.smartplayer.smartplayerapp.main;

public class Player {
    private long dbId;
    private String firstName;
    private String lastName;
    private int number;
    private int age;
    private int height;
    private int weight;

    Player(long dbId, String firstName, String lastName, int number, int age, int height, int weight) {
        this.dbId = dbId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.number = number;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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
