package model;

public class Game {
    private int id;
    private String name;
    private String type;
    private String cassette;

    public Game(String name, String type, String cassette) {
        this.name = name;
        this.type = type;
        this.cassette = cassette;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCassette(String cassette) {
        this.cassette = cassette;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCassette() {
        return cassette;
    }


}

