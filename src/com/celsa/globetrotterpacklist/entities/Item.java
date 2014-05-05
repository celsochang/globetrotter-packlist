package com.celsa.globetrotterpacklist.entities;

public class Item {

    private long id;
    private String name;
    private byte[] image;
    private int status;
    private int order;

    public Item(long id, String name, byte[] image, int status, int order) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.status = status;
        this.order = order;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}