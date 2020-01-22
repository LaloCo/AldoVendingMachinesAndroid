package com.tsool.appvending;

/**
 * Created by Diego on 26/01/2016.
 */
public class Product {
    public int Posicion;
    public String id;
    public String Name;
    public float Price;
    public int Quatity;
    public int Availability;

    public Product(int posicion, String id, String name, float price,int quatity,int availability) {
        this.Posicion = posicion;
        this.id = id;
        this.Name = name;
        this.Price = price;
        this.Quatity = quatity;
        this.Availability = availability;
    }


}
