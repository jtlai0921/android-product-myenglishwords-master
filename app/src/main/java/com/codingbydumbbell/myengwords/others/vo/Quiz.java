package com.codingbydumbbell.myengwords.others.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Quiz implements Serializable {

    private static final long serialVersionUID = Quiz.class.hashCode(); // 實作序列化
    private int num;
    private String ques;
    private String ans;
    private List<String> options;

    public Quiz(String[] question) {
        ques = question[0];
        ans = question[1];
        getOptions(question);
    }

    private void getOptions(String[] question) {
        options = new ArrayList<>(Arrays.asList(question)); // 注意： options = Arrays.asList(question) 這樣寫會噴 UnsupportedOperationException
        options.remove(0);
        Collections.shuffle(options);
    }

    public boolean isBingo(String str) {
        return str.equals(ans);
    }

    public String getQues() {
        return ques;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
