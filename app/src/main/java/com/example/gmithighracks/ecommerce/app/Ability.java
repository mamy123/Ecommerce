package com.example.gmithighracks.ecommerce.app;

/**
 * Created by nikos on 18-Sep-15.
 */
public class Ability {
    public int id;
    public String name;
    public String description;

    public Ability(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {

        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
