package io.nuls.api.context;

import io.nuls.api.constant.Constant;
import io.nuls.api.constant.EntityConstant;
import io.nuls.api.server.dao.mapper.leveldb.HistoryLevelDbService;

/**
 * Description: 14天交易历史统计
 * Author: moon
 * Date:  2018/6/5 0005
 */
public class HistoryContext {

    private static HistoryLevelDbService historyLevelDbService = HistoryLevelDbService.getInstance();
    /**
     * 24小时奖励
     */
    public static Long rewardofday = 0L;

    private static String[] historyList = null;

    public static String[] getAll(){
        if(null == historyList){
            historyList = historyLevelDbService.select(Constant.HISTORY_DB_NAME);
        }
        return historyList;
    }

    public static void reset(String[] args){
        if(null != args){
            historyLevelDbService.insert(Constant.HISTORY_DB_NAME,args);
            historyList = args;
        }
    }

}
