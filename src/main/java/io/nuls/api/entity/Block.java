package io.nuls.api.entity;

import java.util.List;

public class Block {

    private BlockHeader header;

    private List<Transaction> txList;

    public List<Transaction> getTxList() {
        return txList;
    }

    public void setTxList(List<Transaction> txList) {
        this.txList = txList;
    }

    public BlockHeader getHeader() {
        return header;
    }

    public void setHeader(BlockHeader header) {
        this.header = header;
    }
}