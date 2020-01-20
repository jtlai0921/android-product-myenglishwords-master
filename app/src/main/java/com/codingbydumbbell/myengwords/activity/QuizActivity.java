package com.codingbydumbbell.myengwords.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;

import com.codingbydumbbell.myengwords.R;
import com.codingbydumbbell.myengwords.fragment.QuizFragment;
import com.codingbydumbbell.myengwords.others.MyConst;
import com.codingbydumbbell.myengwords.others.UserInfo;
import com.codingbydumbbell.myengwords.others.helper.QuizHelper;
import com.codingbydumbbell.myengwords.others.helper.RankingHelper;
import com.codingbydumbbell.myengwords.others.spec.QuizFragmentListener;
import com.codingbydumbbell.myengwords.others.vo.Quiz;
import com.codingbydumbbell.myengwords.others.vo.Rank;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class QuizActivity extends AppCompatActivity implements QuizFragmentListener, MyConst {

    @BindView(R.id.tv_timer)
    TextView tvTimer;
    @BindView(R.id.tv_score)
    TextView tvScore;
    private List<Quiz> quizzes; // 題目
    private Timer timer; // 計時器
    private int num; // 題號
    private int score; // 得分

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        ButterKnife.bind(this);
        setDefault();
    }

    private void setDefault() {
        quizzes = new QuizHelper(this).getQuizzes(); // 取得題目
        setTimer(); // 設置計時器
        nextQuiz();
    }

    private void nextQuiz() {
        num++;
        if (num <= 10) { // 代表遊戲正在進行
            QuizFragment nextFragment = new QuizFragment();
            Bundle bundle = new Bundle();
            Quiz quiz = quizzes.get(num - 1); // 取得題目，因為 index 是從 0 開始
            quiz.setNum(num); // 將目前題號封裝
            bundle.putSerializable(INTENT_KEY, quiz);
            nextFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, nextFragment).commit();
        } else if (num == 11) { // 代表遊戲已經結束
            Rank r = new Rank(UserInfo.displayName, Integer.parseInt(tvScore.getText().toString())); // 將遊戲狀態封裝成 Rank 物件
            if (RankingHelper.setRank(r)) // 將 Rank 排名，若返回 true 則代表榜上有名，所以要異動 Firebase
                FirebaseDatabase.getInstance()
                        .getReference(FIREBASE_REFERENCE_RANK)
                        .setValue(RankingHelper.getRanks()); // 取得 List<Rank> 物件，為總排名資料
            new AlertDialog.Builder(this)
                    .setTitle("遊戲結束")
                    .setMessage("您的總得分為：" + score)
                    .setPositiveButton("回主畫面", (dialog, which) -> finish())
                    .setNegativeButton("再玩一場", (dialog, which) -> recreate()) // 重啟此 Activity
                    .setCancelable(false) // 讓 Dialog 點擊旁邊無法取消
                    .show();
        }
        timingCounter = DEFAULT_LIMIT_TIME; // 重置時間
    }

    @Override
    public void onButtonClick() {
        nextQuiz();
    }

    @Override
    public void onBingo() {
        score += 10;
        tvScore.setText(String.valueOf(score));
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private int timingCounter;

    private void setTimer() { // 設置 Timer
        timer = new Timer(); // 建立 Timer 物件
        timer.schedule(new TimerTask() {
            @Override
            public void run() { // 因為更動 UI 需要再 UI-Thread
                runOnUiThread(() -> {
                    System.out.println("num:" + num + ", time:" + timingCounter);
                    if (num <= 10) {
                        tvTimer.setText("剩餘時間：" + --timingCounter); // 更動時間
                        if (timingCounter == 0) nextQuiz(); // 當計數器歸 0，則「下一題」
                    } else {
                        tvTimer.setText("剩餘時間：0");
                        timer.cancel();
                        timer.purge();
                    }
                });
            }
        }, 0, 1000); // 參數分別為：執行的任務、延遲開始時間、重覆任務時間，所以這邊的意思是立刻開始 task 任務，並每秒重覆一次
    }

    @Override
    public void onBackPressed() { // 讓返回失效
    }
}