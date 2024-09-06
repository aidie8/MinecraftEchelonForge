package org.aidie8.minecraftechelonforge.Core;

public class LeaderboardEntry {

    private String uid;
    private int score;
    private int position;

    private String name;


    public LeaderboardEntry(String uid, int score, int position,String name)
    {
        this.uid = uid;
        this.score = score;
        this.position = position;
        this.name = name;
    }
    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public String getUid() {
        return uid;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
