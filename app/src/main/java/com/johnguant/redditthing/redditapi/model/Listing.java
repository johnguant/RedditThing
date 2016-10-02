package com.johnguant.redditthing.redditapi.model;

import java.util.List;

public class Listing<E> {

    private String kind;
    private ListingData data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public ListingData getData() {
        return data;
    }

    public void setData(ListingData data) {
        this.data = data;
    }

    public class ListingData {
        String before;
        String after;
        String modhash;
        List<Thing<E>> children;

        public String getBefore() {
            return before;
        }

        public void setBefore(String before) {
            this.before = before;
        }

        public String getAfter() {
            return after;
        }

        public void setAfter(String after) {
            this.after = after;
        }

        public String getModhash() {
            return modhash;
        }

        public void setModhash(String modhash) {
            this.modhash = modhash;
        }

        public List<Thing<E>> getChildren() {
            return children;
        }

        public void setChildren(List<Thing<E>> children) {
            this.children = children;
        }
    }
}
