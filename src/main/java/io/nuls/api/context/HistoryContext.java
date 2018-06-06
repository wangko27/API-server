package io.nuls.api.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Description: 14天交易历史统计
 * Author: zsj
 * Date:  2018/6/5 0005
 */
public class HistoryContext {
    public static Long rewardofday = 0L;
    private static List<HashMap<String,String>> historyList = new ArrayList<>(14);

    public static void add(HashMap<String,String> map){historyList.add(map);}
    public static void clear(){historyList.clear();}

    public static int getSize(){
        return historyList.size();
    }

    public static List<HashMap<String,String>> getAll(){
        return historyList;
    }

    public static void reset(List<HashMap<String,String>> list){
        clear();
        historyList = list;
    }

}
