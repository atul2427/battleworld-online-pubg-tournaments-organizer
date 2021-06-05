package com.skycoder.pubg.model;

public class StatisticsPojo {
    private String id, title, time;
    private int entryFee, prize;

    public StatisticsPojo() {
    }

    public StatisticsPojo(String id, String title, String time, int entryFee, int prize) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.entryFee = entryFee;
        this.prize = prize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(int entryFee) {
        this.entryFee = entryFee;
    }

    public int getPrize() {
        return prize;
    }

    public void setPrize(int prize) {
        this.prize = prize;
    }
}
