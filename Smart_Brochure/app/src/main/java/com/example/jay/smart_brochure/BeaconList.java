package com.example.jay.smart_brochure;

/**
 * Created by Jay on 2015-05-24.
 */
public class BeaconList {

    private String address;
    private String name;
    public BeaconList() {
        super();
    }
    public BeaconList(String address, String name) {
        super();
        this.address = address;
        this.name = name;
    }
    public String getAddress(){
        return address;
    }

    public void setAddress(String address){
        this.address = address;
    }

}
