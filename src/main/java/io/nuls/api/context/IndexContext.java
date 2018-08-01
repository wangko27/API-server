package io.nuls.api.context;

import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.Constant;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.Transaction;
import io.nuls.api.utils.StringUtils;

import java.util.*;

/**
 * Description: 缓存首页的区块列表和交易列表和全网共识
 * Author: zsj
 * Date:  2018/7/4 0004
 */
public class IndexContext {
    private static List<BlockHeader> blocks = new ArrayList<>(Constant.INDEX_BLOCK_LIST_COUNT);
    private static List<Transaction> transactions = new ArrayList<>(Constant.INDEX_TX_LIST_COUNT);
    //全网共识信息，每隔10s在链上加载
    private static Map rpcConsensusData = new HashMap();
    //全网共识列表，缓存，用于查询，每隔10s在链上加载
    private static List<LinkedHashMap> agentNodeList = new ArrayList<>();

    /*private static Queue<BlockHeader> blockQueue = new LinkedList<BlockHeader>();
    private static Queue<Transaction> transactionsQueue = new LinkedList<Transaction>();

    public static BlockHeader getQueue(){
        return blockQueue.poll();
    }
    public static boolean addQueue(BlockHeader blockHeader){
        return blockQueue.offer(blockHeader);
    }*/

    public static BlockHeader getBestNewBlock(){
        if(blocks.size()>0){
            return blocks.get(0);
        }
        return null;
    }

    public static List<BlockHeader> getBlockList(){
        return blocks;
    }

    public static List<Transaction> getTransactions(){
        return transactions;
    }

    public static void putBlock(BlockHeader block){
        if(blocks.size() >= Constant.INDEX_BLOCK_LIST_COUNT){
            blocks.remove(blocks.size()-1);
        }
        blocks.add(0,block);

    }
    public static void removeBlock(BlockHeader block){
        blocks.remove(block);
    }
    public static void initBlocks(List<BlockHeader> blocklist){
        if(null != blocklist){
            blocks = blocklist;
        }
    }
    public static void putTransaction(Transaction transaction){
        if(transactions.size() >= Constant.INDEX_TX_LIST_COUNT){
            transactions.remove(transactions.size()-1);
        }
        transactions.add(0,transaction);
    }
    public static void removeTransaction(Transaction transaction){
        transactions.remove(transaction);
    }
    public static void initTransactions(List<Transaction> transactionList){
        if(null != transactionList){
            transactions = transactionList;
        }
    }

    public static void resetRpcConsensusData(Map data){
        rpcConsensusData = data;
    }

    public static Map getRpcConsensusData(){
        return rpcConsensusData;
    }

    public static void resetRpcAgentNodeList(List<LinkedHashMap> data){
        agentNodeList = data;
    }
    public static PageInfo getAgentNodeList(int pageNumber,int pageSize,String agentNode){
        PageInfo<LinkedHashMap> page = new PageInfo<>();
        List<LinkedHashMap> list = new ArrayList<>();
        int start = (pageNumber-1)*pageSize;
        int end = pageNumber*pageSize;
        if(agentNodeList.size()<pageSize){
            end = agentNodeList.size();
        }else{
            if(end > (agentNodeList.size()-pageSize)){
                end = agentNodeList.size()-pageSize;
            }
        }

        List<LinkedHashMap> tempData = new ArrayList<>();
        if(StringUtils.isNotBlank(agentNode)){
            for(LinkedHashMap hashMap: tempData){
                if(hashMap.get("agentHash").toString().endsWith(agentNode)){
                    tempData.add(hashMap);
                }
            }
        }else{
            tempData = agentNodeList;
        }
        for(int i =start;i<end;i++){
            list.add(tempData.get(i));
        }
        page.setList(list);
        page.setSize(tempData.size());
        page.setPageNum(pageNumber);
        return page;
    }

    public static LinkedHashMap getNodeByAgentHash(String agentHash){
        for(LinkedHashMap map:agentNodeList){
            if(map.get("agentHash").toString().equals(agentHash)){
                return map;
            }
        }
        return null;
    }


}
