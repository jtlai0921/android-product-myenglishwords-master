package com.codingbydumbbell.myengwords;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codingbydumbbell.myengwords.activity.QuizActivity;
import com.codingbydumbbell.myengwords.others.MyConst;
import com.codingbydumbbell.myengwords.others.UserInfo;
import com.codingbydumbbell.myengwords.others.helper.RankingHelper;
import com.codingbydumbbell.myengwords.others.vo.Rank;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, MyConst {

    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.textView_player)
    TextView textViewPlayer;
    @BindView(R.id.tv_displayname)
    TextView tvDisplayname;
    @BindView(R.id.tv_ranking)
    TextView tvRanking;
    @BindView(R.id.textView_rank)
    TextView textViewRank;
    @BindView(R.id.btn_logout)
    Button btnLogout;
    @BindView(R.id.btn_start)
    Button btnStart;
    private static final int RC_SIGN_IN = 108;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser(); // 取得目前的 firebase-user
        if (user != null) { // 代表不為 0
            UserInfo.displayName = user.getDisplayName(); // 取得 displayName
            UserInfo.uid = user.getUid(); // 取得 uid
            tvDisplayname.setText("暱稱：" + UserInfo.displayName);
            FirebaseDatabase.getInstance()
                    .getReference(FIREBASE_REFERENCE_USERS)
                    .child(UserInfo.uid)
                    .child(FIREBASE_REFERENCE_DISPLAYNAME).setValue(UserInfo.displayName);

            FirebaseDatabase.getInstance()
                    .getReference(FIREBASE_REFERENCE_RANK)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<Rank> ranks = new ArrayList<>();
                            // 無法直接轉型為 List<Rank>，必須使用 for 迴圈
                            for (DataSnapshot snapshot : dataSnapshot.getChildren())
                                ranks.add(snapshot.getValue(Rank.class)); // 因為這行的關係，所以 Rank 需要一個空的建構子
                            Collections.sort(ranks); // 將 Ranks 排列，因為我有實作 Comparable
                            RankingHelper.setRanksList(ranks); // 將 ranks 放入
                            for (int i = 0; i < ranks.size(); i++) {
                                if (UserInfo.displayName.equals(ranks.get(i).getDisplayName())) ;
                                tvRanking.setText("最高名次：" + ++i);
                                break;
                            }
                            RankAdapter adapter = new RankAdapter();
                            recycler.setHasFixedSize(true);
                            recycler.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            recycler.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        } else startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build()
                        ))
                        .setTheme(R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                        .setIsSmartLockEnabled(false)
                        .build()
                , RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(this); // 加入傾聽
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(this); // 移除傾聽
    }

    @OnClick({R.id.btn_logout, R.id.btn_start})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_logout:
                auth.signOut();
                break;
            case R.id.btn_start:
                startActivity(new Intent(this, QuizActivity.class));
                break;
        }
    }

    public class RankAdapter extends RecyclerView.Adapter<RankAdapter.RankHolder> {
        List<Rank> ranks = RankingHelper.getRanks();

        @NonNull
        @Override
        public RankHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.row, viewGroup, false);
            return new RankHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RankHolder rankHolder, int i) {
            Rank rank = ranks.get(i);
            rankHolder.rankText.setText("No. " + ++i);
            rankHolder.nameText.setText("暱稱：" + rank.getDisplayName());
            rankHolder.scoreText.setText("分數：" + String.valueOf(rank.getScore()));
        }

        @Override
        public int getItemCount() {
            return ranks.size();
        }

        public class RankHolder extends RecyclerView.ViewHolder {
            TextView rankText;
            TextView nameText;
            TextView scoreText;

            public RankHolder(@NonNull View itemView) {
                super(itemView);
                rankText = itemView.findViewById(R.id.textView_rank);
                nameText = itemView.findViewById(R.id.textView_name);
                scoreText = itemView.findViewById(R.id.textView_score);
            }
        }
    }
}
