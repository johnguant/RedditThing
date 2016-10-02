package com.johnguant.redditthing.redditapi.model;

public class Thing<E> {
    private String id;
    private String name;
    private String kind;
    private E data;

    public Thing(String kind, E data){
        this.kind = kind;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }
}
