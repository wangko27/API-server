package io.nuls.api.context;

/**
 * Description: 14天交易历史统计
 * Author: zsj
 * Date:  2018/6/5 0005
 */
public class HistoryContext {
    /**
     * 24小时奖励
     */
    public static Long rewardofday = 0L;

    private static String[] historyList = new String[14];

    public static void add(String data,int index){historyList[index]=data;}

    public static String[] getAll(){
        return historyList;
    }

    public static void reset(String[] args){
        historyList = args;
    }

}
