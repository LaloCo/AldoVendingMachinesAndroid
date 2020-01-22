package com.tsool.appvending;

/**
 * Created by Diego on 22/01/2016.
 */
public class Bodega {
    public String id;
    public String Name;
    public String UnitName;
    public float Availability;
    public float AvailabilityAlert;
    public float Price;
    public int Type;
    public float PurchasePrice;

    public Bodega() {

    }

    public Bodega(int price) {
        Price = price;
    }

    public Bodega(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public float getPrice() {
        return Price;
    }

    public void setPrice(float price) {
        Price = price;
    }
}
