package io.nuls.api.server.dto;

import io.nuls.api.entity.Transaction;

/**
 * Description:
 * Author: zsj
 * Date:  2018/6/10 0010
 */
public class TransactionDto extends Transaction {
    private Transaction transaction;
    private Long confirmCount = 0L;
    public TransactionDto(){

    }
    public TransactionDto(Transaction entity){
        this.transaction = entity;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Long getConfirmCount() {
        return confirmCount;
    }

    public void setConfirmCount(Long confirmCount) {
        this.confirmCount = confirmCount;
    }
}
