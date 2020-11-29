package model;

import com.google.gson.JsonObject;

import java.util.List;

public class Game {
    private int id;
    private String name;
    private String type;
    private List<String> cassette;
    private int register;

    public Game(String name, String type, List<String> cassette, int register) {
        this.name = name;
        this.type = type;
        this.cassette = cassette;
        this.register = register;
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

    public void setCassette(List<String> cassette) {
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

    public List<String> getCassette() {
        return cassette;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("name", name);
        json.addProperty("type", type);
        json.addProperty("cassette", cassetteToJson());
        json.addProperty("register", register);
        return json;
    }

    private String cassetteToJson() {
        String result = cassette.get(0);
        for (int i = 1; i < cassette.size(); i++) {
            result += "-" + cassette.get(i);
        }
        return result;
    }


    public int getRegister() {
        return register;
    }
    public void setRegister(int register) {
        this.register = register;
    }

    public boolean hasCassette(String cassette) {
        return this.cassette.contains(cassette);
    }
}

