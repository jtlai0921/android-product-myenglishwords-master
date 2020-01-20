package com.codingbydumbbell.myengwords.others.vo;

import androidx.annotation.NonNull;

public class Rank implements Comparable<Rank> {

    String displayName;
    int score;

    public Rank() { // firebase 需要抓資料需要 no-argument constructor
    }

    public Rank(String displayName, int score) {
        this.displayName = displayName;
        this.score = score;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getScore() {
        return score;
    }

    @Override
    public int compareTo(@NonNull Rank o) {
        return -Double.compare(this.getScore(), o.getScore()); // 因為要大到小，所以別忘了負號
    }
}
