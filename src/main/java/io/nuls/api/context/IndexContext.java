package io.nuls.api.context;

import io.nuls.api.constant.Constant;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: zsj
 * Date:  2018/7/4 0004
 */
public class IndexContext {
    private static List<BlockHeader> blocks = new ArrayList<>(Constant.INDEX_BLOCK_LIST_COUNT);
    private static List<Transaction> transactions = new ArrayList<>(Constant.INDEX_TX_LIST_COUNT);


    /*private static Queue<BlockHeader> blockQueue = new LinkedList<BlockHeader>();
    private static Queue<Transaction> transactionsQueue = new LinkedList<Transaction>();

    public static BlockHeader getQueue(){
        return blockQueue.poll();
    }
    public static boolean addQueue(BlockHeader blockHeader){
        return blockQueue.offer(blockHeader);
    }*/


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
}
