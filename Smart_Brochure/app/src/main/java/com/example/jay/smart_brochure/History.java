package com.example.jay.smart_brochure;

/**
 * Created by Jay on 2015-05-19.
 */
public class History {

    private String address;
    private String name;
    public History() {
        super();
    }
    public History(String address, String name) {
        super();
        this.address = address;
        this.name = name;
    }

    public String getAddress(){
        return address;
    }

    public String getName(){
        return name;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public void setName(String name){
        this.name = name;
    }

}
