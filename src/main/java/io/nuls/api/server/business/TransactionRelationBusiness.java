package io.nuls.api.server.business;

import io.nuls.api.entity.Input;
import io.nuls.api.entity.Transaction;
import io.nuls.api.entity.TransactionRelationKey;
import io.nuls.api.entity.Utxo;
import io.nuls.api.server.dao.mapper.TransactionRelationMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class TransactionRelationBusiness implements BaseService<TransactionRelationKey, TransactionRelationKey> {

    @Autowired
    private TransactionRelationMapper relationMapper;

    @Override
    public int save(TransactionRelationKey transactionRelationKey) {
        return relationMapper.insert(transactionRelationKey);
    }

    @Override
    public int update(TransactionRelationKey transactionRelationKey) {
        return 0;
    }

    @Override
    public int deleteByKey(TransactionRelationKey transactionRelationKey) {
        return relationMapper.deleteByPrimaryKey(transactionRelationKey);
    }

    @Override
    public TransactionRelationKey getByKey(TransactionRelationKey transactionRelationKey) {
        return null;
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveTxRelation(Transaction tx) {
        Set<String> addressSet = new HashSet<>();
        if (tx.getInputs() != null) {
            for (Input input : tx.getInputs()) {
                addressSet.add(input.getAddress());
            }
        }
        if (tx.getOutputs() != null) {
            for (Utxo utxo : tx.getOutputs()) {
                addressSet.add(utxo.getAddress());
            }
        }
        for (String address : addressSet) {
            TransactionRelationKey key = new TransactionRelationKey(address, tx.getHash());
            relationMapper.insert(key);
        }
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteByTxHash(String txHash) {
        Searchable searchable = new Searchable();
        searchable.addCondition("tx_hash", SearchOperator.eq, txHash);
        relationMapper.deleteByTxHash(txHash);
    }
}
