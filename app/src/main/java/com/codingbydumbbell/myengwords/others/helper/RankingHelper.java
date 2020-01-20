package com.codingbydumbbell.myengwords.others.helper;

import com.codingbydumbbell.myengwords.others.vo.Rank;

import java.util.Collections;
import java.util.List;

public class RankingHelper {
    private static List<Rank> ranks;

    public static void setRanksList(List<Rank> ranks) {
        RankingHelper.ranks = ranks;
    }

    public static List<Rank> getRanks() {
        Collections.sort(ranks);
        return ranks;
    }

    public static boolean setRank(Rank rank) { // 記得要先到 firebase 建立 ranks 的結構
        if (ranks.get(4).getScore() > rank.getScore()) return false; // 代表分數尚未達到前五名
        else { // 代表分數達到前五名
            ranks.remove(4);
            ranks.add(rank);
            Collections.sort(ranks); // 將 Ranks 排列，因為我有實作 Comparable
            return true;
        }
    }
}
