package com.codingbydumbbell.myengwords.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codingbydumbbell.myengwords.R;
import com.codingbydumbbell.myengwords.others.MyConst;
import com.codingbydumbbell.myengwords.others.spec.QuizFragmentListener;
import com.codingbydumbbell.myengwords.others.vo.Quiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class QuizFragment extends Fragment implements MyConst {

    @BindView(R.id.tv_quesNum)
    TextView tvQuesNum;
    @BindView(R.id.tv_ques)
    TextView tvQues;
    @BindView(R.id.btn_A)
    Button btnA;
    @BindView(R.id.btn_B)
    Button btnB;
    @BindView(R.id.btn_C)
    Button btnC;
    @BindView(R.id.btn_D)
    Button btnD;
    Unbinder unbinder;
    private QuizFragmentListener mListener; // 監聽器
    private Quiz quiz;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quiz = (Quiz) getArguments().getSerializable(INTENT_KEY); // 取得 Quiz 物件
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        unbinder = ButterKnife.bind(this, view);
        setView();
        return view;
    }

    private void setView() {
        tvQuesNum.setText(String.format("第 %d 題", quiz.getNum())); // 設定題號
        tvQues.setText(quiz.getQues()); // 設定問題
        Button[] buttons = {btnA, btnB, btnC, btnD};
        for (int i = 0; i < buttons.length; i++)
            buttons[i].setText(buttons[i].getText().toString() + quiz.getOptions().get(i)); // 設定選項
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_A, R.id.btn_B, R.id.btn_C, R.id.btn_D})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_A:
                isBingo(btnA);
                break;
            case R.id.btn_B:
                isBingo(btnB);
                break;
            case R.id.btn_C:
                isBingo(btnC);
                break;
            case R.id.btn_D:
                isBingo(btnD);
                break;
        }
        mListener.onButtonClick(); // 通知 Activity 按鈕已經按下
    }

    private void isBingo(Button btn) {
        if (quiz.isBingo(btn.getText().toString().substring(3))) // 判斷是否回答正確
            mListener.onBingo(); // 通知 Activity 答對了
    }

    @Override
    public void onAttach(Context context) {
        mListener = (QuizFragmentListener) context; // 監聽器的設置
        super.onAttach(context);
    }
}




