package com.example.jay.smart_brochure;

/**
 * Created by Jay on 2015-05-19.
 */
public class History {

    private String address;
    private String name;
    private String code;
    public History() {
        super();
    }
    public History(String address, String name, String code) {
        super();
        this.address = address;
        this.name = name;
        this.code = code;
    }

    public String getAddress(){
        return address;
    }

    public String getName(){
        return name;
    }

    public String getCode() { return code; }

    public void setAddress(String address){
        this.address = address;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCode(String code) { this.code = code; }

}
