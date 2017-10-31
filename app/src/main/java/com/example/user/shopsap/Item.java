package com.example.user.shopsap;

/**
 * Created by user on 24.9.2017.
 */

public class Item {


    public String itemname,description,price,count,barcode,type;


    public Item (String itemname,String description,String price,String count,String barcode,String type)
    {
        this.itemname=itemname;
        this.description=description;
        this.price=price;
        this.count=count;
        this.barcode=barcode;
        this.type=type;
    }

    public Item()
    {
        this.itemname=null;
        this.description=null;
        this.price=null;
        this.count=null;
        this.barcode=null;
        this.type=null;
    }


    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getPrice() {
        return price;
    }

    public String getCount() {
        return count;
    }

    public String getDescription() {
        return description;
    }

    public String getItemname() {
        return itemname;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
