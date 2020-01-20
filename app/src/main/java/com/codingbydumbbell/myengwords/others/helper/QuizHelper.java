package com.codingbydumbbell.myengwords.others.helper;

import android.content.Context;

import com.codingbydumbbell.myengwords.R;
import com.codingbydumbbell.myengwords.others.vo.Quiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizHelper {
    private final int[] quesId = {
            R.array.question1, R.array.question2, R.array.question3, R.array.question4, R.array.question5,
            R.array.question6, R.array.question7, R.array.question8, R.array.question9, R.array.question10,
            R.array.question11, R.array.question12, R.array.question13, R.array.question14, R.array.question15,
            R.array.question16, R.array.question17, R.array.question18, R.array.question19, R.array.question20};
    private final List<Quiz> quizzes;

    public QuizHelper(Context context) {
        quizzes = new ArrayList<>();
        for (int id : quesId) { // 載入題目
            String[] question = context.getResources().getStringArray(id);
            quizzes.add(new Quiz(question));
        }
    }

    public List<Quiz> getQuizzes() {
        Collections.shuffle(quizzes); // 隨機題目
        while (quizzes.size() > 10) quizzes.remove(0); // 讓題目剩下 10 題
        return quizzes;
    }
}
