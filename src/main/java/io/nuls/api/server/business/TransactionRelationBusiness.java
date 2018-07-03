package io.nuls.api.server.business;

import io.nuls.api.entity.Input;
import io.nuls.api.entity.Transaction;
import io.nuls.api.entity.TransactionRelation;

import io.nuls.api.entity.Utxo;
import io.nuls.api.server.dao.mapper.TransactionRelationMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TransactionRelationBusiness implements BaseService<TransactionRelation, Long> {

    @Autowired
    private TransactionRelationMapper relationMapper;



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
        //2018-07-02修改为批量插入

        List<TransactionRelation> relationList = new ArrayList<>();
        for (String address : addressSet) {
            TransactionRelation key = new TransactionRelation(address, tx.getHash());
            relationList.add(key);
        }
        if(relationList.size() > 0){
            relationMapper.insertByBatch(relationList);
        }
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteByTxHash(String txHash) {
        Searchable searchable = new Searchable();
        searchable.addCondition("tx_hash", SearchOperator.eq, txHash);
        relationMapper.deleteByTxHash(txHash);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public int save(TransactionRelation transactionRelation) {
        return relationMapper.insert(transactionRelation);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public int update(TransactionRelation transactionRelation) {
        return relationMapper.updateByPrimaryKey(transactionRelation);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByKey(Long aLong) {
        return relationMapper.deleteByPrimaryKey(aLong);
    }

    @Override
    public TransactionRelation getByKey(Long aLong) {
        return relationMapper.selectByPrimaryKey(aLong);
    }
}
