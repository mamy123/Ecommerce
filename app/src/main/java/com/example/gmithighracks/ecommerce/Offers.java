package com.example.gmithighracks.ecommerce;



public class Offers
{

    public Offers(double price,String endTime,String experience,String username,int id)
    {
        this.price = price;
        this.endTime = endTime;
        this.experience = experience;
        this.username = username;
        this.id = id;

    }


    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private double price;
    String endTime,experience,username;
    int id;

    public double getPrice() {

        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
