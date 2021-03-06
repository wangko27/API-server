package io.nuls.api.context;

import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.Constant;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.Transaction;
import io.nuls.api.server.dto.AgentDto;
import io.nuls.api.utils.StringUtils;
import io.nuls.api.utils.comparator.AgentComparator;

import java.util.*;

/**
 * Description: 缓存首页的区块列表和交易列表和全网共识
 * Author: moon
 * Date:  2018/7/4 0004
 */
public class IndexContext {
    /**
     * 区块链浏览器首页区块列表缓存
     */
    private static List<BlockHeader> blocks = new ArrayList<>(Constant.INDEX_BLOCK_LIST_COUNT);
    /**
     * 区块链浏览器首页，交易缓存
     */
    private static List<Transaction> transactions = new ArrayList<>(Constant.INDEX_TX_LIST_COUNT);
    /**
     * 全网共识信息，每隔10s在链上加载
     */
    private static Map rpcConsensusData = new HashMap();
    /**
     * 全网节点列表，缓存，用于查询，每隔10s在链上加载
     */
    private static List<AgentDto> agentNodeList = new ArrayList<>();

    public static List<AgentDto> getAgentNodeList(){
        return agentNodeList;
    }

    /**
     * 从缓存中获取最新块
     * @return
     */
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

    public static void resetRpcAgentNodeList(List<AgentDto> data){
        agentNodeList = data;
    }
    public static PageInfo<AgentDto> getAgentNodeList(int pageNumber,int pageSize,String keyword,Integer status,int sort){
        PageInfo<AgentDto> page = new PageInfo<>();
        int start = (pageNumber-1)*pageSize;
        int end = start+pageSize;


        List<AgentDto> tempData = new ArrayList<>();
        for(AgentDto hashMap: agentNodeList){
            if(StringUtils.isNotBlank(keyword)){
                //搜索
                keyword = keyword.toUpperCase();
                boolean b = hashMap.getAgentId().indexOf(keyword) >= 0;
                b = b || hashMap.getAgentAddress().toUpperCase().equals(keyword) || hashMap.getPackingAddress().toUpperCase().equals(keyword);
                if (StringUtils.isNotBlank(hashMap.getAgentName())) {
                    b = b || hashMap.getAgentName().toUpperCase().indexOf(keyword) >= 0;
                }
                if (b) {
                    if(null != status){
                        if(hashMap.getStatus()==status){
                            tempData.add(hashMap);
                        }else{
                            continue;
                        }
                    }else{
                        tempData.add(hashMap);
                    }
                }
                continue;
            }else{
                if(null != status){
                    if(hashMap.getStatus()==status){
                        tempData.add(hashMap);
                    }else{
                        continue;
                    }
                }else{
                    tempData.add(hashMap);
                }
            }
            //tempData.add(hashMap);
        }
        if(tempData.size()<end){
            end = tempData.size();
        }
        Collections.sort(tempData, AgentComparator.getInstance(sort));
        List<AgentDto> list = new ArrayList<>();
        for(int i =start;i<end;i++){
            list.add(tempData.get(i));
        }
        page.setList(list);
        page.setTotal(tempData.size());
        page.setPageSize(pageSize);
        page.setPageNum(pageNumber);
        return page;
    }

    public static AgentDto getNodeByAgentHash(String agentHash){
        for(AgentDto map:agentNodeList){
            if(map.getTxHash().equals(agentHash)){
                return map;
            }
        }
        return null;
    }

    public static AgentDto getNodeByAgentAddress(String agentAddress){
        for(AgentDto map:agentNodeList){
            if(map.getAgentAddress().equals(agentAddress)){
                return map;
            }
        }
        return null;
    }


}
